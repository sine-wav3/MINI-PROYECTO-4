package controlador;

import modelo.memento.JuegoMemento;
import java.util.*;
import modelo.juego.Jugador;
import modelo.carta.Carta;
import modelo.carta.Monstruo;
import modelo.carta.AcesCoup;
import modelo.carta.ChangeOfHeart;
import modelo.carta.DarkHole;
import modelo.carta.Hinotama;
import modelo.carta.MirrorForce;
import modelo.carta.PotOfGreed;
import modelo.carta.Raigeki;
import modelo.carta.SakuretsuArmor;
import modelo.carta.StandarOfCourage;
import modelo.carta.TyphoonOfMagicalSpace;
import modelo.efecto.Activable;
import modelo.efecto.BoostAtk;
import vista.VistaJuego;

public class JuegoControlador {

    private Jugador j1, j2;
    private boolean turnoJ1;

    private JuegoMemento ultimoEstado;
    private boolean haRobadoEsteTurno      = false;
    private boolean haJugadoCartaEsteTurno = false;
    private boolean haAtacadoEsteTurno     = false;
    private boolean primerTurnoRealizado   = false;
    private Queue<String> eventos = new LinkedList<>();
    private Set<String> cartasUtilizadas =
        new HashSet<>();

    private List<VistaJuego> vistas = new ArrayList<>();

    public JuegoControlador() {
        inicializarJuego();
    }
    ////////////////////////////////////////
    public void guardarPartida() {

        ultimoEstado = new JuegoMemento(
            j1.getLp(),
            j2.getLp(),
            turnoJ1,
            j1.getMano(),
            j2.getMano(),
            j1.getCampo(),
            j2.getCampo()
        );

        notificarMensaje("Partida guardada.");
    }

    public void cargarPartida() {

        if (ultimoEstado == null) {
            notificarMensaje(
                "No hay partida guardada."
            );
            return;
        }

        j1.setLp(
            ultimoEstado.getLpJ1()
        );

        j2.setLp(
            ultimoEstado.getLpJ2()
        );

        j1.setMano(
            ultimoEstado.getManoJ1()
        );

        j2.setMano(
            ultimoEstado.getManoJ2()
        );

        j1.setCampo(
            ultimoEstado.getCampoJ1()
        );

        j2.setCampo(
            ultimoEstado.getCampoJ2()
        );

        turnoJ1 =
            ultimoEstado.isTurnoJ1();

        notificarVistas();
        notificarMensaje(
            "Partida restaurada."
        );
    }

    /////////////////////////////////// Para la parte de guardar y cargar partidas

    private void registrarEvento(String evento) {
        eventos.offer(evento);

        if (eventos.size() > 50) {
            eventos.poll();
        }
    }

    public Queue<String> getEventos() {
        return eventos;
    }

    public void agregarVista(VistaJuego vista) {
        vistas.add(vista);
    }

    private void inicializarJuego() {
        List<Carta> pool = new ArrayList<>();

        String[] nombresMonstruos = {
            "Mago Oscuro", "Dragón Blanco", "Exodia", "Blue-Eyes", "Red-Eyes",
            "Curse of Dragon", "Gaia", "Summoned Skull", "Celtic Guardian", "Dark Magician Girl",
            "Jinzo", "Insect Queen", "Morphing Jar", "Sangan", "Witch of the Black Forest",
            "Kuriboh", "Man-Eater Bug", "Cyber Dragon", "Elemental HERO", "Neo-Spacian",
            "Gravekeeper's", "Vampire Lord", "Zombie Master", "Ryko", "Lyla",
            "Judgment Dragon", "Celestia", "Wulf", "Ehren", "Gorz"
        };

        for (int i = 0; i < 30; i++) {
            int nivel = 1 + (i % 12);
            int atk   = 500 + (i * 80);
            int def   = 500 + (i * 60);
            pool.add(new Monstruo(
                nombresMonstruos[i % nombresMonstruos.length] + " " + (i + 1),
                atk, def, nivel));
        }

        pool.add(new PotOfGreed());      pool.add(new PotOfGreed());
        pool.add(new BoostAtk());        pool.add(new AcesCoup());
        pool.add(new DarkHole());        pool.add(new Hinotama());
        pool.add(new ChangeOfHeart());   pool.add(new Raigeki());
        pool.add(new StandarOfCourage()); pool.add(new TyphoonOfMagicalSpace());

        for (int i = 0; i < 5; i++) pool.add(new MirrorForce());
        for (int i = 0; i < 5; i++) pool.add(new SakuretsuArmor());

        Collections.shuffle(pool);

        Stack<Carta> mazo1 = new Stack<>();
        Stack<Carta> mazo2 = new Stack<>();
        for (int i = 0; i < 25; i++) {
            mazo1.push(pool.remove(0));
            mazo2.push(pool.remove(0));
        }

        j1 = new Jugador("Jugador 1", mazo1);
        j2 = new Jugador("Jugador 2", mazo2);

        for (int i = 0; i < 5; i++) { j1.robarCarta(); j2.robarCarta(); }

        turnoJ1 = new Random().nextBoolean();
    }

