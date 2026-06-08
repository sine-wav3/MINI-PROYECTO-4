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
        controlador.refrescarVistas();
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
        List<Carta> mano = actual.getMano();
        for (int i = 0; i < mano.size(); i++) {
            Carta c = mano.get(i);
            String detalle = (c instanceof Monstruo m)
                ? "<html><center>" + c.getNombre() + "<br>Nv." + m.getNivel() + " ATK:" + m.getAtk() + "</center></html>"
                : "<html><center>" + c.getNombre() + "<br>[Magia/Trampa]</center></html>";
            JButton btn = new JButton(detalle);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setBackground(new Color(255, 255, 200));
            btn.setPreferredSize(new Dimension(140, 60));
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
            if (c instanceof Monstruo m) {
                opciones[i] = c.getNombre() + "  |  Nv." + m.getNivel() +
                              "  ATK:" + m.getAtk() + "  DEF:" + m.getDef();
            } else {
                opciones[i] = c.getNombre() + "  |  [Magia/Trampa]";
            }
        }

        JList<String> lista = new JList<>(opciones);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setSelectedIndex(0);
        lista.setFont(new Font("Monospaced", Font.PLAIN, 13));
        lista.setVisibleRowCount(Math.min(opciones.length, 8));
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setPreferredSize(new Dimension(480, lista.getVisibleRowCount() * 28));

        int result = JOptionPane.showConfirmDialog(this, scroll,
            "Mano de " + actual.getNombre() + " — elige una carta",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && lista.getSelectedIndex() >= 0) {
            controlador.onJugarCarta(lista.getSelectedIndex());
        }
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

        JDialog dialogo = new JDialog(this, titulo, true);
        dialogo.setLayout(new BorderLayout());
        dialogo.getContentPane().setBackground(new Color(10, 10, 30));

        // Título del diálogo
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(255, 215, 0)); // dorado
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 8, 10));
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(new Color(10, 10, 30));
        dialogo.add(lblTitulo, BorderLayout.NORTH);

        // Panel de cartas
        JPanel panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelCartas.setBackground(new Color(20, 20, 50));
        panelCartas.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Monstruo[] seleccion = {null};

        for (Monstruo m : lista) {
            boolean esAtaque = m.isEnAtaque();
            Color fondoCarta  = esAtaque ? new Color(180, 120, 40) : new Color(60, 80, 160);
            Color bordeColor  = new Color(255, 215, 0);

            JPanel carta = new JPanel(new GridLayout(5, 1, 2, 2));
            carta.setBackground(fondoCarta);
            carta.setPreferredSize(new Dimension(160, 200));
            carta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bordeColor, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            carta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel nombre = new JLabel("<html><center>" + m.getNombre() + "</center></html>", SwingConstants.CENTER);
            nombre.setFont(new Font("Serif", Font.BOLD, 13));
            nombre.setForeground(Color.WHITE);

            JLabel modo = new JLabel(mostrarModo ? (esAtaque ? "⚔  ATAQUE" : "🛡  DEFENSA") : "", SwingConstants.CENTER);
            modo.setFont(new Font("Arial", Font.BOLD, 12));
            modo.setForeground(esAtaque ? new Color(255, 200, 80) : new Color(150, 180, 255));

            JLabel atk = new JLabel("ATK  " + m.getAtk(), SwingConstants.CENTER);
            atk.setFont(new Font("Monospaced", Font.BOLD, 13));
            atk.setForeground(new Color(255, 100, 100));

            JLabel def = new JLabel("DEF  " + m.getDef(), SwingConstants.CENTER);
            def.setFont(new Font("Monospaced", Font.BOLD, 13));
            def.setForeground(new Color(100, 180, 255));

            JLabel nivel = new JLabel("★".repeat(Math.min(m.getNivel(), 12)), SwingConstants.CENTER);
            nivel.setFont(new Font("Dialog", Font.PLAIN, 10));
            nivel.setForeground(new Color(255, 215, 0));

            carta.add(nombre);
            carta.add(nivel);
            carta.add(modo);
            carta.add(atk);
            carta.add(def);

            carta.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    carta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 3),
                        BorderFactory.createEmptyBorder(7, 7, 7, 7)
                    ));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    carta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bordeColor, 2),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)
                    ));
                }
                @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                    seleccion[0] = m;
                    dialogo.dispose();
                }
            });

            panelCartas.add(carta);
        }

        JScrollPane scroll = new JScrollPane(panelCartas,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBackground(new Color(20, 20, 50));
        scroll.setBorder(null);
        dialogo.add(scroll, BorderLayout.CENTER);

        // Botón cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(80, 20, 20));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        JPanel panelSur = new JPanel();
        panelSur.setBackground(new Color(10, 10, 30));
        panelSur.add(btnCancelar);
        dialogo.add(panelSur, BorderLayout.SOUTH);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);

        return seleccion[0];
    }

    private void mostrarEstadisticas() {
        String datos = controlador.getEstadisticas();

        JTextArea area = new JTextArea(datos);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(new Color(10, 10, 30));
        area.setForeground(new Color(255, 215, 0));
        area.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 300));
        scroll.getViewport().setBackground(new Color(10, 10, 30));

        JDialog dialogo = new JDialog(this, "Estadísticas históricas", true);
        dialogo.setLayout(new BorderLayout());
        dialogo.getContentPane().setBackground(new Color(10, 10, 30));

        JLabel titulo = new JLabel("HISTORIAL DE DUELOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 18));
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 10, 8, 10));
        titulo.setOpaque(true);
        titulo.setBackground(new Color(10, 10, 30));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(80, 20, 20));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrar.addActionListener(e -> dialogo.dispose());
        JPanel sur = new JPanel();
        sur.setBackground(new Color(10, 10, 30));
        sur.add(btnCerrar);

        dialogo.add(titulo, BorderLayout.NORTH);
        dialogo.add(scroll, BorderLayout.CENTER);
        dialogo.add(sur, BorderLayout.SOUTH);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
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
        panelMano.setBorder(BorderFactory.createTitledBorder("MANO — haz clic en una carta para jugarla"));
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

        btnAtacar               = new JButton("ATACAR");
        btnCambiarModo          = new JButton("CAMBIAR MODO");
        btnPasar                = new JButton("PASAR TURNO");
        JButton btnGuardar      = new JButton("GUARDAR PARTIDA");
        JButton btnCargar       = new JButton("CARGAR PARTIDA");
        JButton btnEstadisticas = new JButton("ESTADÍSTICAS");

        btnAtacar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCambiarModo.setFont(new Font("Arial", Font.BOLD, 14));
        btnPasar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCargar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEstadisticas.setFont(new Font("Arial", Font.BOLD, 14));

        btnAtacar.setBackground(new Color(255, 200, 200));
        btnCambiarModo.setBackground(new Color(200, 255, 200));
        btnPasar.setBackground(new Color(255, 255, 200));
        btnGuardar.setBackground(new Color(180, 255, 220));
        btnCargar.setBackground(new Color(220, 180, 255));
        btnEstadisticas.setBackground(new Color(255, 220, 150));

        btnAtacar.addActionListener(e -> accionAtacar());
        btnCambiarModo.addActionListener(e -> accionCambiarModo());
        btnPasar.addActionListener(e -> controlador.onPasarTurno());
        btnGuardar.addActionListener(e -> controlador.onGuardar());
        btnCargar.addActionListener(e -> controlador.onCargar());
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());

        panelBotones.add(btnAtacar);
        panelBotones.add(btnCambiarModo);
        panelBotones.add(btnPasar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCargar);
        panelBotones.add(btnEstadisticas);
        add(panelBotones, BorderLayout.EAST);
    }
}