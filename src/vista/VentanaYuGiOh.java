package vista;

import controlador.JuegoControlador;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import modelo.carta.Carta;
import modelo.carta.Monstruo;
import modelo.juego.Jugador;

public class VentanaYuGiOh extends JFrame implements VistaJuego {

    private JuegoControlador controlador;

    private JTextArea areaLog;
    private JLabel lpJ1, lpJ2;
    private JPanel panelCampoJ1, panelCampoJ2;
    private JPanel panelMano;
    private JButton btnAtacar, btnPasar, btnCambiarModo;

    private String nombreJ1, nombreJ2;

    public VentanaYuGiOh(JuegoControlador controlador) {
        this.controlador = controlador;
        controlador.agregarVista(this);

        setTitle("Yu-Gi-Oh - Duelo De Cartas");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String n1 = JOptionPane.showInputDialog(this, "Nombre del Jugador 1:", "Yugi Muto");
        String n2 = JOptionPane.showInputDialog(this, "Nombre del Jugador 2:", "Seto Kaiba");
        if (n1 == null || n1.trim().isEmpty()) n1 = "Yugi";
        if (n2 == null || n2.trim().isEmpty()) n2 = "Kaiba";

        nombreJ1 = n1;
        nombreJ2 = n2;
        controlador.setNombres(n1, n2);

        crearInterfaz();
        controlador.onRobar();
    }

    // -------------------------------------------------------
    // VistaJuego
    // -------------------------------------------------------

