package ui;

import dao.UsuarioDAO;
import dao.UsuarioDAOSQLite;
import entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.util.UUID;

public class PantallaLogin extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPass;
    private UsuarioDAO usuarioDAO;

    public PantallaLogin() {
        this.usuarioDAO = new UsuarioDAOSQLite();

        setTitle("Pistalia - Acceso");
        setSize(420, 620); // Aumentado ligeramente para mejorar el diseño
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // --- CABECERA DE LOGO Y TITULO (Corregido) ---
        // Usamos FlowLayout para poner logo y texto uno al lado del otrocentrados
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Limitamos la altura máxima de la cabecera
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel lblLogoImg = new JLabel();
        try {
            URL imgURL = getClass().getResource("/img/logo.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();

                // LÓGICA DE ESCALADO PROPORCIONAL
                int origW = img.getWidth(null);
                int origH = img.getHeight(null);
                int targetH = 80; // Altura deseada para la cabecera
                int targetW = (origW * targetH) / origH; // Calculamos ancho proporcional

                Image scaled = img.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                lblLogoImg.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) {
            // Si falla, no mostramos nada o un icono por defecto
        }
        pnlHeader.add(lblLogoImg);

        JLabel lblTitleText = new JLabel("PISTALIA");
        lblTitleText.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitleText.setForeground(new Color(44, 62, 80)); // Un tono gris azulado elegante
        pnlHeader.add(lblTitleText);


        txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35)); // Ligeramente más alto
        txtPass = new JPasswordField();
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JButton btnEntrar = new JButton("Iniciar Sesión");
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.setBackground(new Color(52, 152, 219));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // Botón más grande
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnRegistro = new JButton("¿No tienes cuenta? Regístrate");
        btnRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        estilizarBotonLink(btnRegistro);

        JButton btnOlvido = new JButton("¿Has olvidado tu contraseña?");
        btnOlvido.setAlignmentX(Component.CENTER_ALIGNMENT);
        estilizarBotonLink(btnOlvido);

        // Lógica de botones
        btnEntrar.addActionListener(e -> login());
        btnRegistro.addActionListener(e -> abrirDialogoRegistro());
        btnOlvido.addActionListener(e -> recuperarContrasena());

        // Añadimos componentes al panel principal
        panel.add(pnlHeader);
        panel.add(Box.createVerticalStrut(30));

        // Estilizamos etiquetas de campo
        JLabel lblEmail = new JLabel("Email de usuario:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblEmail);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtEmail);

        panel.add(Box.createVerticalStrut(15));

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblPass);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtPass);

        panel.add(Box.createVerticalStrut(30));
        panel.add(btnEntrar);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnRegistro);
        panel.add(btnOlvido);

        add(panel);
    }

    private void login() {
        String email = txtEmail.getText().trim();
        String pass = new String(txtPass.getPassword());

        if(email.isEmpty() || pass.isEmpty()){
            JOptionPane.showMessageDialog(this, "Rellena todos los campos");
            return;
        }

        Usuario u = usuarioDAO.login(email, pass);
        if (u != null) {
            new PantallaPrincipal(u).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Email o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recuperarContrasena() {
        String email = JOptionPane.showInputDialog(this,
                "Introduce el email con el que te registraste:",
                "Recuperar Contraseña",
                JOptionPane.QUESTION_MESSAGE);

        if (email != null && !email.trim().isEmpty()) {
            Usuario u = usuarioDAO.buscarPorEmail(email.trim());

            if (u != null) {
                JOptionPane.showMessageDialog(this,
                        "¡Usuario localizado!\n\n" +
                                "Se ha enviado un código de seguridad a: " + email + "\n" +
                                "(Simulación: Tu contraseña actual es: " + u.getPassword() + ")",
                        "Recuperación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No existe ningún usuario registrado con ese email.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirDialogoRegistro() {
        JDialog dialogo = new JDialog(this, "Crear Cuenta", true);
        dialogo.setLayout(new GridLayout(0, 1, 10, 10));
        dialogo.setSize(350, 400);
        dialogo.setLocationRelativeTo(this);
        ((JPanel)dialogo.getContentPane()).setBorder(new EmptyBorder(20,20,20,20));

        JTextField regUser = new JTextField();
        JTextField regEmail = new JTextField();
        JPasswordField regPass = new JPasswordField();
        JButton btnConfirmar = new JButton("Registrarme");

        dialogo.add(new JLabel("Nombre Completo:"));
        dialogo.add(regUser);
        dialogo.add(new JLabel("Email:"));
        dialogo.add(regEmail);
        dialogo.add(new JLabel("Contraseña:"));
        dialogo.add(regPass);
        dialogo.add(new JLabel(""));
        dialogo.add(btnConfirmar);

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

    private void estilizarBotonLink(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(new Color(41, 128, 185));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PantallaLogin().setVisible(true));
    }
}