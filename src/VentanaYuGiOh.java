import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class VentanaYuGiOh extends JFrame {
    // jugadores por este lado
    private Jugador j1, j2;
    private boolean turnoJ1;
    
    // aqui van las reglas (o mas bien pues los controles de turno y eso)
    private boolean haRobadoEsteTurno = false;
    private boolean haAtacadoEsteTurno = false;
    private boolean haJugadoCartaEsteTurno = false;
    private boolean primerTurnoRealizado = false;
    
    // componentes basicos de la interfaz
    private JTextArea areaLog;
    private JLabel lpJ1, lpJ2;
    private JPanel panelCampoJ1, panelCampoJ2;
    private JPanel panelMano;
    private JButton btnAtacar, btnPasar, btnCambiarModo;
    
    public VentanaYuGiOh() {
        setTitle("Yu-Gi-Oh - Duelo De Cartas");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // se inicializa el juego (nombres, mazos, manos iniciales, etc etc)
        inicializarJuego();
        
        // como su nombre dice crea la interfaz y todo lo visual del juego
        crearInterfaz();
        
        // actualiza la vista para mostrar todo lo que se inicializo y eso
        actualizarVista();
        
        // mensajito de bienvenida a la batallaaaaaaa (si eso mismo)
        agregarLog(">>>>>>>> DUELO INICIADO <<<<<<<<"); //no conte si estaban parejos los > < pero supondre que si por pereza
        agregarLog(j1.getNombre() + " vs " + j2.getNombre());
        agregarLog((turnoJ1 ? j1.getNombre() : j2.getNombre()) + " comienza el duelo!");
        
        // aqui es el robo inicial el cual es automatico en el primer turno para asi poder empezar a jugar cartas y eso, si no pues no se podria hacer nada en el primer turno y eso no es divertido umjum umjum
        accionRobarSiNecesario();
    }
    
    private void inicializarJuego() {
        // aqui se piden nombres con los dialogos
        String n1 = JOptionPane.showInputDialog(this, "Nombre del Jugador 1:", "Yugi Muto");
        String n2 = JOptionPane.showInputDialog(this, "Nombre del Jugador 2:", "Seto Kaiba");
        
        if (n1 == null || n1.trim().isEmpty()) n1 = "Yugi";
        if (n2 == null || n2.trim().isEmpty()) n2 = "Kaiba";
        
        // se crean 50 cartas (30 monstruos, 10 magicas, 10 trampas)
        List<Carta> pool = new ArrayList<>();
        
        // 30 monstruos (con niveles variados deñ 1 al 12)
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
            int atk = 500 + (i * 80);
            int def = 500 + (i * 60);
            pool.add(new Monstruo(nombresMonstruos[i % nombresMonstruos.length] + " " + (i+1), atk, def, nivel));
        }
        
        // 10 cartitas magicas
        pool.add(new PotOfGreed());
        pool.add(new PotOfGreed());
        pool.add(new BoostAtk());
        pool.add(new AcesCoup());
        pool.add(new DarkHole());
        pool.add(new Hinotama());
        pool.add(new ChangeOfHeart());
        pool.add(new Raigeki());
        pool.add(new StandarOfCourage());
        pool.add(new TyphoonOfMagicalSpace());
        
        // 10 cartitas trampa
        for (int i = 0; i < 5; i++) pool.add(new MirrorForce());
        for (int i = 0; i < 5; i++) pool.add(new SakuretsuArmor());
        
        // aqui se mezcla y se reparten 25 cartas a cada jugador para formar sus mazos, el resto se queda fuera del juego
        Collections.shuffle(pool);
        
        Stack<Carta> mazo1 = new Stack<>();
        Stack<Carta> mazo2 = new Stack<>();
        
        for (int i = 0; i < 25; i++) {
            mazo1.push(pool.remove(0));
            mazo2.push(pool.remove(0));
        }
        
        j1 = new Jugador(n1, mazo1);
        j2 = new Jugador(n2, mazo2);
        
        // roba 5 cartas iniciales cada uno
        for (int i = 0; i < 5; i++) {
            j1.robarCarta();
            j2.robarCarta();
        }
        
        // aqui el gambling para ver quien empieza, es un simple random pero pues es para darle un toque de aleatoriedad al asunto
        turnoJ1 = new Random().nextBoolean();
    }
    
    private void crearInterfaz() {
    // aqui tambien toco que hacer cambios ya que la vida no era visible
    JPanel panelLP = new JPanel(new GridLayout(1, 2, 20, 10));
    panelLP.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
    panelLP.setBackground(new Color(10, 10, 30));
    
    // panel para el jugador 1
    JPanel panelJ1 = new JPanel(new BorderLayout());
    panelJ1.setBackground(new Color(30, 30, 60));
    panelJ1.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    
    // panel para el jugador 2
    JPanel panelJ2 = new JPanel(new BorderLayout());
    panelJ2.setBackground(new Color(30, 30, 60));
    panelJ2.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    
    // labels de la vida 
    lpJ1 = new JLabel("", SwingConstants.CENTER);
    lpJ2 = new JLabel("", SwingConstants.CENTER);
    
    lpJ1.setFont(new Font("Monospaced", Font.BOLD, 28));
    lpJ2.setFont(new Font("Monospaced", Font.BOLD, 28));
    
    lpJ1.setForeground(new Color(255, 80, 80));
    lpJ2.setForeground(new Color(255, 80, 80));
    
    lpJ1.setOpaque(true);
    lpJ2.setOpaque(true);
    lpJ1.setBackground(new Color(0, 0, 0, 100));
    lpJ2.setBackground(new Color(0, 0, 0, 100));
    
    // subtitulos con nombres
    JLabel nombreJ1 = new JLabel(j1.getNombre(), SwingConstants.CENTER);
    JLabel nombreJ2 = new JLabel(j2.getNombre(), SwingConstants.CENTER);
    nombreJ1.setFont(new Font("Arial", Font.BOLD, 16));
    nombreJ2.setFont(new Font("Arial", Font.BOLD, 16));
    nombreJ1.setForeground(Color.WHITE);
    nombreJ2.setForeground(Color.WHITE);
    nombreJ1.setOpaque(true);
    nombreJ2.setOpaque(true);
    nombreJ1.setBackground(new Color(50, 50, 80));
    nombreJ2.setBackground(new Color(50, 50, 80));
    
    panelJ1.add(nombreJ1, BorderLayout.NORTH);
    panelJ1.add(lpJ1, BorderLayout.CENTER);
    
    panelJ2.add(nombreJ2, BorderLayout.NORTH);
    panelJ2.add(lpJ2, BorderLayout.CENTER);
    
    panelLP.add(panelJ1);
    panelLP.add(panelJ2);
    add(panelLP, BorderLayout.NORTH);
    
    // aqui la se hace un pequeño reseteo de la interfaz
    
    // panel central con campos de batalla
    JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
    panelCampoJ1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    panelCampoJ2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    panelCampoJ1.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.YELLOW, 2), 
        "⚔️ CAMPO DE " + j1.getNombre() + " ⚔️"));
    panelCampoJ2.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.YELLOW, 2), 
        "⚔️ CAMPO DE " + j2.getNombre() + " ⚔️"));
    panelCampoJ1.setBackground(new Color(240, 240, 250));
    panelCampoJ2.setBackground(new Color(250, 240, 240));
    panelCentral.add(panelCampoJ1);
    panelCentral.add(panelCampoJ2);
    add(panelCentral, BorderLayout.CENTER);
    
    // panel de mano del jugador actual
    panelMano = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
    panelMano.setBorder(BorderFactory.createTitledBorder("🃏 MANO ACTUAL 🃏"));
    panelMano.setPreferredSize(new Dimension(1000, 120));
    add(panelMano, BorderLayout.SOUTH);
    
    // area de log
    areaLog = new JTextArea();
    areaLog.setEditable(false);
    areaLog.setBackground(new Color(20, 20, 30));
    areaLog.setForeground(new Color(100, 255, 100));
    areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JScrollPane scrollLog = new JScrollPane(areaLog);
    scrollLog.setPreferredSize(new Dimension(1000, 150));
    add(scrollLog, BorderLayout.AFTER_LAST_LINE);
    
    // panel de botones (derecha)
    JPanel panelBotones = new JPanel(new GridLayout(4, 1, 10, 10));
    panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    JButton btnJugar = new JButton("🎴 JUGAR CARTA");
    btnAtacar = new JButton("⚔️ ATACAR");
    btnCambiarModo = new JButton("🔄 CAMBIAR MODO");
    btnPasar = new JButton("⏩ PASAR TURNO");
    
    btnJugar.setFont(new Font("Arial", Font.BOLD, 14));
    btnAtacar.setFont(new Font("Arial", Font.BOLD, 14));
    btnCambiarModo.setFont(new Font("Arial", Font.BOLD, 14));
    btnPasar.setFont(new Font("Arial", Font.BOLD, 14));
    
    btnJugar.setBackground(new Color(200, 220, 255));
    btnAtacar.setBackground(new Color(255, 200, 200));
    btnCambiarModo.setBackground(new Color(200, 255, 200));
    btnPasar.setBackground(new Color(255, 255, 200));
    
    btnJugar.addActionListener(e -> accionJugarCarta());
    btnAtacar.addActionListener(e -> accionAtacar());
    btnCambiarModo.addActionListener(e -> accionCambiarModo());
    btnPasar.addActionListener(e -> accionPasarTurno());
    
    panelBotones.add(btnJugar);
    panelBotones.add(btnAtacar);
    panelBotones.add(btnCambiarModo);
    panelBotones.add(btnPasar);
    add(panelBotones, BorderLayout.EAST);
    
    //fuerza una actualizacion inicial
    actualizarVista();
}
    
    private void accionRobarSiNecesario() {
        if (!haRobadoEsteTurno) {
            Jugador actual = getJugadorActual();
            actual.robarCarta();
            haRobadoEsteTurno = true;
            agregarLog("este " + actual.getNombre() + " roba una carta del mazo");
            actualizarVista();
            
            //aqui verifica si el mazo esta vacio para declarar que perdio el que tenga el mazo vacio
            verificarVictoria();
        }
    }
    
    private void accionJugarCarta() {
        Jugador actual = getJugadorActual();
        Jugador oponente = turnoJ1 ? j2 : j1;
        
        // he aqui la regla de 1 carta por turno
        if (haJugadoCartaEsteTurno) {
            agregarLog("ey bro para, ya jugaste una carta este turno calmate");
            return;
        }
        
        // asegura quien robo primero
        accionRobarSiNecesario();
        
        if (actual.getMano().isEmpty()) {
            agregarLog("nah mano No tienes cartas en la mano");
            return;
        }
        
        // aqui las opciones de las cartas
        String[] opciones = new String[actual.getMano().size()];
        for (int i = 0; i < actual.getMano().size(); i++) {
            Carta c = actual.getMano().get(i);
            opciones[i] = c.getNombre();
            if (c instanceof Monstruo) {
                Monstruo m = (Monstruo) c;
                opciones[i] += " (nivel " + m.getNivel() + " | araque " + m.getAtk() + " | defensa " + m.getDef() + ")"; 
            }
        }
        
        int seleccion = JOptionPane.showOptionDialog(
            this,
            "Elige una carta para jugar:",
            "Mano de " + actual.getNombre(),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            null
        );
        
        if (seleccion >= 0) {
            actual.jugarCarta(seleccion, oponente, this);
            haJugadoCartaEsteTurno = true;
            actualizarVista();
            verificarVictoria();
        }
    }
    
    private void accionAtacar() {
        Jugador atacante = getJugadorActual();
        Jugador defensor = turnoJ1 ? j2 : j1;
        
        // Reglas de ataque
        if (!primerTurnoRealizado) {
            agregarLog("ey manito para, el primer jugador no puede atacar en su primer turno");
            return;
        }
        
        if (haAtacadoEsteTurno) {
            agregarLog("no te pases bro, ya atacaste este turno");
            return;
        }
        
        if (atacante.getCampo().isEmpty()) {
            agregarLog("noporolo, no tienes monstruos para atacar");
            return;
        }
        
        // Se elige el monstruo atacante 
        Monstruo atacanteM = elegirMonstruo(atacante.getCampo(), "Elige tu monstruo atacante", true);
        if (atacanteM == null) return;
        
        // si no hay monstruos enemigos ronces ataque directo
        if (defensor.getCampo().isEmpty()) {
            int dano = atacanteM.getAtk();
            defensor.recibirDano(dano);
            agregarLog(" ATAQUE DIRECTO, TOMAAA! " + atacanteM.getNombre() + " causa " + dano + " de daño a " + defensor.getNombre());
        } else {
            // Elegir monstruo defensor
            Monstruo defensorM = elegirMonstruo(defensor.getCampo(), "Elige monstruo enemigo", false);
            if (defensorM == null) return;
            
            int atkAtacante = atacanteM.getAtk();
            int defDefensor = defensorM.getDef();
            
            if (atkAtacante > defDefensor) {
                int diff = atkAtacante - defDefensor;
                defensor.getCampo().remove(defensorM);
                defensor.recibirDano(diff);
                agregarLog("Pum " + atacanteM.getNombre() + " (" + atkAtacante + ") destruye a " + defensorM.getNombre() + " (" + defDefensor + ")!");
                if (diff > 0) {
                    agregarLog("Daño de diferencia: " + diff);
                }
            } else {
                agregarLog("PIM " + atacanteM.getNombre() + " (" + atkAtacante + ") no pudo vencer a " + defensorM.getNombre() + " (" + defDefensor + ")");
            }
        }
        
        haAtacadoEsteTurno = true;
        actualizarVista();
        verificarVictoria();
    }
    
    private void accionCambiarModo() {
        Jugador actual = getJugadorActual();
        
        if (actual.getCampo().isEmpty()) {
            agregarLog("Para, no tienes monstruos para cambiar de modo");
            return;
        }
        
        Monstruo m = elegirMonstruo(actual.getCampo(), "Elige monstruo para cambiar de modo", true);
        if (m == null) return;
        
        if (!m.puedeCambiarModo()) {
            agregarLog("Ey, este monstruo ya cambió de modo este turno");
            return;
        }
        
        m.cambiarModo();
        agregarLog("FIUM " + m.getNombre() + " ahora está en " + (m.isEnAtaque() ? "MODO ATAQUE" : "MODO DEFENSA"));
        actualizarVista();
    }
    
    private Monstruo elegirMonstruo(List<Monstruo> lista, String titulo, boolean mostrarModo) {
        if (lista.isEmpty()) return null;
        
        String[] opciones = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Monstruo m = lista.get(i);
            String modo = "";
            if (mostrarModo) {
                modo = m.isEnAtaque() ? " [⚔️]" : " [🛡️]";
            }
            opciones[i] = m.getNombre() + modo + " (⚔️" + m.getAtk() + " | 🛡️" + m.getDef() + " | ⭐" + m.getNivel() + ")";
        }
        
        int idx = JOptionPane.showOptionDialog(
            this,
            titulo,
            "Seleccionar monstruo",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            null
        );
        
        return (idx >= 0) ? lista.get(idx) : null;
    }
    
    private void accionPasarTurno() {
        turnoJ1 = !turnoJ1;
        
        // resetear flags del nuevo turno
        haRobadoEsteTurno = false;
        haAtacadoEsteTurno = false;
        haJugadoCartaEsteTurno = false;
        primerTurnoRealizado = true;
        
        // resetear cambio de modo de los monstruos
        for (Monstruo m : j1.getCampo()) m.resetCambio();
        for (Monstruo m : j2.getCampo()) m.resetCambio();
        
        agregarLog("\n🔁 ===== TURNO DE " + getJugadorActual().getNombre().toUpperCase() + " ===== 🔁");
        actualizarVista();
        
        // roba una carta automáticamente al empezar el turno
        accionRobarSiNecesario();
    }
    
    private Jugador getJugadorActual() {
        return turnoJ1 ? j1 : j2;
    }
    
    private void agregarLog(String msg) {
        areaLog.append(msg + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }
    
    private void actualizarVista() {
    //toco cambiar una parte de esta funcion ya que no se veia la vida
    String vidaJ1 = "❤️ " + j1.getLp() + " ❤️";
    String vidaJ2 = "❤️ " + j2.getLp() + " ❤️";
    
    lpJ1.setText(vidaJ1);
    lpJ2.setText(vidaJ2);
    
    // Cambiar color si la vida es baja (menos de 2000 de vida para darle cositas bonitas a la interfaz)
    if (j1.getLp() < 2000) {
        lpJ1.setForeground(new Color(255, 0, 0));
        lpJ1.setFont(new Font("Monospaced", Font.BOLD, 32));
    } else {
        lpJ1.setForeground(new Color(255, 80, 80));
        lpJ1.setFont(new Font("Monospaced", Font.BOLD, 28));
    }
    
    if (j2.getLp() < 2000) {
        lpJ2.setForeground(new Color(255, 0, 0));
        lpJ2.setFont(new Font("Monospaced", Font.BOLD, 32));
    } else {
        lpJ2.setForeground(new Color(255, 80, 80));
        lpJ2.setFont(new Font("Monospaced", Font.BOLD, 28));
    }
    
    // resaltar al jugador que tiene el turno
    if (turnoJ1) {
        lpJ1.setBackground(new Color(0, 100, 0, 80));
        lpJ2.setBackground(new Color(0, 0, 0, 100));
    } else {
        lpJ1.setBackground(new Color(0, 0, 0, 100));
        lpJ2.setBackground(new Color(0, 100, 0, 80));
    }
    
    // actualizar campos de monstruos
    panelCampoJ1.removeAll();
    for (Monstruo m : j1.getCampo()) {
        JLabel lbl = new JLabel(m.getNombre() + " [" + (m.isEnAtaque() ? "⚔️" : "🛡️") + "] " + m.getAtk() + "/" + m.getDef());
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        lbl.setOpaque(true);
        lbl.setBackground(m.isEnAtaque() ? new Color(255, 200, 200) : new Color(200, 200, 255));
        panelCampoJ1.add(lbl);
    }
    
    panelCampoJ2.removeAll();
    for (Monstruo m : j2.getCampo()) {
        JLabel lbl = new JLabel(m.getNombre() + " [" + (m.isEnAtaque() ? "⚔️" : "🛡️") + "] " + m.getAtk() + "/" + m.getDef());
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        lbl.setOpaque(true);
        lbl.setBackground(m.isEnAtaque() ? new Color(255, 200, 200) : new Color(200, 200, 255));
        panelCampoJ2.add(lbl);
    }
    
    panelCampoJ1.revalidate();
    panelCampoJ1.repaint();
    panelCampoJ2.revalidate();
    panelCampoJ2.repaint();
    
    // actualiza la mano
    panelMano.removeAll();
    Jugador actual = getJugadorActual();
    for (int i = 0; i < actual.getMano().size(); i++) {
        Carta c = actual.getMano().get(i);
        JButton btnCarta = new JButton(c.getNombre());
        btnCarta.setFont(new Font("Arial", Font.PLAIN, 11));
        btnCarta.setBackground(new Color(255, 255, 200));
        
        final int idx = i;
        btnCarta.addActionListener(e -> {
            if (!haJugadoCartaEsteTurno) {
                actual.jugarCarta(idx, turnoJ1 ? j2 : j1, this);
                haJugadoCartaEsteTurno = true;
                actualizarVista();
                verificarVictoria();
            } else {
                agregarLog("Nuh uh, ya jugaste una carta este turno!");
            }
        });
        
        panelMano.add(btnCarta);
    }
    
    panelMano.revalidate();
    panelMano.repaint();
    
    //toco hacer este mini debug para asegurar que los labels si existen
    System.out.println("Vida J1: " + j1.getLp() + " - Label: " + lpJ1.getText());
    System.out.println("Vida J2: " + j2.getLp() + " - Label: " + lpJ2.getText());
}
    
    private void verificarVictoria() {
        String ganador = null;
        
        if (j1.getLp() <= 0) {
            ganador = j2.getNombre();
        } else if (j2.getLp() <= 0) {
            ganador = j1.getNombre();
        } else if (j1.getMazo().isEmpty() && j1.getMano().isEmpty()) {
            ganador = j2.getNombre();
            agregarLog(j1.getNombre() + " se quedó sin cartas!");
        } else if (j2.getMazo().isEmpty() && j2.getMano().isEmpty()) {
            ganador = j1.getNombre();
            agregarLog(j2.getNombre() + " se quedó sin cartas!");
        }
        
        if (ganador != null) {
            agregarLog("\n✨✨✨✨✨✨✨✨✨✨✨✨✨✨✨✨✨");
            agregarLog("🎉🏆 " + ganador.toUpperCase() + " ES EL GANADOR DEL DUELO! 🏆🎉");
            agregarLog("✨✨✨✨✨✨✨✨✨✨✨✨✨✨✨✨✨");
            
            int respuesta = JOptionPane.showConfirmDialog(
                this,
                ganador + " ha ganado el duelo!\n¿Quieres cerrar el juego?",
                "Yu-Gi-Oh! - Fin del Duelo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (respuesta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
}