package ui;

import entidades.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class PantallaInicio extends JFrame {

    // ── Paleta de colores ──────────────────────────────────────────────────────
    private static final Color BG_DARK       = new Color(10, 14, 23);
    private static final Color BG_CARD       = new Color(18, 24, 38);
    private static final Color ACCENT_CYAN   = new Color(0, 212, 255);
    private static final Color ACCENT_GREEN  = new Color(0, 230, 118);
    private static final Color ACCENT_RED    = new Color(255, 69, 58);
    private static final Color TEXT_PRIMARY  = new Color(240, 244, 255);
    private static final Color TEXT_MUTED    = new Color(120, 140, 170);
    private static final Color BORDER_SUBTLE = new Color(30, 42, 62);

    public PantallaInicio() {
        setTitle("Pistalia - Bienvenido");
        setSize(460, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);
        setResizable(false);

        // Panel principal con fondo degradado oscuro
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fondo base
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Halo de acento superior izquierda
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 212, 255, 40),
                        getWidth(), getHeight(), new Color(0, 0, 0, 0)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(new EmptyBorder(60, 55, 50, 55));

        // ── Logotipo ───────────────────────────────────────────────────────────
        JPanel pnlLogo = new JPanel();
        pnlLogo.setLayout(new BoxLayout(pnlLogo, BoxLayout.Y_AXIS));
        pnlLogo.setOpaque(false);
        pnlLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icono decorativo (rombo de acento)
        JLabel lblIcono = new JLabel("◈") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Halo brillante
                g2.setColor(new Color(0, 212, 255, 60));
                g2.fillOval(-10, -10, getWidth() + 20, getHeight() + 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcono.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 38));
        lblIcono.setForeground(ACCENT_CYAN);
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("PISTALIA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 46));
        lblTitulo.setForeground(TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("CENTRO DEPORTIVO");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitulo.setForeground(ACCENT_CYAN);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Línea separadora
        JSeparator sep = new JSeparator() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        getWidth() / 2f, 0, ACCENT_CYAN,
                        true
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sep.setMaximumSize(new Dimension(200, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlLogo.add(lblIcono);
        pnlLogo.add(Box.createVerticalStrut(8));
        pnlLogo.add(lblTitulo);
        pnlLogo.add(Box.createVerticalStrut(4));
        pnlLogo.add(lblSubtitulo);
        pnlLogo.add(Box.createVerticalStrut(18));
        pnlLogo.add(sep);

        // ── Botones ────────────────────────────────────────────────────────────
        JButton btnLogin    = crearBotonEstilizado("Iniciar Sesión",        ACCENT_CYAN);
        JButton btnInvitado = crearBotonEstilizado("Entrar como Invitado",  new Color(40, 58, 85));
        JButton btnSalir    = crearBotonEstilizado("Salir",                 new Color(60, 20, 20));

        // Ajuste de color de texto para botón invitado
        btnInvitado.setForeground(TEXT_MUTED);

        btnLogin.addActionListener(e -> {
            new PantallaLogin().setVisible(true);
            dispose();
        });

        btnInvitado.addActionListener(e -> {
            Usuario invitado = new Usuario();
            invitado.setNombre("Invitado");
            invitado.setEsAdmin(false);
            new PantallaPrincipal(invitado).setVisible(true);
            dispose();
        });

        btnSalir.addActionListener(e -> System.exit(0));

        // Etiqueta de versión
        JLabel lblVersion = new JLabel("v2.0  •  © 2025 Pistalia");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblVersion.setForeground(new Color(60, 80, 110));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Ensamblaje ─────────────────────────────────────────────────────────
        panelPrincipal.add(pnlLogo);
        panelPrincipal.add(Box.createVerticalStrut(50));
        panelPrincipal.add(btnLogin);
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(btnInvitado);
        panelPrincipal.add(Box.createVerticalStrut(30));
        panelPrincipal.add(btnSalir);
        panelPrincipal.add(Box.createVerticalGlue());
        panelPrincipal.add(lblVersion);

        setContentPane(panelPrincipal);
    }

    /** Crea un botón redondeado con hover suave. Color de texto auto-detectado. */
    public static JButton crearBotonEstilizado(String texto, Color colorFondo) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                // Texto a mano para no perder el color
                FontMetrics fm = getFontMetrics(getFont());
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g3.setColor(getForeground());
                g3.setFont(getFont());
                g3.drawString(getText(), x, y);
                g3.dispose();
            }
        };
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(320, 48));
        boton.setPreferredSize(new Dimension(320, 48));
        boton.setBackground(colorFondo);

        // Color de texto: oscuro si el fondo es muy claro (ej. cyan), blanco si oscuro
        double luminance = 0.299 * colorFondo.getRed() + 0.587 * colorFondo.getGreen() + 0.114 * colorFondo.getBlue();
        boton.setForeground(luminance > 160 ? new Color(10, 14, 23) : new Color(240, 244, 255));

        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(false);

        Color hoverColor = colorFondo.brighter();
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { boton.setBackground(hoverColor); boton.repaint(); }
            public void mouseExited(MouseEvent e)  { boton.setBackground(colorFondo);  boton.repaint(); }
        });

        return boton;
    }

    public static void main(String[] args) {
        // Apariencia oscura nativa
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new PantallaInicio().setVisible(true));
    }
}