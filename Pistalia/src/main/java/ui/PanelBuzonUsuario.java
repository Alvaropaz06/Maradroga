package ui;

import dao.MensajeDaoSQLite;
import entidades.Mensaje;
import entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PanelBuzonUsuario extends JPanel {

    private static final Color BG           = new Color(16, 22, 36);
    private static final Color CARD_BG      = new Color(20, 28, 46);
    private static final Color ACCENT_CYAN  = new Color(0, 212, 255);
    private static final Color ACCENT_GREEN = new Color(0, 230, 118);
    private static final Color TEXT_PRIMARY = new Color(240, 244, 255);
    private static final Color TEXT_MUTED   = new Color(120, 140, 170);
    private static final Color INPUT_BG     = new Color(24, 32, 50);

    private Usuario usuario;
    private MensajeDaoSQLite mensajeDAO;
    private JPanel panelLista;

    public PanelBuzonUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.mensajeDAO = new MensajeDaoSQLite();

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // Cabecera y Formulario
        JPanel pnlNorte = new JPanel(new BorderLayout());
        pnlNorte.setOpaque(false);

        JLabel titulo = new JLabel("Buzón y Ayuda");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXT_PRIMARY);
        pnlNorte.add(titulo, BorderLayout.NORTH);

        JPanel pnlEnviar = new JPanel(new BorderLayout(10, 10));
        pnlEnviar.setOpaque(false);
        pnlEnviar.setBorder(new EmptyBorder(15, 0, 20, 0));

        JTextArea txtMensaje = new JTextArea(3, 20);
        txtMensaje.setBackground(INPUT_BG);
        txtMensaje.setForeground(TEXT_PRIMARY);
        txtMensaje.setCaretColor(ACCENT_CYAN);
        txtMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMensaje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(28, 40, 62)),
                new EmptyBorder(10, 10, 10, 10)));
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);

        JButton btnEnviar = new JButton("Enviar Consulta") {{
            setBackground(ACCENT_CYAN);
            setForeground(new Color(10, 14, 23));
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false); setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }};

        btnEnviar.addActionListener(e -> {
            String texto = txtMensaje.getText().trim();
            if (!texto.isEmpty()) {
                Mensaje m = new Mensaje();
                m.setId(UUID.randomUUID().toString());
                m.setIdUsuario(usuario.getId());
                m.setNombreUsuario(usuario.getNombre());
                m.setTextoMensaje(texto);
                m.setFecha(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
                if(mensajeDAO.enviarMensaje(m)) {
                    txtMensaje.setText("");
                    cargarMensajes();
                }
            }
        });

        pnlEnviar.add(new JScrollPane(txtMensaje), BorderLayout.CENTER);
        pnlEnviar.add(btnEnviar, BorderLayout.EAST);
        pnlNorte.add(pnlEnviar, BorderLayout.CENTER);

        add(pnlNorte, BorderLayout.NORTH);

        // Lista de mensajes
        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(panelLista);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        cargarMensajes();
    }

    private void cargarMensajes() {
        panelLista.removeAll();
        List<Mensaje> lista = mensajeDAO.obtenerPorUsuario(usuario.getId());

        if (lista.isEmpty()) {
            JLabel empty = new JLabel("Aún no has enviado ninguna consulta.");
            empty.setForeground(TEXT_MUTED);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panelLista.add(empty);
        } else {
            for (Mensaje m : lista) {
                panelLista.add(crearTarjetaMensaje(m));
                panelLista.add(Box.createVerticalStrut(10));
            }
        }
        panelLista.revalidate();
        panelLista.repaint();
    }

    private JPanel crearTarjetaMensaje(Mensaje m) {
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // Borde izquierdo
                g2.setColor(m.getRespuesta() != null ? ACCENT_GREEN : new Color(255, 165, 60));
                g2.fill(new RoundRectangle2D.Float(0, 8, 4, getHeight()-16, 4, 4));
                g2.dispose();
            }
        };
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(15, 20, 15, 20));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFecha = new JLabel("Enviado el " + m.getFecha());
        lblFecha.setForeground(TEXT_MUTED);
        lblFecha.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        JLabel lblTexto = new JLabel("<html><p style='width: 600px; color: white;'>" + m.getTextoMensaje() + "</p></html>");
        lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        fila.add(lblFecha);
        fila.add(Box.createVerticalStrut(5));
        fila.add(lblTexto);

        if (m.getRespuesta() != null && !m.getRespuesta().isEmpty()) {
            fila.add(Box.createVerticalStrut(15));
            JPanel pnlResp = new JPanel(new BorderLayout());
            pnlResp.setOpaque(false);
            pnlResp.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, ACCENT_GREEN));
            JLabel lblResp = new JLabel("<html><p style='width: 580px; color: #00e676;'><b>Respuesta Admin:</b><br>" + m.getRespuesta() + "</p></html>");
            lblResp.setBorder(new EmptyBorder(0, 10, 0, 0));
            pnlResp.add(lblResp, BorderLayout.CENTER);
            fila.add(pnlResp);
        } else {
            fila.add(Box.createVerticalStrut(10));
            JLabel lblPend = new JLabel("Pendiente de respuesta...");
            lblPend.setForeground(new Color(255, 165, 60));
            lblPend.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            fila.add(lblPend);
        }

        return fila;
    }
}