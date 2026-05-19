package ui;

import dao.PistaDAO;
import dao.PistaDaoSQLite;
import entidades.Pista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PanelAdminPistas extends JPanel {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG           = new Color(16, 22, 36);
    private static final Color CARD_BG      = new Color(20, 28, 46);
    private static final Color CARD_HOVER   = new Color(24, 34, 56);
    private static final Color ACCENT_CYAN  = new Color(0, 212, 255);
    private static final Color ACCENT_GREEN = new Color(0, 230, 118);
    private static final Color ACCENT_RED   = new Color(255, 80, 70);
    private static final Color ACCENT_ORG   = new Color(255, 165, 60);
    private static final Color TEXT_PRIMARY = new Color(240, 244, 255);
    private static final Color TEXT_MUTED   = new Color(120, 140, 170);
    private static final Color BORDER_COLOR = new Color(28, 40, 62);
    private static final Color INPUT_BG     = new Color(24, 32, 50);

    private PistaDAO pistaDAO;
    private JPanel panelLista;

    public PanelAdminPistas() {
        this.pistaDAO = new PistaDaoSQLite();
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // ── Cabecera ───────────────────────────────────────────────────────────
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);
        panelNorte.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel pnlTitTxt = new JPanel();
        pnlTitTxt.setLayout(new BoxLayout(pnlTitTxt, BoxLayout.Y_AXIS));
        pnlTitTxt.setOpaque(false);

        JLabel titulo = new JLabel("Gestión de Pistas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Añade, bloquea o elimina pistas del centro");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);

        pnlTitTxt.add(titulo);
        pnlTitTxt.add(Box.createVerticalStrut(3));
        pnlTitTxt.add(lblSub);

        JButton btnAnadir = crearBotonAccion("+ Nueva Pista", ACCENT_GREEN, new Color(10, 14, 23));
        btnAnadir.addActionListener(e -> abrirDialogoNuevaPista());

        panelNorte.add(pnlTitTxt, BorderLayout.WEST);
        panelNorte.add(btnAnadir, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        // ── Lista ──────────────────────────────────────────────────────────────
        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(panelLista);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        cargarPistas();
    }

    private void cargarPistas() {
        panelLista.removeAll();
        List<Pista> lista = pistaDAO.obtenerTodas();

        if (lista.isEmpty()) {
            JLabel empty = new JLabel("No hay pistas registradas.");
            empty.setForeground(TEXT_MUTED);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panelLista.add(empty);
        } else {
            for (Pista p : lista) {
                panelLista.add(crearFilaPista(p));
                panelLista.add(Box.createVerticalStrut(10));
            }
        }
        panelLista.revalidate();
        panelLista.repaint();
    }

    private JPanel crearFilaPista(Pista p) {
        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // Línea lateral de estado
                g2.setColor(p.isDisponible() ? new Color(0, 230, 118, 80) : new Color(255, 80, 70, 80));
                g2.fill(new RoundRectangle2D.Float(0, 8, 4, getHeight()-16, 4, 4));
                g2.dispose();
            }
        };
        fila.setLayout(new BorderLayout(0, 0));
        fila.setBackground(CARD_BG);
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(14, 20, 14, 16));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icono deporte
        String emoji = switch (p.getDeporte().toLowerCase()) {
            case "pádel", "padel" -> "🏓";
            case "tenis"          -> "🎾";
            case "fútbol", "futbol" -> "⚽";
            case "baloncesto"     -> "🏀";
            default               -> "🏟";
        };
        JLabel lblEmoji = new JLabel(emoji);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblEmoji.setBorder(new EmptyBorder(0, 8, 0, 14));

        // Info
        String nombreLimpio = p.getId();
        if (nombreLimpio.contains(" (")) nombreLimpio = nombreLimpio.substring(0, nombreLimpio.lastIndexOf(" ("));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);

        JLabel lblNombre = new JLabel("Pista " + nombreLimpio + "  ·  " + p.getDeporte());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(TEXT_PRIMARY);

        JLabel lblPrecio = new JLabel(String.format("%.2f€/h", p.getPrecio()));
        lblPrecio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrecio.setForeground(TEXT_MUTED);

        pnlInfo.add(lblNombre);
        pnlInfo.add(Box.createVerticalStrut(3));
        pnlInfo.add(lblPrecio);

        // Estado badge
        JLabel lblEstado = new JLabel(p.isDisponible() ? "● Operativa" : "● Mantenimiento") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = p.isDisponible() ? new Color(0, 230, 118, 25) : new Color(255, 80, 70, 25);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstado.setForeground(p.isDisponible() ? ACCENT_GREEN : ACCENT_RED);
        lblEstado.setBorder(new EmptyBorder(4, 10, 4, 10));
        lblEstado.setOpaque(false);

        // Botones
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotones.setOpaque(false);

        JButton btnEstado = crearBotonAccion(
                p.isDisponible() ? "Bloquear" : "Habilitar",
                p.isDisponible() ? ACCENT_ORG : ACCENT_GREEN,
                new Color(10, 14, 23)
        );
        btnEstado.addActionListener(e -> {
            pistaDAO.actualizarDisponibilidad(p.getId(), !p.isDisponible());
            cargarPistas();
        });

        JButton btnEliminar = crearBotonAccion("Eliminar", ACCENT_RED, TEXT_PRIMARY);
        String finalNombreLimpio = nombreLimpio;
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Borrar la pista '" + finalNombreLimpio + "' definitivamente?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                pistaDAO.eliminarPista(p.getId());
                cargarPistas();
            }
        });

        pnlBotones.add(lblEstado);
        pnlBotones.add(btnEstado);
        pnlBotones.add(btnEliminar);

        JPanel pnlIzq = new JPanel(new BorderLayout());
        pnlIzq.setOpaque(false);
        pnlIzq.add(lblEmoji, BorderLayout.WEST);
        pnlIzq.add(pnlInfo, BorderLayout.CENTER);

        fila.add(pnlIzq, BorderLayout.CENTER);
        fila.add(pnlBotones, BorderLayout.EAST);

        fila.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { fila.setBackground(CARD_HOVER); fila.repaint(); }
            public void mouseExited(MouseEvent e)  { fila.setBackground(CARD_BG); fila.repaint(); }
        });

        return fila;
    }

    private void abrirDialogoNuevaPista() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Pista", true);
        dialogo.setSize(380, 350); // <--- Aumentado a 350 de altura
        dialogo.setLocationRelativeTo(this);

        JPanel pnlHead = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 16)) {{
            setBackground(new Color(14, 20, 34));
        }};
        JLabel headLbl = new JLabel("Añadir Nueva Pista");
        headLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headLbl.setForeground(TEXT_PRIMARY);
        pnlHead.add(headLbl);
        dialogo.add(pnlHead, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setBackground(new Color(16, 22, 36));
        pnlForm.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField txtNombre = crearInput();
        JTextField txtPrecio = crearInput(); txtPrecio.setText("15.0");
        String[] deportes = {"Pádel", "Tenis", "Fútbol", "Baloncesto"};
        JComboBox<String> comboDeportes = new JComboBox<>(deportes);
        estilizarCombo(comboDeportes);

        agregarFilaForm(pnlForm, "Nombre (ej: Central)", txtNombre);
        pnlForm.add(Box.createVerticalStrut(12));
        agregarFilaForm(pnlForm, "Deporte", comboDeportes);
        pnlForm.add(Box.createVerticalStrut(12));
        agregarFilaForm(pnlForm, "Precio por hora (€)", txtPrecio);

        dialogo.add(pnlForm, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar Pista") {{
            setBackground(ACCENT_GREEN);
            setForeground(new Color(10, 14, 23));
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false); setBorderPainted(false);
            setPreferredSize(new Dimension(0, 46));
        }};
        btnGuardar.addActionListener(e -> {
            try {
                double precio = Double.parseDouble(txtPrecio.getText().replace(",", "."));
                Pista p = new Pista();
                p.setId(txtNombre.getText().trim() + " (" + comboDeportes.getSelectedItem() + ")");
                p.setDeporte((String) comboDeportes.getSelectedItem());
                p.setDisponible(true);
                p.setPrecio(precio);
                if (pistaDAO.anadirPista(p)) { cargarPistas(); dialogo.dispose(); }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Precio no válido.");
            }
        });
        dialogo.add(btnGuardar, BorderLayout.SOUTH);
        dialogo.setBackground(BG);
        dialogo.setVisible(true);
    }

    // ── Helpers de UI ─────────────────────────────────────────────────────────

    private JButton crearBotonAccion(String texto, Color bgColor, Color fgColor) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(96, 32));
        Color hover = bgColor.darker();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); btn.repaint(); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bgColor); btn.repaint(); }
        });
        return btn;
    }

    private JTextField crearInput() {
        JTextField tf = new JTextField();
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT_CYAN);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return tf;
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setBackground(INPUT_BG);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private void agregarFilaForm(JPanel panel, String labelText, JComponent campo) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(campo);
    }
}