    @Override
    public void actualizar(Jugador j1, Jugador j2, boolean turnoJ1) {
        lpJ1.setText("❤️ " + j1.getLp());
        lpJ2.setText("❤️ " + j2.getLp());
        revalidate();
        repaint();

        if (j1.getLp() < 2000) { lpJ1.setForeground(Color.RED); lpJ1.setFont(new Font("Monospaced", Font.BOLD, 32)); }
        else                   { lpJ1.setForeground(new Color(255, 80, 80)); lpJ1.setFont(new Font("Monospaced", Font.BOLD, 28)); }
        if (j2.getLp() < 2000) { lpJ2.setForeground(Color.RED); lpJ2.setFont(new Font("Monospaced", Font.BOLD, 32)); }
        else                   { lpJ2.setForeground(new Color(255, 80, 80)); lpJ2.setFont(new Font("Monospaced", Font.BOLD, 28)); }

        if (turnoJ1) {
            lpJ1.setBackground(new Color(0, 100, 0, 80));
            lpJ2.setBackground(new Color(0, 0, 0, 100));
        } else {
            lpJ1.setBackground(new Color(0, 0, 0, 100));
            lpJ2.setBackground(new Color(0, 100, 0, 80));
        }

        panelCampoJ1.removeAll();
        for (Monstruo m : j1.getCampo()) {
            JLabel lbl = new JLabel(m.getNombre() +
                " [" + (m.isEnAtaque() ? "ATK" : "DEF") + "] " +
                m.getAtk() + "/" + m.getDef());
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            lbl.setOpaque(true);
            lbl.setBackground(m.isEnAtaque() ? new Color(255, 200, 200) : new Color(200, 200, 255));
            panelCampoJ1.add(lbl);
        }
        panelCampoJ1.revalidate();
        panelCampoJ1.repaint();

        panelCampoJ2.removeAll();
        for (Monstruo m : j2.getCampo()) {
            JLabel lbl = new JLabel(m.getNombre() +
                " [" + (m.isEnAtaque() ? "ATK" : "DEF") + "] " +
                m.getAtk() + "/" + m.getDef());
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            lbl.setOpaque(true);
            lbl.setBackground(m.isEnAtaque() ? new Color(255, 200, 200) : new Color(200, 200, 255));
            panelCampoJ2.add(lbl);
        }
        panelCampoJ2.revalidate();
        panelCampoJ2.repaint();

        panelMano.removeAll();
        Jugador actual = controlador.getActual();
        for (int i = 0; i < actual.getMano().size(); i++) {
            Carta c = actual.getMano().get(i);
            JButton btn = new JButton(c.getNombre());
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setBackground(new Color(255, 255, 200));
            final int idx = i;
            btn.addActionListener(e -> controlador.onJugarCarta(idx));
            panelMano.add(btn);
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        areaLog.append(mensaje + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    @Override
    public void mostrarGanador(String nombreGanador) {
        mostrarMensaje("¡" + nombreGanador + " GANA EL DUELO!");
        int r = JOptionPane.showConfirmDialog(this,
            nombreGanador + " ha ganado!\n¿Cerrar el juego?",
            "Fin del Duelo", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) System.exit(0);
    }

    @Override
    public int pedirEleccion(String titulo, String[] opciones) {
        return JOptionPane.showOptionDialog(this, titulo, "Selección",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opciones, null);
    }

    // -------------------------------------------------------
    // Acciones — solo recogen input y delegan al controlador
    // -------------------------------------------------------

    private void accionJugarCarta() {
        Jugador actual = controlador.getActual();
        if (actual.getMano().isEmpty()) { mostrarMensaje("No tienes cartas en mano."); return; }

        String[] opciones = new String[actual.getMano().size()];
        for (int i = 0; i < actual.getMano().size(); i++) {
            Carta c = actual.getMano().get(i);
            opciones[i] = c.getNombre();
            if (c instanceof Monstruo) {
                Monstruo m = (Monstruo) c;
                opciones[i] += " (Nv." + m.getNivel() + " ATK:" + m.getAtk() + " DEF:" + m.getDef() + ")";
            }
        }
        int sel = JOptionPane.showOptionDialog(this, "Elige una carta:",
            "Mano de " + actual.getNombre(), JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, opciones, null);
        if (sel >= 0) controlador.onJugarCarta(sel);
    }

    private void accionAtacar() {
        Jugador atacante = controlador.getActual();
        Jugador defensor = controlador.getOponente();

        if (atacante.getCampo().isEmpty()) { mostrarMensaje("No tienes monstruos."); return; }

        Monstruo atkM = elegirMonstruo(atacante.getCampo(), "Elige tu monstruo atacante", true);
        if (atkM == null) return;
        int idxAtk = atacante.getCampo().indexOf(atkM);

        if (defensor.getCampo().isEmpty()) {
            controlador.onAtacar(idxAtk, -1);
            return;
        }

        Monstruo defM = elegirMonstruo(defensor.getCampo(), "Elige monstruo enemigo", false);
        if (defM == null) return;
        controlador.onAtacar(idxAtk, defensor.getCampo().indexOf(defM));
    }

    private void accionCambiarModo() {
        Jugador actual = controlador.getActual();
        if (actual.getCampo().isEmpty()) { mostrarMensaje("No tienes monstruos."); return; }
        Monstruo m = elegirMonstruo(actual.getCampo(), "Elige monstruo para cambiar de modo", true);
        if (m == null) return;
        controlador.onCambiarModo(actual.getCampo().indexOf(m));
    }

    private Monstruo elegirMonstruo(List<Monstruo> lista, String titulo, boolean mostrarModo) {
        if (lista.isEmpty()) return null;
        String[] opciones = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Monstruo m = lista.get(i);
            String modo = mostrarModo ? (m.isEnAtaque() ? " [ATK]" : " [DEF]") : "";
            opciones[i] = m.getNombre() + modo + " (ATK:" + m.getAtk() + " DEF:" + m.getDef() + ")";
        }
        int idx = JOptionPane.showOptionDialog(this, titulo, "Seleccionar monstruo",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, null);
        return (idx >= 0) ? lista.get(idx) : null;
    }

    // -------------------------------------------------------
    // Construcción de la interfaz
    // -------------------------------------------------------

    private void crearInterfaz() {
        JPanel panelLP = new JPanel(new GridLayout(1, 2, 20, 10));
        panelLP.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelLP.setBackground(new Color(10, 10, 30));

        JPanel panelJ1 = new JPanel(new BorderLayout());
        panelJ1.setBackground(new Color(30, 30, 60));
        panelJ1.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JPanel panelJ2 = new JPanel(new BorderLayout());
        panelJ2.setBackground(new Color(30, 30, 60));
        panelJ2.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        lpJ1 = new JLabel("", SwingConstants.CENTER);
        lpJ2 = new JLabel("", SwingConstants.CENTER);
        lpJ1.setFont(new Font("Monospaced", Font.BOLD, 28));
        lpJ2.setFont(new Font("Monospaced", Font.BOLD, 28));
        lpJ1.setForeground(new Color(255, 80, 80));
        lpJ2.setForeground(new Color(255, 80, 80));
        lpJ1.setOpaque(true); lpJ1.setBackground(new Color(0, 0, 0, 100));
        lpJ2.setOpaque(true); lpJ2.setBackground(new Color(0, 0, 0, 100));

        JLabel nJ1 = new JLabel(nombreJ1, SwingConstants.CENTER);
        JLabel nJ2 = new JLabel(nombreJ2, SwingConstants.CENTER);
        nJ1.setFont(new Font("Arial", Font.BOLD, 16)); nJ1.setForeground(Color.WHITE);
        nJ2.setFont(new Font("Arial", Font.BOLD, 16)); nJ2.setForeground(Color.WHITE);
        nJ1.setOpaque(true); nJ1.setBackground(new Color(50, 50, 80));
        nJ2.setOpaque(true); nJ2.setBackground(new Color(50, 50, 80));

        panelJ1.add(nJ1, BorderLayout.NORTH); panelJ1.add(lpJ1, BorderLayout.CENTER);
        panelJ2.add(nJ2, BorderLayout.NORTH); panelJ2.add(lpJ2, BorderLayout.CENTER);
        panelLP.add(panelJ1); panelLP.add(panelJ2);
        add(panelLP, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCampoJ1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelCampoJ2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelCampoJ1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 2), "CAMPO DE " + nombreJ1));
        panelCampoJ2.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 2), "CAMPO DE " + nombreJ2));
        panelCampoJ1.setBackground(new Color(240, 240, 250));
        panelCampoJ2.setBackground(new Color(250, 240, 240));
        panelCentral.add(panelCampoJ1); panelCentral.add(panelCampoJ2);
        add(panelCentral, BorderLayout.CENTER);

        panelMano = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelMano.setBorder(BorderFactory.createTitledBorder("MANO ACTUAL"));
        panelMano.setPreferredSize(new Dimension(1000, 120));
        //add(panelMano, BorderLayout.SOUTH);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(20, 20, 30));
        areaLog.setForeground(new Color(100, 255, 100));
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(areaLog);
        scrollLog.setPreferredSize(new Dimension(1000, 150));
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelMano, BorderLayout.NORTH);
        panelInferior.add(scrollLog, BorderLayout.CENTER);

        add(panelInferior, BorderLayout.SOUTH);

        //add(scrollLog, BorderLayout.AFTER_LAST_LINE);

        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnJugar   = new JButton("JUGAR CARTA");
        btnAtacar          = new JButton("ATACAR");
        btnCambiarModo     = new JButton("CAMBIAR MODO");
        btnPasar           = new JButton("PASAR TURNO");
        JButton btnGuardar = new JButton("GUARDAR PARTIDA");
        JButton btnCargar  = new JButton("CARGAR PARTIDA");

        btnJugar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAtacar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCambiarModo.setFont(new Font("Arial", Font.BOLD, 14));
        btnPasar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCargar.setFont(new Font("Arial", Font.BOLD, 14));

        btnJugar.setBackground(new Color(200, 220, 255));
        btnAtacar.setBackground(new Color(255, 200, 200));
        btnCambiarModo.setBackground(new Color(200, 255, 200));
        btnPasar.setBackground(new Color(255, 255, 200));
        btnGuardar.setBackground(new Color(180, 255, 220));
        btnCargar.setBackground(new Color(220, 180, 255));

        btnJugar.addActionListener(e -> accionJugarCarta());
        btnAtacar.addActionListener(e -> accionAtacar());
        btnCambiarModo.addActionListener(e -> accionCambiarModo());
        btnPasar.addActionListener(e -> controlador.onPasarTurno());
        btnGuardar.addActionListener(e -> controlador.onGuardar());
        btnCargar.addActionListener(e -> controlador.onCargar());

        panelBotones.add(btnJugar);
        panelBotones.add(btnAtacar);
        panelBotones.add(btnCambiarModo);
        panelBotones.add(btnPasar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCargar);
        add(panelBotones, BorderLayout.EAST);
    }
}