package ui;

import dao.MaterialDAO;
import dao.MaterialDaoSQlite;
import entidades.Material;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PanelAdminMateriales extends JPanel {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG           = new Color(16, 22, 36);
    private static final Color CARD_BG      = new Color(20, 28, 46);
    private static final Color CARD_HOVER   = new Color(24, 34, 56);
    private static final Color ACCENT_CYAN  = new Color(0, 212, 255);
    private static final Color ACCENT_GREEN = new Color(0, 230, 118);
    private static final Color ACCENT_RED   = new Color(255, 80, 70);
    private static final Color TEXT_PRIMARY = new Color(240, 244, 255);
    private static final Color TEXT_MUTED   = new Color(120, 140, 170);
    private static final Color BORDER_COLOR = new Color(28, 40, 62);
    private static final Color INPUT_BG     = new Color(24, 32, 50);

    private MaterialDAO materialDAO;
    private JPanel panelLista;

    public PanelAdminMateriales() {
        this.materialDAO = new MaterialDaoSQlite();
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

        JLabel titulo = new JLabel("Gestión de Materiales");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Control de stock y equipamiento deportivo");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);

        pnlTitTxt.add(titulo);
        pnlTitTxt.add(Box.createVerticalStrut(3));
        pnlTitTxt.add(lblSub);

        JButton btnAnadir = crearBotonAccion("+ Nuevo Material", ACCENT_GREEN, new Color(10, 14, 23));
        btnAnadir.addActionListener(e -> abrirDialogoNuevoMaterial());

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

        cargarMateriales();
    }

    private void cargarMateriales() {
        panelLista.removeAll();
        List<Material> lista = materialDAO.obtenerTodos();

        if (lista.isEmpty()) {
            JLabel empty = new JLabel("No hay materiales en la base de datos.");
            empty.setForeground(TEXT_MUTED);
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panelLista.add(empty);
        } else {
            for (Material m : lista) {
                panelLista.add(crearFilaMaterial(m));
                panelLista.add(Box.createVerticalStrut(10));
            }
        }
        panelLista.revalidate();
        panelLista.repaint();
    }

    private JPanel crearFilaMaterial(Material m) {
        boolean hayStock = m.getCantidad() > 0;

        JPanel fila = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // Línea lateral de stock
                g2.setColor(hayStock ? new Color(0, 230, 118, 80) : new Color(255, 80, 70, 80));
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
        String emoji = switch (m.getDeporte() != null ? m.getDeporte().toLowerCase() : "") {
            case "pádel", "padel" -> "🏓";
            case "tenis"          -> "🎾";
            case "fútbol","futbol"-> "⚽";
            case "baloncesto"     -> "🏀";
            default               -> "📦";
        };
        JLabel lblEmoji = new JLabel(emoji);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblEmoji.setBorder(new EmptyBorder(0, 8, 0, 14));

        // Info
        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);

        JLabel lblNombre = new JLabel(m.getNombre() + "  ·  " + (m.getDeporte() != null ? m.getDeporte() : "—"));
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(TEXT_PRIMARY);

        JLabel lblDetalle = new JLabel(String.format("%.2f€  ·  Stock: %d unidades", m.getPrecio(), m.getCantidad()));
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalle.setForeground(TEXT_MUTED);

        pnlInfo.add(lblNombre);
        pnlInfo.add(Box.createVerticalStrut(3));
        pnlInfo.add(lblDetalle);

        // Badge de stock
        JLabel lblBadge = new JLabel(hayStock ? "● En stock" : "● Agotado") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hayStock ? new Color(0, 230, 118, 25) : new Color(255, 80, 70, 25));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBadge.setForeground(hayStock ? ACCENT_GREEN : ACCENT_RED);
        lblBadge.setBorder(new EmptyBorder(4, 10, 4, 10));
        lblBadge.setOpaque(false);

        // Botones
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotones.setOpaque(false);

        JButton btnEliminar = crearBotonAccion("Eliminar", ACCENT_RED, TEXT_PRIMARY);
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar '" + m.getNombre() + "' de la base de datos?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                materialDAO.eliminarMaterial(m.getId());
                cargarMateriales();
            }
        });

        pnlBotones.add(lblBadge);
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

    private void abrirDialogoNuevoMaterial() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Material", true);
        dialogo.setSize(380, 420);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel pnlHead = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 16));
        pnlHead.setBackground(new Color(14, 20, 34));
        JLabel headLbl = new JLabel("Añadir Nuevo Material");
        headLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headLbl.setForeground(TEXT_PRIMARY);
        pnlHead.add(headLbl);
        dialogo.add(pnlHead, BorderLayout.NORTH);

        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setBackground(new Color(16, 22, 36));
        pnlForm.setBorder(new EmptyBorder(20, 24, 20, 24));

        JTextField txtNombre   = crearInput();
        JTextField txtDeporte  = crearInput();
        JTextField txtPrecio   = crearInput();
        JTextField txtCantidad = crearInput();

        agregarFilaForm(pnlForm, "Nombre del material", txtNombre);
        pnlForm.add(Box.createVerticalStrut(12));
        agregarFilaForm(pnlForm, "Deporte", txtDeporte);
        pnlForm.add(Box.createVerticalStrut(12));
        agregarFilaForm(pnlForm, "Precio (€)", txtPrecio);
        pnlForm.add(Box.createVerticalStrut(12));
        agregarFilaForm(pnlForm, "Cantidad inicial", txtCantidad);

        dialogo.add(pnlForm, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar Material") {{
            setBackground(ACCENT_GREEN);
            setForeground(new Color(10, 14, 23));
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false); setBorderPainted(false);
            setPreferredSize(new Dimension(0, 46));
        }};

        btnGuardar.addActionListener(e -> {
            try {
                Material mat = new Material();
                // 1. Añadimos el ID automático necesario para la Base de Datos
                mat.setId(java.util.UUID.randomUUID().toString());

                mat.setNombre(txtNombre.getText().trim());
                mat.setDeporte(txtDeporte.getText().trim());

                // 2. Protegemos el precio contra comas accidentales
                String precioStr = txtPrecio.getText().trim().replace(",", ".");
                mat.setPrecio(Double.parseDouble(precioStr));

                mat.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
                mat.setStockDisponible(mat.getCantidad() > 0);

                if (materialDAO.anadirMaterial(mat)) {
                    JOptionPane.showMessageDialog(dialogo, "✓ Material añadido con éxito.");
                    dialogo.dispose();
                    cargarMateriales();
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Error al añadir material en la base de datos.");
                }
            } catch (NumberFormatException ex) {
                // Ahora si fallan los números te avisa de esto concretamente
                JOptionPane.showMessageDialog(dialogo, "El precio y la cantidad deben ser números válidos.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error general: Revisa los datos.");
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
        btn.setPreferredSize(new Dimension(130, 34));
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