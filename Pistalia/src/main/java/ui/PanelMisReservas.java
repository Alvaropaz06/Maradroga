package ui;

import dao.MaterialDaoSQlite;
import dao.ReservaDAO;
import dao.ReservaDaoSQLite;
import entidades.Reserva;
import entidades.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelMisReservas extends JPanel {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG           = new Color(16, 22, 36);
    private static final Color CARD_BG      = new Color(20, 28, 46);
    private static final Color ACCENT_CYAN  = new Color(0, 212, 255);
    private static final Color ACCENT_GREEN = new Color(0, 230, 118);
    private static final Color ACCENT_RED   = new Color(255, 80, 70);
    private static final Color ACCENT_ORG   = new Color(255, 165, 60);
    private static final Color TEXT_PRIMARY = new Color(240, 244, 255);
    private static final Color TEXT_MUTED   = new Color(120, 140, 170);
    private static final Color BORDER_COLOR = new Color(28, 40, 62);
    private static final Color TAB_ACTIVE   = new Color(0, 212, 255, 40);

    private Usuario usuario;
    private ReservaDAO reservaDAO;
    private JPanel panelProximas;
    private JPanel panelHistorial;
    private DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PanelMisReservas(Usuario usuario) {
        this.usuario = usuario;
        this.reservaDAO = new ReservaDaoSQLite();

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // ── Cabecera ───────────────────────────────────────────────────────────
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titulo = new JLabel("Mis Reservas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Gestiona tus partidos y cancelaciones");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);

        JPanel pnlTitTxt = new JPanel();
        pnlTitTxt.setLayout(new BoxLayout(pnlTitTxt, BoxLayout.Y_AXIS));
        pnlTitTxt.setOpaque(false);
        pnlTitTxt.add(titulo);
        pnlTitTxt.add(Box.createVerticalStrut(3));
        pnlTitTxt.add(lblSub);
        pnlTop.add(pnlTitTxt, BorderLayout.WEST);
        add(pnlTop, BorderLayout.NORTH);

        // ── Tabs personalizados ────────────────────────────────────────────────
        JPanel pnlTabs = new JPanel(new BorderLayout());
        pnlTabs.setOpaque(false);

        // Barra de tabs
        JPanel barTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barTabs.setOpaque(false);
        barTabs.setBorder(new EmptyBorder(0, 0, 0, 0));

        panelProximas  = crearPanelScroll();
        panelHistorial = crearPanelScroll();

        JPanel[] paneles = {panelProximas, panelHistorial};
        String[] tabs    = {"Próximos Partidos", "Historial de Juego"};
        JButton[] btnTabs = new JButton[2];

        JPanel pnlContenidoTabs = new JPanel(new CardLayout());
        pnlContenidoTabs.setOpaque(false);
        pnlContenidoTabs.add(new JScrollPane(panelProximas)  {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, "0");
        pnlContenidoTabs.add(new JScrollPane(panelHistorial) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, "1");

        for (int i = 0; i < 2; i++) {
            final int idx = i;
            JButton btnTab = crearBotonTab(tabs[i], i == 0);
            btnTabs[i] = btnTab;
            btnTab.addActionListener(e -> {
                for (JButton b : btnTabs) setTabActivo(b, false);
                setTabActivo(btnTab, true);
                ((CardLayout) pnlContenidoTabs.getLayout()).show(pnlContenidoTabs, String.valueOf(idx));
            });
            barTabs.add(btnTab);
            if (i < 1) barTabs.add(Box.createHorizontalStrut(4));
        }

        pnlTabs.add(barTabs, BorderLayout.NORTH);
        pnlTabs.add(pnlContenidoTabs, BorderLayout.CENTER);
        add(pnlTabs, BorderLayout.CENTER);

        organizarReservas();
        lanzarNotificacionEmergente();
    }

    private JPanel crearPanelScroll() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(14, 0, 0, 0));
        return p;
    }

    private JButton crearBotonTab(String texto, boolean activo) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                if (isSelected()) {
                    // Borde inferior de acento
                    g2.setColor(ACCENT_CYAN);
                    g2.fillRect(8, getHeight() - 2, getWidth() - 16, 2);
                }
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 38));
        setTabActivo(btn, activo);
        return btn;
    }

    private void setTabActivo(JButton btn, boolean activo) {
        btn.setSelected(activo);
        btn.setBackground(activo ? new Color(0, 212, 255, 25) : new Color(20, 28, 46));
        btn.setForeground(activo ? ACCENT_CYAN : TEXT_MUTED);
        btn.repaint();
    }

    private void organizarReservas() {
        panelProximas.removeAll();
        panelHistorial.removeAll();

        List<Reserva> todas = reservaDAO.obtenerPorUsuario(usuario.getId());
        LocalDateTime ahora = LocalDateTime.now();

        for (Reserva r : todas) {
            try {
                LocalDateTime fechaReserva = LocalDateTime.parse(r.getFechaHoraInicio(), formateador);
                if (fechaReserva.isBefore(ahora)) {
                    panelHistorial.add(crearTarjetaReserva(r, false));
                    panelHistorial.add(Box.createVerticalStrut(12));
                } else {
                    panelProximas.add(crearTarjetaReserva(r, true));
                    panelProximas.add(Box.createVerticalStrut(12));
                }
            } catch (Exception e) {
                panelProximas.add(crearTarjetaReserva(r, true));
            }
        }

        if (panelProximas.getComponentCount() == 0) {
            panelProximas.add(crearMensajeVacio("No tienes partidos programados.", "Reserva una pista para empezar."));
        }
        if (panelHistorial.getComponentCount() == 0) {
            panelHistorial.add(crearMensajeVacio("Sin historial todavía.", "Aquí aparecerán tus partidos jugados."));
        }

        panelProximas.revalidate(); panelProximas.repaint();
        panelHistorial.revalidate(); panelHistorial.repaint();
    }

    private JPanel crearMensajeVacio(String titulo, String sub) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(40, 0, 0, 0));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(titulo);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub2 = new JLabel(sub);
        sub2.setForeground(new Color(80, 100, 130));
        sub2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(6)); p.add(sub2);
        return p;
    }

    private JPanel crearTarjetaReserva(Reserva r, boolean esFutura) {
        JPanel tarjeta = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // Borde de color según estado
                Color borderCol = esFutura
                        ? (r.isPistaDisponible() ? new Color(0, 212, 255, 60) : new Color(255, 80, 70, 80))
                        : new Color(40, 55, 80);
                g2.setColor(borderCol);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 12, 12));
                // Barra lateral
                g2.setColor(esFutura ? (r.isPistaDisponible() ? ACCENT_CYAN : ACCENT_RED) : new Color(60, 80, 110));
                g2.fill(new RoundRectangle2D.Float(0, 12, 4, getHeight()-24, 4, 4));
                g2.dispose();
            }
        };
        tarjeta.setLayout(new BorderLayout(14, 0));
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(14, 20, 14, 16));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Imagen
        JLabel lblImagen = crearImagenPista(r.getIdPista(), r.getDeportePista(), 110, 100);
        tarjeta.add(lblImagen, BorderLayout.WEST);

        // Info central
        String idCompleto = r.getIdPista();
        String nombrePista = idCompleto;
        if (idCompleto.contains(" (") && idCompleto.endsWith(")"))
            nombrePista = idCompleto.substring(0, idCompleto.lastIndexOf(" ("));

        String deporte = r.getDeportePista() != null ? "  ·  " + r.getDeportePista() : "";

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);

        JLabel lblFecha = new JLabel(r.getFechaHoraInicio());
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFecha.setForeground(TEXT_PRIMARY);

        JLabel lblPista = new JLabel("Pista: " + nombrePista + deporte);
        lblPista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPista.setForeground(TEXT_MUTED);

        JLabel lblPrecio = new JLabel(String.format("Total: %.2f€", r.getPrecioTotal()));
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPrecio.setForeground(ACCENT_CYAN);

        String textoEstado = esFutura ? r.getEstado() : "Finalizada";
        Color colorEstado = esFutura
                ? (r.isPistaDisponible() ? ACCENT_GREEN : ACCENT_RED)
                : new Color(100, 120, 150);
        JLabel lblEstado = new JLabel("● " + textoEstado);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstado.setForeground(colorEstado);

        pnlInfo.add(lblFecha);
        pnlInfo.add(Box.createVerticalStrut(4));
        pnlInfo.add(lblPista);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblPrecio);
        pnlInfo.add(Box.createVerticalStrut(5));
        pnlInfo.add(lblEstado);

        if (esFutura && !r.isPistaDisponible()) {
            JLabel lblAviso = new JLabel("⚠ Pista cerrada por mantenimiento");
            lblAviso.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblAviso.setForeground(ACCENT_ORG);
            pnlInfo.add(Box.createVerticalStrut(3));
            pnlInfo.add(lblAviso);
        }

        tarjeta.add(pnlInfo, BorderLayout.CENTER);

        // Acción derecha
        if (esFutura) {
            JButton btnCancelar = new JButton("Cancelar") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                    g2.setColor(getForeground());
                    FontMetrics fm = g2.getFontMetrics(getFont());
                    g2.setFont(getFont());
                    g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                            (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    g2.dispose();
                }
            };
            btnCancelar.setPreferredSize(new Dimension(90, 34));
            btnCancelar.setBackground(new Color(60, 20, 20));
            btnCancelar.setForeground(ACCENT_RED);
            btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCancelar.setFocusPainted(false); btnCancelar.setBorderPainted(false); btnCancelar.setContentAreaFilled(false);
            btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnCancelar.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btnCancelar.setBackground(new Color(90, 25, 25)); btnCancelar.repaint(); }
                public void mouseExited(MouseEvent e)  { btnCancelar.setBackground(new Color(60, 20, 20)); btnCancelar.repaint(); }
            });
            btnCancelar.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Cancelar este partido?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (r.getMaterialesAlquilados() != null && !r.getMaterialesAlquilados().isEmpty()) {
                        MaterialDaoSQlite matDao = new MaterialDaoSQlite();
                        for (String idMat : r.getMaterialesAlquilados().split(",")) {
                            if (!idMat.trim().isEmpty()) matDao.devolverMaterial(idMat.trim());
                        }
                    }
                    if (reservaDAO.eliminar(r.getId())) organizarReservas();
                }
            });
            tarjeta.add(btnCancelar, BorderLayout.EAST);
        } else {
            JLabel lblCheck = new JLabel("✓");
            lblCheck.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblCheck.setForeground(new Color(60, 80, 110));
            tarjeta.add(lblCheck, BorderLayout.EAST);
        }

        return tarjeta;
    }

    private JLabel crearImagenPista(String idPista, String deporte, int width, int height) {
        JLabel lblFoto = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 50, 80),
                        getWidth(), getHeight(), new Color(10, 25, 45));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblFoto.setPreferredSize(new Dimension(width, height));
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            String depLimpio = deporte != null ? deporte.toLowerCase()
                    .replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u") : "default";
            int n = 1;
            String nums = idPista.replaceAll("\\D+", "");
            if (!nums.isEmpty()) n = ((Integer.parseInt(nums)-1) % 5) + 1;
            else n = (Math.abs(idPista.hashCode()) % 5) + 1;
            java.net.URL imgURL = getClass().getResource("/img/" + depLimpio + n + ".jpg");
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                lblFoto.setIcon(new ImageIcon(img));
            } else {
                lblFoto.setText(deporte != null ? deporte.substring(0,1) : "?");
                lblFoto.setForeground(ACCENT_CYAN);
                lblFoto.setFont(new Font("Segoe UI", Font.BOLD, 22));
            }
        } catch (Exception e) { lblFoto.setText("?"); }
        return lblFoto;
    }

    private void lanzarNotificacionEmergente() {
        List<Reserva> reservas = reservaDAO.obtenerPorUsuario(usuario.getId());
        LocalDateTime ahora = LocalDateTime.now();
        for (Reserva r : reservas) {
            try {
                LocalDateTime fecha = LocalDateTime.parse(r.getFechaHoraInicio(), formateador);
                if (fecha.isAfter(ahora) && !r.isPistaDisponible()) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this,
                                    "⚠ Tienes partidos próximos en pistas cerradas.\nRevisa la pestaña 'Próximos Partidos'.",
                                    "Aviso de Mantenimiento", JOptionPane.WARNING_MESSAGE));
                    break;
                }
            } catch (Exception e) {}
        }
    }
}