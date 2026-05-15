package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaOlvidoContrasena extends JFrame {

    public PantallaOlvidoContrasena() {
        setTitle("Pistalia - Recuperar Contraseña");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(panelPrincipal);

        JLabel titulo = new JLabel("Recuperar cuenta", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Texto explicativo
        JLabel lblExplicacion = new JLabel("<html>Introduce tu correo y te enviaremos<br>las instrucciones para recuperarla.</html>");
        lblExplicacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblExplicacion.setForeground(Color.DARK_GRAY);
        lblExplicacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEmail = new JLabel("Correo electrónico:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtEmail = new JTextField();
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Reutilizamos nuestro método creador de botones bonitos
        JButton btnEnviar = PantallaInicio.crearBotonEstilizado("Enviar correo", new Color(52, 152, 219)); // Color azul
        JButton btnVolver = PantallaInicio.crearBotonEstilizado("Volver", new Color(149, 165, 166)); // Color gris

        // --- ACCIONES ---
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String correo = txtEmail.getText();
                if (correo.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, introduce un correo válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Como no tenemos servidor de correo real, simulamos que se envía
                    JOptionPane.showMessageDialog(null,
                            "Si el correo " + correo + " está registrado,\nrecibirás un mensaje con instrucciones en breve.",
                            "Correo enviado",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Al "enviarlo", devolvemos al usuario al Login
                    new PantallaLogin().setVisible(true);
                    dispose();
                }
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PantallaLogin().setVisible(true);
                dispose();
            }
        });

        // --- ENSAMBLAJE ---
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(lblExplicacion);
        panelPrincipal.add(Box.createVerticalStrut(20));

        panelPrincipal.add(lblEmail);
        panelPrincipal.add(Box.createVerticalStrut(5));
        panelPrincipal.add(txtEmail);
        panelPrincipal.add(Box.createVerticalStrut(30));

        panelPrincipal.add(btnEnviar);
        panelPrincipal.add(Box.createVerticalStrut(10));
        panelPrincipal.add(btnVolver);
    }
}