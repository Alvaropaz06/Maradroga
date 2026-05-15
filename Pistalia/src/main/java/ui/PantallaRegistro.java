package ui;

import dao.UsuarioDAOSQLite;
import servicios.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PantallaRegistro extends JFrame {

    private UsuarioService usuarioService;

    public PantallaRegistro() {
        this.usuarioService = new UsuarioService(new UsuarioDAOSQLite());

        setTitle("Pistalia - Crear Cuenta");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(panelPrincipal);

        JLabel titulo = new JLabel("Nueva Cuenta", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- CAMPOS ---
        JLabel lblNombre = new JLabel("Nombre completo:");
        JTextField txtNombre = new JTextField();
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lblEmail = new JLabel("Correo electrónico:");
        JTextField txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel lblPassword = new JLabel("Contraseña:");
        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // --- BOTONES ---
        JButton btnRegistrar = PantallaInicio.crearBotonEstilizado("Registrarse", new Color(155, 89, 182)); // Color morado
        JButton btnVolver = PantallaInicio.crearBotonEstilizado("Volver", new Color(149, 165, 166));

        // --- ACCIONES ---
        btnRegistrar.addActionListener(e -> {
            String nombre = txtNombre.getText();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());

            if(nombre.isEmpty() || email.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(null, "Por favor, rellena todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Aquí llamamos a tu servicio para que guarde el usuario en SQLite
                usuarioService.registrarUsuario(nombre, email, password);
                JOptionPane.showMessageDialog(null, "¡Cuenta creada con éxito! Ya puedes iniciar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                new PantallaLogin().setVisible(true);
                dispose(); // Cierra esta ventana
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error al registrar", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> {
            new PantallaInicio().setVisible(true);
            dispose();
        });

        // --- ENSAMBLAJE ---
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createVerticalStrut(25));

        panelPrincipal.add(lblNombre);
        panelPrincipal.add(txtNombre);
        panelPrincipal.add(Box.createVerticalStrut(15));

        panelPrincipal.add(lblEmail);
        panelPrincipal.add(txtEmail);
        panelPrincipal.add(Box.createVerticalStrut(15));

        panelPrincipal.add(lblPassword);
        panelPrincipal.add(txtPassword);
        panelPrincipal.add(Box.createVerticalStrut(30));

        panelPrincipal.add(btnRegistrar);
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(btnVolver);
    }
}