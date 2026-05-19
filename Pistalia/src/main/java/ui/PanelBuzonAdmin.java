package ui;

import dao.MensajeDaoSQLite;
import entidades.Mensaje;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PanelBuzonAdmin extends JPanel {

    private static final Color BG           = new Color(16, 22, 36);
    private static final Color CARD_BG      = new Color(20, 28, 46);
    private static final Color ACCENT_CYAN  = new Color(0, 212, 255);
    private static final Color ACCENT_GREEN = new Color(0, 230, 118);
    private static final Color TEXT_PRIMARY = new Color(240, 244, 255);
    private static final Color TEXT_MUTED   = new Color(120, 140, 170);

    private MensajeDaoSQLite mensajeDAO;
    private JPanel panelLista;

    public PanelBuzonAdmin() {
        this.mensajeDAO = new MensajeDaoSQLite();
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel titulo = new JLabel("Buzón de Usuarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXT_PRIMARY);
        titulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(panelLista);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        cargarMensajes();
    }

    private void cargarMensajes() {
        panelLista.removeAll();
        List<Mensaje> lista = mensajeDAO.obtenerTodos();

        if (lista.isEmpty()) {
            JLabel empty = new JLabel("No hay mensajes en el buzón.");
            empty.setForeground(TEXT_MUTED);
            panelLista.add(empty);
        } else {
            for (Mensaje m : lista) {
                panelLista.add(crearFilaMensaje(m));
                panelLista.add(Box.createVerticalStrut(10));
            }
        }
        panelLista.revalidate();
        panelLista.repaint();
    }

    private JPanel crearFilaMensaje(Mensaje m) {
        boolean respondido = m.getRespuesta() != null && !m.getRespuesta().isEmpty();

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(respondido ? new Color(0, 230, 118, 80) : new Color(255, 165, 60, 80));
                g2.fill(new RoundRectangle2D.Float(0, 8, 4, getHeight()-16, 4, 4));
                g2.dispose();
            }
        };
        fila.setLayout(new BorderLayout());
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(15, 20, 15, 20));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);

        JLabel lblUsuario = new JLabel(m.getNombreUsuario() + "  ·  " + m.getFecha());
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(ACCENT_CYAN);

        JLabel lblMsg = new JLabel("<html><p style='width: 500px; color: white;'>" + m.getTextoMensaje() + "</p></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        pnlCentro.add(lblUsuario);
        pnlCentro.add(Box.createVerticalStrut(5));
        pnlCentro.add(lblMsg);

        JPanel pnlDerecha = new JPanel(new BorderLayout());
        pnlDerecha.setOpaque(false);

        if (respondido) {
            JLabel badge = new JLabel("✓ Respondido");
            badge.setForeground(ACCENT_GREEN);
            badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            pnlDerecha.add(badge, BorderLayout.CENTER);

            JLabel respAbajo = new JLabel("<html><p style='color: #00e676;'>Tú: " + m.getRespuesta() + "</p></html>");
            pnlCentro.add(Box.createVerticalStrut(8));
            pnlCentro.add(respAbajo);
        } else {
            JButton btnResponder = new JButton("Responder") {{
                setBackground(new Color(255, 165, 60));
                setForeground(new Color(10, 14, 23));
                setFocusPainted(false); setBorderPainted(false);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }};
            btnResponder.addActionListener(e -> {
                String resp = JOptionPane.showInputDialog(this, "Escribe tu respuesta para " + m.getNombreUsuario() + ":");
                if (resp != null && !resp.trim().isEmpty()) {
                    if (mensajeDAO.responderMensaje(m.getId(), resp.trim())) {
                        cargarMensajes();
                    }
                }
            });
            pnlDerecha.add(btnResponder, BorderLayout.CENTER);
        }

        fila.add(pnlCentro, BorderLayout.CENTER);
        fila.add(pnlDerecha, BorderLayout.EAST);

        return fila;
    }
}