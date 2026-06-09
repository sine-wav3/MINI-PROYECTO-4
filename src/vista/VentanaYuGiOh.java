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
    private PanelVida vidaJ1, vidaJ2;
    private JPanel panelCampoJ1, panelCampoJ2;
    private JPanel panelMano;
    private JButton btnAtacar, btnPasar, btnCambiarModo;

    private String nombreJ1, nombreJ2;

    // Panel que pinta el fondo de salud proporcional a los LP
    private static class PanelVida extends JPanel {
        private int lp;
        private static final int MAX = 8000;
        private final JLabel lbl;

        PanelVida() {
            lp = MAX;
            setLayout(new BorderLayout());
            setOpaque(false);
            lbl = new JLabel("❤️ " + MAX, SwingConstants.CENTER);
            lbl.setFont(new Font("Monospaced", Font.BOLD, 28));
            lbl.setForeground(new Color(255, 80, 80));
            add(lbl, BorderLayout.CENTER);
        }

        void setLp(int nuevoLp) {
            lp = Math.max(nuevoLp, 0);
            lbl.setText("❤️ " + lp);
            repaint();
        }

        void setActivo(boolean activo) {
            lbl.setForeground(activo ? new Color(255, 120, 120) : new Color(200, 60, 60));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            // fondo oscuro
            g.setColor(new Color(20, 20, 40));
            g.fillRect(0, 0, w, h);
            // relleno de salud
            int fill = (int)((double) lp / MAX * w);
            Color color = lp > MAX / 2 ? new Color(55, 148, 70)
                        : lp > MAX / 4 ? new Color(160, 120, 0)
                        : new Color(148, 35, 35);
            g.setColor(color);
            g.fillRect(0, 0, fill, h);
        }
    }

    public VentanaYuGiOh(JuegoControlador controlador) {
        this.controlador = controlador;

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
        // agregarVista DESPUÉS de crearInterfaz para que areaLog nunca sea null
        controlador.agregarVista(this);
        controlador.refrescarVistas();
    }

   
    // VistaJuego

    @Override
    public void actualizar(Jugador j1, Jugador j2, boolean turnoJ1) {
        vidaJ1.setLp(j1.getLp());
        vidaJ2.setLp(j2.getLp());
        vidaJ1.setActivo(turnoJ1);
        vidaJ2.setActivo(!turnoJ1);

        panelCampoJ1.removeAll();
        for (Monstruo m : j1.getCampo()) panelCampoJ1.add(crearCartaCampo(m));
        panelCampoJ1.revalidate();
        panelCampoJ1.repaint();

        panelCampoJ2.removeAll();
        for (Monstruo m : j2.getCampo()) panelCampoJ2.add(crearCartaCampo(m));
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
            final Color baseCard = (c instanceof Monstruo)
                ? new Color(200, 170, 90) : new Color(110, 160, 200);
            JButton btn = new JButton(detalle) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setPaint(new GradientPaint(0, 0, baseCard.brighter(), 0, getHeight(), baseCard.darker()));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setBackground(baseCard);
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
            ));
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
        // Mantener solo las últimas 15 líneas para no saturar el log
        String[] lineas = areaLog.getText().split("\n");
        if (lineas.length > 15) {
            StringBuilder sb = new StringBuilder();
            for (int i = lineas.length - 15; i < lineas.length; i++) {
                sb.append(lineas[i]).append("\n");
            }
            areaLog.setText(sb.toString());
        }
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

    // Acciones — solo recogen input y delegan al controlador

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

    private JPanel crearCartaCampo(Monstruo m) {
        boolean esAtaque = m.isEnAtaque();
        Color base = esAtaque ? new Color(140, 50, 50) : new Color(45, 55, 130);
        JPanel card = new JPanel(new GridLayout(5, 1, 2, 3)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, base.brighter(), 0, getHeight(), base.darker()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(155, 195));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 175, 60), 2),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        JLabel nombre = new JLabel("<html><center>" + m.getNombre() + "</center></html>", SwingConstants.CENTER);
        nombre.setFont(new Font("Serif", Font.BOLD, 13));
        nombre.setForeground(Color.WHITE);

        JLabel nivel = new JLabel("★".repeat(Math.min(m.getNivel(), 12)), SwingConstants.CENTER);
        nivel.setFont(new Font("Dialog", Font.PLAIN, 11));
        nivel.setForeground(new Color(255, 215, 0));

        JLabel modo = new JLabel(esAtaque ? "⚔  ATAQUE" : "🛡  DEFENSA", SwingConstants.CENTER);
        modo.setFont(new Font("Arial", Font.BOLD, 12));
        modo.setForeground(esAtaque ? new Color(255, 180, 180) : new Color(150, 180, 255));

        JLabel atk = new JLabel("ATK  " + m.getAtk(), SwingConstants.CENTER);
        atk.setFont(new Font("Monospaced", Font.BOLD, 12));
        atk.setForeground(new Color(255, 130, 130));

        JLabel def = new JLabel("DEF  " + m.getDef(), SwingConstants.CENTER);
        def.setFont(new Font("Monospaced", Font.BOLD, 12));
        def.setForeground(new Color(130, 185, 255));

        card.add(nombre);
        card.add(nivel);
        card.add(modo);
        card.add(atk);
        card.add(def);
        return card;
    }

    private JButton boton3D(String texto, Color base) {
        boolean[] pressed = {false};
        javax.swing.border.Border raised = BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );
        javax.swing.border.Border lowered = BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createEmptyBorder(6, 9, 4, 7)
        );
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = pressed[0] ? base.darker() : base.brighter();
                Color bot = pressed[0] ? base.brighter() : base.darker();
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bot));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setBackground(base);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(raised);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                pressed[0] = true;  btn.setBorder(lowered); btn.repaint();
            }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) {
                pressed[0] = false; btn.setBorder(raised);  btn.repaint();
            }
        });
        return btn;
    }

    private void crearInterfaz() {
        
        JPanel panelLP = new JPanel(new GridLayout(1, 2, 20, 10));
        panelLP.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panelLP.setBackground(new Color(38, 35, 62));

        // Recuadro J1
        JPanel panelJ1 = new JPanel(new BorderLayout());
        panelJ1.setBorder(BorderFactory.createLineBorder(new Color(195, 185, 235), 2));

        JLabel nJ1 = new JLabel(nombreJ1, SwingConstants.CENTER);
        nJ1.setFont(new Font("Arial", Font.BOLD, 16));
        nJ1.setForeground(Color.WHITE);
        nJ1.setOpaque(true);
        nJ1.setBackground(new Color(50, 50, 80));

        vidaJ1 = new PanelVida();
        vidaJ1.setPreferredSize(new Dimension(0, 55));

        panelJ1.add(nJ1,    BorderLayout.NORTH);
        panelJ1.add(vidaJ1, BorderLayout.CENTER);

        // Recuadro J2
        JPanel panelJ2 = new JPanel(new BorderLayout());
        panelJ2.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JLabel nJ2 = new JLabel(nombreJ2, SwingConstants.CENTER);
        nJ2.setFont(new Font("Arial", Font.BOLD, 16));
        nJ2.setForeground(Color.WHITE);
        nJ2.setOpaque(true);
        nJ2.setBackground(new Color(50, 50, 80));

        vidaJ2 = new PanelVida();
        vidaJ2.setPreferredSize(new Dimension(0, 55));

        panelJ2.add(nJ2,    BorderLayout.NORTH);
        panelJ2.add(vidaJ2, BorderLayout.CENTER);

        panelLP.add(panelJ1);
        panelLP.add(panelJ2);
        add(panelLP, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new GridLayout(2, 1, 10, 10));
        panelCampoJ1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelCampoJ2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        javax.swing.border.TitledBorder tbC1 = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 180, 60), 2), "CAMPO DE " + nombreJ1);
        tbC1.setTitleColor(new Color(200, 210, 255));
        panelCampoJ1.setBorder(tbC1);
        javax.swing.border.TitledBorder tbC2 = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 180, 60), 2), "CAMPO DE " + nombreJ2);
        tbC2.setTitleColor(new Color(200, 210, 255));
        panelCampoJ2.setBorder(tbC2);
        panelCampoJ1.setBackground(new Color(22, 28, 55));
        panelCampoJ2.setBackground(new Color(28, 22, 55));
        panelCentral.add(panelCampoJ1); panelCentral.add(panelCampoJ2);
        add(panelCentral, BorderLayout.CENTER);

        panelMano = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        panelMano.setBackground(new Color(38, 35, 62));
        javax.swing.border.TitledBorder tbMano = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(110, 100, 160), 1),
            "MANO — haz clic en una carta para jugarla");
        tbMano.setTitleColor(new Color(190, 180, 225));
        panelMano.setBorder(tbMano);

        JScrollPane scrollMano = new JScrollPane(panelMano,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollMano.getViewport().setBackground(new Color(38, 35, 62));
        scrollMano.setBackground(new Color(38, 35, 62));
        scrollMano.setBorder(null);
        scrollMano.setPreferredSize(new Dimension(0, 95));
        scrollMano.getHorizontalScrollBar().setUnitIncrement(20);

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setBackground(new Color(18, 18, 35));
        areaLog.setForeground(new Color(170, 210, 255));
        areaLog.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        areaLog.setMargin(new Insets(8, 12, 8, 12));
        areaLog.setLineWrap(true);
        areaLog.setWrapStyleWord(true);

        
        JScrollPane scrollLog = new JScrollPane(areaLog,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLog.setPreferredSize(new Dimension(0, 115));
        scrollLog.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80, 75, 120)));
        scrollLog.getViewport().setBackground(new Color(18, 18, 35));

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(scrollMano, BorderLayout.NORTH);
        panelInferior.add(scrollLog, BorderLayout.CENTER);

        add(panelInferior, BorderLayout.SOUTH);

        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelBotones.setBackground(new Color(38, 35, 62));

        btnAtacar               = boton3D("ATACAR",           new Color(210, 100, 100));
        btnCambiarModo          = boton3D("CAMBIAR MODO",     new Color(90,  180, 120));
        btnPasar                = boton3D("PASAR TURNO",      new Color(210, 200,  80));
        JButton btnGuardar      = boton3D("GUARDAR PARTIDA",  new Color(80,  185, 160));
        JButton btnCargar       = boton3D("CARGAR PARTIDA",   new Color(160, 110, 210));
        JButton btnEstadisticas = boton3D("ESTADÍSTICAS",     new Color(215, 165,  60));

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