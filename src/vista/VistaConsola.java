package vista;

import controlador.JuegoControlador;
import java.util.List;
import java.util.Scanner;
import modelo.carta.Carta;
import modelo.carta.Monstruo;
import modelo.juego.Jugador;

public class VistaConsola implements VistaJuego {

    private final JuegoControlador controlador;
    private final Scanner sc;
    private boolean juegoTerminado = false;

    public VistaConsola(JuegoControlador controlador) {
        this.controlador = controlador;
        this.sc = new Scanner(System.in);
    }

    // VistaJuego — OUTPUT (notificaciones del Controller)

    @Override
    public void actualizar(Jugador j1, Jugador j2, boolean turnoJ1) {
        System.out.println("\n===== ESTADO =====");
        System.out.println(j1.getNombre() + " LP:" + j1.getLp() +
            " | Campo:" + j1.getCampo().size() +
            " | Mano:" + j1.getMano().size());
        System.out.println(j2.getNombre() + " LP:" + j2.getLp() +
            " | Campo:" + j2.getCampo().size() +
            " | Mano:" + j2.getMano().size());
        System.out.println("Turno: " + (turnoJ1 ? j1.getNombre() : j2.getNombre()));
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println("[LOG] " + mensaje);
    }

    @Override
    public void mostrarGanador(String nombreGanador) {
        System.out.println("\n¡" + nombreGanador.toUpperCase() + " GANA EL DUELO!");
        juegoTerminado = true;
    }

    @Override
    public int pedirEleccion(String titulo, String[] opciones) {
        System.out.println(titulo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println(i + ". " + opciones[i]);
        }
        return Integer.parseInt(sc.nextLine().trim());
    }

    // Aquí podemos jugar en la terminal también.
  

    public void iniciar() {
        System.out.println("=== YU-GI-OH! TERMINAL ===");
        controlador.onRobar();

        while (!juegoTerminado) {
            turno();
        }
    }

    private void turno() {
        Jugador actual = controlador.getActual();

        System.out.println("\n-- Mano de " + actual.getNombre() + " --");
        List<Carta> mano = actual.getMano();
        for (int i = 0; i < mano.size(); i++) {
            Carta c = mano.get(i);
            String extra = (c instanceof Monstruo m)
                ? " Nv." + m.getNivel() + " ATK:" + m.getAtk() + " DEF:" + m.getDef()
                : " [Magia/Trampa]";
            System.out.println(i + ". " + c.getNombre() + extra);
        }

        System.out.println("\n1. Jugar carta  2. Atacar  3. Cambiar modo  4. Pasar turno 5.Guardar partida 6.Cargar partida");
        System.out.print("> ");

        switch (sc.nextLine().trim()) {
            case "1" -> accionJugarCarta();
            case "2" -> accionAtacar();
            case "3" -> accionCambiarModo();
            case "4" -> controlador.onPasarTurno();
            case "5" -> controlador.onGuardar();
            case "6" -> controlador.onCargar();
            default  -> System.out.println("Opción no válida.");
        }
    }

    private void accionJugarCarta() {
        System.out.print("Índice de carta: ");
        try {
            controlador.onJugarCarta(Integer.parseInt(sc.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("Índice inválido.");
        }
    }

    private void accionAtacar() {
        Jugador atacante = controlador.getActual();
        Jugador defensor = controlador.getOponente();

        if (atacante.getCampo().isEmpty()) { System.out.println("Sin monstruos."); return; }

        System.out.println("Tu campo:");
        for (int i = 0; i < atacante.getCampo().size(); i++) {
            Monstruo m = atacante.getCampo().get(i);
            System.out.println(i + ". " + m.getNombre() + " ATK:" + m.getAtk());
        }
        System.out.print("Atacante [índice]: ");

        try {
            int idxAtk = Integer.parseInt(sc.nextLine().trim());
            if (defensor.getCampo().isEmpty()) {
                controlador.onAtacar(idxAtk, -1);
                return;
            }
            System.out.println("Campo enemigo:");
            for (int i = 0; i < defensor.getCampo().size(); i++) {
                Monstruo m = defensor.getCampo().get(i);
                System.out.println(i + ". " + m.getNombre()
                    + " [" + (m.isEnAtaque() ? "ATK" : "DEF") + "]"
                    + " ATK:" + m.getAtk() + " DEF:" + m.getDef());
            }
            System.out.print("Defensor [índice]: ");
            controlador.onAtacar(idxAtk, Integer.parseInt(sc.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("Índice inválido.");
        }
    }

    private void accionCambiarModo() {
        Jugador actual = controlador.getActual();
        if (actual.getCampo().isEmpty()) { System.out.println("Sin monstruos."); return; }

        System.out.println("Tu campo:");
        for (int i = 0; i < actual.getCampo().size(); i++) {
            Monstruo m = actual.getCampo().get(i);
            System.out.println(i + ". " + m.getNombre()
                + " [" + (m.isEnAtaque() ? "ATK" : "DEF") + "]");
        }
        System.out.print("Monstruo [índice]: ");
        try {
            controlador.onCambiarModo(Integer.parseInt(sc.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("Índice inválido.");
        }
    }
}