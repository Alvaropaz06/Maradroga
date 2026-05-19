package ui;

import dao.UsuarioDAO;
import dao.UsuarioDAOSQLite;
import entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

public class PantallaLogin extends JFrame {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(10, 14, 23);
    private static final Color BG_CARD      = new Color(18, 24, 38);
    private static final Color ACCENT_CYAN  = new Color(0, 212, 255);
    private static final Color TEXT_PRIMARY = new Color(240, 244, 255);
    private static final Color TEXT_MUTED   = new Color(120, 140, 170);
    private static final Color BORDER_COLOR = new Color(30, 42, 62);
    private static final Color INPUT_BG     = new Color(24, 32, 50);

    private JTextField    txtEmail;
    private JPasswordField txtPass;
    private UsuarioDAO    usuarioDAO;

    public PantallaLogin() {
        this.usuarioDAO = new UsuarioDAOSQLite();

        setTitle("Pistalia - Acceso");
        setSize(430, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint gp = new GradientPaint(
                        getWidth(), 0, new Color(0, 212, 255, 25),
                        0, getHeight(), new Color(0, 0, 0, 0)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 45, 35, 45));

        // ── Cabecera ───────────────────────────────────────────────────────────
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblLogoImg = new JLabel();
        try {
            URL imgURL = getClass().getResource("/img/logo.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                int origW = img.getWidth(null), origH = img.getHeight(null);
                int tH = 68, tW = (origW * tH) / origH;
                lblLogoImg.setIcon(new ImageIcon(img.getScaledInstance(tW, tH, Image.SCALE_SMOOTH)));
            }
        } catch (Exception ignored) {}
        pnlHeader.add(lblLogoImg);

        JLabel lblTitle = new JLabel("PISTALIA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblTitle.setForeground(TEXT_PRIMARY);
        pnlHeader.add(lblTitle);

        JLabel lblSub = new JLabel("Bienvenido de vuelta");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Campos ─────────────────────────────────────────────────────────────
        txtEmail = crearCampoTexto("correo@ejemplo.com", false);
        txtPass  = (JPasswordField) crearCampoTexto("••••••••", true);

        JLabel lblEmail = crearEtiqueta("Correo electrónico");
        JLabel lblPass  = crearEtiqueta("Contraseña");

        // ── Botones ────────────────────────────────────────────────────────────
        JButton btnEntrar   = PantallaInicio.crearBotonEstilizado("Iniciar Sesión", ACCENT_CYAN);
        JButton btnRegistro = crearBotonLink("¿No tienes cuenta? Regístrate");
        JButton btnOlvido   = crearBotonLink("¿Olvidaste tu contraseña?");

        btnEntrar.addActionListener(e -> login());
        txtPass.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) login();
            }
        });
        btnRegistro.addActionListener(e -> abrirDialogoRegistro());
        btnOlvido.addActionListener(e -> recuperarContrasena());

        // ── Ensamblaje ─────────────────────────────────────────────────────────
        panel.add(pnlHeader);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lblSub);
        panel.add(Box.createVerticalStrut(35));
        panel.add(lblEmail);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtEmail);
        panel.add(Box.createVerticalStrut(18));
        panel.add(lblPass);
        panel.add(Box.createVerticalStrut(6));
        panel.add(txtPass);
        panel.add(Box.createVerticalStrut(28));
        panel.add(btnEntrar);
        panel.add(Box.createVerticalStrut(22));

        JSeparator sep = crearSeparador();
        panel.add(sep);
        panel.add(Box.createVerticalStrut(14));
        panel.add(btnRegistro);
        panel.add(Box.createVerticalStrut(4));
        panel.add(btnOlvido);

        setContentPane(panel);
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearCampoTexto(String placeholder, boolean esPassword) {
        JTextField campo = esPassword ? new JPasswordField() : new JTextField();
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        campo.setPreferredSize(new Dimension(340, 42));
        campo.setBackground(INPUT_BG);
        campo.setForeground(TEXT_PRIMARY);
        campo.setCaretColor(ACCENT_CYAN);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        // Placeholder
        if (!esPassword) {
            campo.setForeground(TEXT_MUTED);
            campo.setText(placeholder);
            campo.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (campo.getText().equals(placeholder)) {
                        campo.setText("");
                        campo.setForeground(TEXT_PRIMARY);
                    }
                }
                public void focusLost(FocusEvent e) {
                    if (campo.getText().isEmpty()) {
                        campo.setForeground(TEXT_MUTED);
                        campo.setText(placeholder);
                    }
                }
            });
        }
        // Borde de foco
        campo.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_CYAN, 1, true),
                        new EmptyBorder(8, 14, 8, 14)
                ));
            }
            public void focusLost(FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(8, 14, 8, 14)
                ));
            }
        });
        return campo;
    }

    private JButton crearBotonLink(String texto) {
        JButton btn = new JButton(texto);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(new Color(0, 180, 220));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(ACCENT_CYAN); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(new Color(0, 180, 220)); }
        });
        return btn;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(0,0,0,0),
                        getWidth()/2f, 0, BORDER_COLOR, true);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ── Lógica (sin cambios) ───────────────────────────────────────────────────

    private void login() {
        String email = txtEmail.getText().trim();
        String pass  = new String(txtPass.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            mostrarError("Rellena todos los campos");
            return;
        }
        Usuario u = usuarioDAO.login(email, pass);
        if (u != null) {
            new PantallaPrincipal(u).setVisible(true);
            dispose();
        } else {
            mostrarError("Email o contraseña incorrectos");
        }
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void recuperarContrasena() {
        String email = JOptionPane.showInputDialog(this,
                "Introduce el email con el que te registraste:",
                "Recuperar Contraseña", JOptionPane.QUESTION_MESSAGE);
        if (email != null && !email.trim().isEmpty()) {
            Usuario u = usuarioDAO.buscarPorEmail(email.trim());
            if (u != null) {
                JOptionPane.showMessageDialog(this,
                        "¡Usuario localizado!\n\nSe ha enviado un código de seguridad a: " + email +
                                "\n(Simulación: Tu contraseña actual es: " + u.getPassword() + ")",
                        "Recuperación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No existe ningún usuario registrado con ese email.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoRegistro() {
        JDialog dialogo = new JDialog(this, "Crear Cuenta", true);
        dialogo.setSize(360, 380);
        dialogo.setLocationRelativeTo(this);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_CARD);
        p.setBorder(new EmptyBorder(25, 30, 25, 30));

        JTextField regUser  = crearCampoTexto("", false);
        JTextField regEmail = crearCampoTexto("", false);
        JPasswordField regPass = (JPasswordField) crearCampoTexto("", true);

        JButton btnConfirmar = PantallaInicio.crearBotonEstilizado("Registrarme", ACCENT_CYAN);
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(crearEtiqueta("Nombre Completo")); p.add(Box.createVerticalStrut(5)); p.add(regUser);
        p.add(Box.createVerticalStrut(14));
        p.add(crearEtiqueta("Email"));           p.add(Box.createVerticalStrut(5)); p.add(regEmail);
        p.add(Box.createVerticalStrut(14));
        p.add(crearEtiqueta("Contraseña"));      p.add(Box.createVerticalStrut(5)); p.add(regPass);
        p.add(Box.createVerticalStrut(24));
        p.add(btnConfirmar);

        dialogo.setContentPane(p);

        btnConfirmar.addActionListener(e -> {
            String email = regEmail.getText().trim();
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(dialogo, "Formato de email no válido.");
                return;
            }
            Usuario nuevo = new Usuario();
            nuevo.setNombre(regUser.getText().trim());
            nuevo.setEmail(email);
            nuevo.setPassword(new String(regPass.getPassword()));
            nuevo.setEsAdmin(false);
            if (usuarioDAO.registrar(nuevo)) {
                JOptionPane.showMessageDialog(dialogo, "¡Cuenta creada correctamente!");
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, "Error al registrar. El email ya podría estar en uso.");
            }
        });

        dialogo.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PantallaLogin().setVisible(true));
    }
}