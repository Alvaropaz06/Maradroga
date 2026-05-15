package ui;

import entidades.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PantallaInicio extends JFrame {

    public PantallaInicio() {
        setTitle("Pistalia - Bienvenido");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(new Color(245, 247, 250));
        panelPrincipal.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel lblTitulo = new JLabel("PISTALIA");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblTitulo.setForeground(new Color(44, 62, 80));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Centro Deportivo");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Solo los botones que has pedido
        JButton btnLogin = crearBotonEstilizado("Iniciar Sesión", new Color(52, 152, 219));
        JButton btnInvitado = crearBotonEstilizado("Entrar como Invitado", new Color(189, 195, 199));
        JButton btnSalir = crearBotonEstilizado("Salir", new Color(231, 76, 60));

        btnLogin.addActionListener(e -> {
            new PantallaLogin().setVisible(true);
            dispose();
        });

        // La lógica del invitado
        btnInvitado.addActionListener(e -> {
            Usuario invitado = new Usuario();
            invitado.setNombre("Invitado");
            invitado.setEsAdmin(false);

            new PantallaPrincipal(invitado).setVisible(true);
            dispose();
        });

        btnSalir.addActionListener(e -> System.exit(0));

        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(lblSubtitulo);
        panelPrincipal.add(Box.createVerticalStrut(60));
        panelPrincipal.add(btnLogin);
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(btnInvitado);
        panelPrincipal.add(Box.createVerticalStrut(50));
        panelPrincipal.add(btnSalir);

        add(panelPrincipal, BorderLayout.CENTER);
    }

    // Método de diseño intacto
    public static JButton crearBotonEstilizado(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(300, 45));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorFondo.brighter());
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorFondo);
            }
        });

        return boton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PantallaInicio().setVisible(true);
        });
    }
}