    public void setNombres(String nombre1, String nombre2) {
        j1.setNombre(nombre1);
        j2.setNombre(nombre2);
    }

    public void onRobar() {
        if (haRobadoEsteTurno) return;
        Jugador actual = getActual();
        if (actual.isMazoVacio()) {
            notificarMensaje(actual.getNombre() + " no tiene cartas. ¡Pierde!");
            notificarGanador(getOponente().getNombre());
            return;
        }
        actual.robarCarta();
        haRobadoEsteTurno = true;
        notificarMensaje(actual.getNombre() + " roba una carta.");
        notificarVistas();
    }

    public void onJugarCarta(int index) {
        if (haJugadoCartaEsteTurno) {
            notificarMensaje("Ya jugaste una carta este turno.");
            return;
        }
        if (!haRobadoEsteTurno) onRobar();

        Jugador actual   = getActual();
        Jugador oponente = getOponente();

        if (index < 0 || index >= actual.getMano().size()) return;

        Carta carta = actual.getMano().get(index);

        cartasUtilizadas.add(  //se modifica esto para el diseño de memento, se guarda el nombre de la carta que se jugp    
            carta.getNombre());

        if (carta instanceof Monstruo) {
            Monstruo m = (Monstruo) carta;
            int necesarios = m.getNivel() >= 7 ? 2 : m.getNivel() >= 4 ? 1 : 0;

            if (necesarios > 0) {
                if (actual.getCampo().size() < necesarios) {
                    notificarMensaje("Necesitas " + necesarios +
                        " sacrificio(s) para invocar " + m.getNombre());
                    return;
                }
                int[] sacrificios = pedirSacrificios(necesarios, actual);
                if (sacrificios == null) return;
                Arrays.sort(sacrificios);
                for (int i = sacrificios.length - 1; i >= 0; i--) {
                    Monstruo sac = actual.getCampo().remove(sacrificios[i]);
                    notificarMensaje("Sacrificaste: " + sac.getNombre());
                }
            }

            actual.getMano().remove(index);
            actual.getCampo().add(m);
            notificarMensaje(actual.getNombre() + " invoca " + m.getNombre());

        } else if (carta instanceof Activable) {
            actual.getMano().remove(index);
            ((Activable) carta).activar(actual, oponente);
            notificarMensaje(actual.getNombre() + " activa " + carta.getNombre());
        }

        haJugadoCartaEsteTurno = true;
        notificarVistas();
        verificarVictoria();
    }

