package ui;

import dao.UsuarioDAOSQLite;
import servicios.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PantallaRegistro extends JFrame {

    private static final Color BG_DARK      = new Color(10, 14, 23);
    private static final Color ACCENT_CYAN  = new Color(0, 212, 255);
    private static final Color TEXT_PRIMARY = new Color(240, 244, 255);
    private static final Color TEXT_MUTED   = new Color(120, 140, 170);
    private static final Color BORDER_COLOR = new Color(30, 42, 62);
    private static final Color INPUT_BG     = new Color(24, 32, 50);

    private UsuarioService usuarioService;

    public PantallaRegistro() {
        this.usuarioService = new UsuarioService(new UsuarioDAOSQLite());

        setTitle("Pistalia - Crear Cuenta");
        setSize(420, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                GradientPaint gp = new GradientPaint(getWidth(), 0, new Color(0, 212, 255, 22),
                        0, getHeight(), new Color(0,0,0,0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(45, 45, 35, 45));

        // Cabecera
        JLabel lblIco = new JLabel("◈");
        lblIco.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        lblIco.setForeground(ACCENT_CYAN);
        lblIco.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Nueva Cuenta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Únete a Pistalia hoy");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campos
        JTextField txtNombre   = crearCampo();
        JTextField txtEmail    = crearCampo();
        JPasswordField txtPass = new JPasswordField();
        aplicarEstiloCampo(txtPass);

        // Botones
        JButton btnRegistrar = PantallaInicio.crearBotonEstilizado("Crear Cuenta", new Color(100, 60, 200));
        JButton btnVolver    = crearLink("← Volver al inicio");

        btnRegistrar.addActionListener(e -> {
            String nombre   = txtNombre.getText().trim();
            String email    = txtEmail.getText().trim();
            String password = new String(txtPass.getPassword());

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, rellena todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                usuarioService.registrarUsuario(nombre, email, password);
                JOptionPane.showMessageDialog(null, "¡Cuenta creada con éxito! Ya puedes iniciar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                new PantallaLogin().setVisible(true);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error al registrar", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> { new PantallaInicio().setVisible(true); dispose(); });

        // Ensamblaje
        panel.add(lblIco);
        panel.add(Box.createVerticalStrut(6));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(lblSub);
        panel.add(Box.createVerticalStrut(35));
        panel.add(crearEtiqueta("Nombre completo")); panel.add(Box.createVerticalStrut(5)); panel.add(txtNombre);
        panel.add(Box.createVerticalStrut(16));
        panel.add(crearEtiqueta("Correo electrónico")); panel.add(Box.createVerticalStrut(5)); panel.add(txtEmail);
        panel.add(Box.createVerticalStrut(16));
        panel.add(crearEtiqueta("Contraseña")); panel.add(Box.createVerticalStrut(5)); panel.add(txtPass);
        panel.add(Box.createVerticalStrut(28));
        panel.add(btnRegistrar);
        panel.add(Box.createVerticalStrut(14));
        panel.add(btnVolver);

        setContentPane(panel);
    }

    private JTextField crearCampo() {
        JTextField tf = new JTextField();
        aplicarEstiloCampo(tf);
        return tf;
    }

    private void aplicarEstiloCampo(JTextField tf) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT_CYAN);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_CYAN, 1, true),
                        new EmptyBorder(8, 14, 8, 14)));
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(8, 14, 8, 14)));
            }
        });
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JButton crearLink(String texto) {
        JButton btn = new JButton(texto);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
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
}