    public void onAtacar(int idxAtacante, int idxDefensor) {
        if (!primerTurnoRealizado) {
            notificarMensaje("El primer jugador no puede atacar en su primer turno.");
            return;
        }
        if (haAtacadoEsteTurno) {
            notificarMensaje("Ya atacaste este turno.");
            return;
        }

        Jugador atacante = getActual();
        Jugador defensor = getOponente();

        if (atacante.getCampo().isEmpty()) {
            notificarMensaje("No tienes monstruos para atacar.");
            return;
        }

        Monstruo atkM = atacante.getCampo().get(idxAtacante);

        if (defensor.getCampo().isEmpty()) {
            defensor.recibirDano(atkM.getAtk());
            notificarMensaje("¡ATAQUE DIRECTO! " + atkM.getNombre() +
                " causa " + atkM.getAtk() + " de daño.");
        } else {
            Monstruo defM = defensor.getCampo().get(idxDefensor);
            if (atkM.getAtk() > defM.getDef()) {
                int diff = atkM.getAtk() - defM.getDef();
                defensor.getCampo().remove(idxDefensor);
                defensor.recibirDano(diff);
                notificarMensaje(atkM.getNombre() + " destruye a " +
                    defM.getNombre() + ". Daño: " + diff);
            } else {
                notificarMensaje(atkM.getNombre() + " no pudo vencer a " + defM.getNombre());
            }
        }

        haAtacadoEsteTurno = true;
        notificarVistas();
        verificarVictoria();
    }

    public void onCambiarModo(int index) {
        Jugador actual = getActual();
        if (index < 0 || index >= actual.getCampo().size()) return;
        Monstruo m = actual.getCampo().get(index);
        if (!m.puedeCambiarModo()) {
            notificarMensaje(m.getNombre() + " ya cambió de modo este turno.");
            return;
        }
        m.cambiarModo();
        notificarMensaje(m.getNombre() + " ahora está en " +
            (m.isEnAtaque() ? "ATAQUE" : "DEFENSA"));
        notificarVistas();
    }

    public void onPasarTurno() {
        turnoJ1 = !turnoJ1;
        haRobadoEsteTurno      = false;
        haJugadoCartaEsteTurno = false;
        haAtacadoEsteTurno     = false;
        primerTurnoRealizado   = true;

        for (Monstruo m : j1.getCampo()) m.resetCambio();
        for (Monstruo m : j2.getCampo()) m.resetCambio();

        notificarMensaje("Turno de " + getActual().getNombre());
        notificarVistas();
        onRobar();
    }

    private int[] pedirSacrificios(int cantidad, Jugador jugador) {
        String[] opciones = new String[jugador.getCampo().size()];
        for (int i = 0; i < jugador.getCampo().size(); i++) {
            Monstruo m = jugador.getCampo().get(i);
            opciones[i] = m.getNombre() + " ATK:" + m.getAtk();
        }
        int[] resultado = new int[cantidad];
        for (int i = 0; i < cantidad; i++) {
            int idx = vistas.get(0).pedirEleccion(
                "Elige sacrificio " + (i + 1) + " de " + cantidad, opciones);
            if (idx < 0) return null;
            resultado[i] = idx;
        }
        return resultado;
    }

    private void verificarVictoria() {
        String ganador = null;
        if (j1.getLp() <= 0)                              ganador = j2.getNombre();
        else if (j2.getLp() <= 0)                         ganador = j1.getNombre();
        else if (j1.isMazoVacio() && j1.getMano().isEmpty()) ganador = j2.getNombre();
        else if (j2.isMazoVacio() && j2.getMano().isEmpty()) ganador = j1.getNombre();
        if (ganador != null) notificarGanador(ganador);
    }

    private void notificarVistas()            { for (VistaJuego v : vistas) v.actualizar(j1, j2, turnoJ1); }
    private void notificarMensaje(String msg) { //se modifica esto para que la queue almacene el historial
        registrarEvento(msg);
        for (VistaJuego v : vistas) {
            v.mostrarMensaje(msg);
        }
    }
    private void notificarGanador(String n)   { for (VistaJuego v : vistas) v.mostrarGanador(n); }

    public Jugador getActual()   { return turnoJ1 ? j1 : j2; }
    public Jugador getOponente() { return turnoJ1 ? j2 : j1; }
    
    public Set<String> getCartasUtilizadas() {
        return cartasUtilizadas;
    }

}