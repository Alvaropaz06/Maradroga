package ui;

import dao.MaterialDaoSQlite;
import dao.PistaDAO;
import dao.PistaDaoSQLite;
import entidades.Material;
import entidades.Pista;
import entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanelPistas extends JPanel {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG            = new Color(16, 22, 36);
    private static final Color CARD_BG       = new Color(20, 28, 46);
    private static final Color CARD_HOVER    = new Color(24, 34, 56);
    private static final Color ACCENT_CYAN   = new Color(0, 212, 255);
    private static final Color ACCENT_GREEN  = new Color(0, 230, 118);
    private static final Color ACCENT_RED    = new Color(255, 80, 70);
    private static final Color TEXT_PRIMARY  = new Color(240, 244, 255);
    private static final Color TEXT_MUTED    = new Color(120, 140, 170);
    private static final Color BORDER_COLOR  = new Color(28, 40, 62);
    private static final Color BTN_PRIMARY   = new Color(0, 180, 220);
    private static final Color FILTER_BG     = new Color(24, 34, 54);
    private static final Color FILTER_SEL    = new Color(0, 212, 255, 40);

    private Usuario usuarioLogueado;
    private PistaDAO pistaDAO;
    private JPanel panelCuadricula;

    public PanelPistas(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.pistaDAO = new PistaDaoSQLite();

        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(28, 28, 28, 28));

        // ── Cabecera ───────────────────────────────────────────────────────────
        JPanel panelNorte = new JPanel(new BorderLayout(0, 14));
        panelNorte.setOpaque(false);
        panelNorte.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel pnlTituloFila = new JPanel(new BorderLayout());
        pnlTituloFila.setOpaque(false);

        JLabel titulo = new JLabel("Pistas Disponibles");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Selecciona deporte y reserva tu horario");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXT_MUTED);

        JPanel pnlTituloTexto = new JPanel();
        pnlTituloTexto.setLayout(new BoxLayout(pnlTituloTexto, BoxLayout.Y_AXIS));
        pnlTituloTexto.setOpaque(false);
        pnlTituloTexto.add(titulo);
        pnlTituloTexto.add(Box.createVerticalStrut(3));
        pnlTituloTexto.add(lblSub);
        pnlTituloFila.add(pnlTituloTexto, BorderLayout.WEST);

        // Filtros de deporte
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFiltros.setOpaque(false);

        String[] deportes = {"Todas", "Pádel", "Tenis", "Fútbol", "Baloncesto"};
        JButton[] btnsFiltro = new JButton[deportes.length];
        for (int i = 0; i < deportes.length; i++) {
            final String dep = deportes[i];
            JButton btn = crearBotonFiltro(dep, i == 0);
            btnsFiltro[i] = btn;
            final int idx = i;
            btn.addActionListener(e -> {
                for (JButton b : btnsFiltro) setFiltroActivo(b, false);
                setFiltroActivo(btn, true);
                filtrarPistas(dep);
            });
            panelFiltros.add(btn);
        }

        panelNorte.add(pnlTituloFila, BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // ── Grid de pistas ─────────────────────────────────────────────────────
        panelCuadricula = new JPanel(new GridLayout(0, 2, 18, 18));
        panelCuadricula.setOpaque(false);
        panelCuadricula.setBorder(new EmptyBorder(4, 0, 0, 0));

        actualizarVistaPistas(pistaDAO.obtenerTodas());

        JScrollPane scroll = new JScrollPane(panelCuadricula);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setBackground(BG);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Helpers de filtro ─────────────────────────────────────────────────────

    private JButton crearBotonFiltro(String texto, boolean activo) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                // Borde
                g2.setColor(isSelected() ? ACCENT_CYAN : BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 20, 20));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 30));
        setFiltroActivo(btn, activo);
        return btn;
    }

    private void setFiltroActivo(JButton btn, boolean activo) {
        btn.setSelected(activo);
        btn.setBackground(activo ? new Color(0, 212, 255, 40) : FILTER_BG);
        btn.setForeground(activo ? ACCENT_CYAN : TEXT_MUTED);
        btn.repaint();
    }

    private void filtrarPistas(String deporte) {
        List<Pista> filtradas = deporte.equals("Todas") ?
                pistaDAO.obtenerTodas() : pistaDAO.obtenerPorDeporte(deporte);
        actualizarVistaPistas(filtradas);
    }

    private void actualizarVistaPistas(List<Pista> listaPistas) {
        panelCuadricula.removeAll();
        if (listaPistas != null && !listaPistas.isEmpty()) {
            for (Pista pista : listaPistas) {
                panelCuadricula.add(crearTarjetaPista(pista));
            }
        } else {
            JLabel vacio = new JLabel("No hay pistas disponibles para este deporte.");
            vacio.setForeground(TEXT_MUTED);
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panelCuadricula.add(vacio);
        }
        panelCuadricula.revalidate();
        panelCuadricula.repaint();
    }

    // ── Tarjeta de pista ──────────────────────────────────────────────────────

    private JPanel crearTarjetaPista(Pista pista) {
        JPanel tarjeta = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                // Borde
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(CARD_BG);
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Imagen de pista
        JLabel lblImagen = crearImagenPista(pista.getId(), pista.getDeporte(), 0, 140);
        lblImagen.setPreferredSize(new Dimension(0, 140));
        lblImagen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        lblImagen.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblImagen);
        tarjeta.add(Box.createVerticalStrut(14));

        // Cuerpo de texto
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(new EmptyBorder(0, 16, 0, 16));
        cuerpo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String nombreLimpio = pista.getId();
        if (nombreLimpio.contains(" ("))
            nombreLimpio = nombreLimpio.substring(0, nombreLimpio.lastIndexOf(" ("));

        JLabel lblNombre = new JLabel("Pista " + nombreLimpio + "  ·  " + pista.getDeporte());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombre.setForeground(TEXT_PRIMARY);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Precio badge
        JPanel filaPrecio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaPrecio.setOpaque(false);
        filaPrecio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel lblPrecio = new JLabel(String.format("%.0f€/h", pista.getPrecio())) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 212, 255, 25));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrecio.setForeground(ACCENT_CYAN);
        lblPrecio.setBorder(new EmptyBorder(3, 8, 3, 8));
        lblPrecio.setOpaque(false);

        boolean operativa = pista.isDisponible();
        JLabel lblEstado = new JLabel(operativa ? "  ●  Operativa" : "  ●  Mantenimiento");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(operativa ? ACCENT_GREEN : ACCENT_RED);

        filaPrecio.add(lblPrecio);
        filaPrecio.add(Box.createHorizontalStrut(10));
        filaPrecio.add(lblEstado);

        // Botón reservar
        JButton btnReservar = new JButton(operativa ? "Reservar ahora" : "No disponible") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btnReservar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnReservar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReservar.setBackground(operativa ? BTN_PRIMARY : new Color(35, 35, 50));
        btnReservar.setForeground(operativa ? new Color(10, 14, 23) : TEXT_MUTED);
        btnReservar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReservar.setFocusPainted(false);
        btnReservar.setBorderPainted(false);
        btnReservar.setContentAreaFilled(false);
        btnReservar.setEnabled(operativa);
        btnReservar.setCursor(operativa ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

        if (operativa) {
            btnReservar.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btnReservar.setBackground(ACCENT_CYAN); btnReservar.repaint(); }
                public void mouseExited(MouseEvent e)  { btnReservar.setBackground(BTN_PRIMARY); btnReservar.repaint(); }
            });
            btnReservar.addActionListener(e -> {
                if (usuarioLogueado == null || "Invitado".equalsIgnoreCase(usuarioLogueado.getNombre())) {
                    JOptionPane.showMessageDialog(this, "Inicia sesión para reservar.");
                } else {
                    abrirVentanaReserva(pista);
                }
            });
        }

        cuerpo.add(lblNombre);
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(filaPrecio);
        cuerpo.add(Box.createVerticalStrut(12));
        cuerpo.add(btnReservar);

        tarjeta.add(cuerpo);

        // Hover en tarjeta
        tarjeta.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { tarjeta.setBackground(CARD_HOVER); tarjeta.repaint(); }
            public void mouseExited(MouseEvent e)  { tarjeta.setBackground(CARD_BG); tarjeta.repaint(); }
        });

        return tarjeta;
    }

    private JLabel crearImagenPista(String idPista, String deporte, int width, int height) {
        JLabel lblFoto = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Fondo degradado si no hay imagen
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 50, 80),
                        getWidth(), getHeight(), new Color(10, 25, 45));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblFoto.setPreferredSize(new Dimension(width, height));
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            String depLimpio = deporte != null ? deporte.toLowerCase()
                    .replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u") : "default";
            int numeroFoto = 1;
            String nums = idPista.replaceAll("\\D+", "");
            if (!nums.isEmpty()) {
                numeroFoto = ((Integer.parseInt(nums) - 1) % 5) + 1;
            } else {
                numeroFoto = (Math.abs(idPista.hashCode()) % 5) + 1;
            }
            java.net.URL imgURL = getClass().getResource("/img/" + depLimpio + numeroFoto + ".jpg");
            if (imgURL != null) {
                ImageIcon icono = new ImageIcon(imgURL);
                Image img = icono.getImage().getScaledInstance(width > 0 ? width : 400, height, Image.SCALE_SMOOTH);
                lblFoto.setIcon(new ImageIcon(img));
            } else {
                lblFoto.setText(deporte);
                lblFoto.setForeground(new Color(120, 140, 170));
                lblFoto.setFont(new Font("Segoe UI", Font.BOLD, 18));
            }
        } catch (Exception e) {
            lblFoto.setText("Foto");
        }
        return lblFoto;
    }

    // ── Ventana de reserva (lógica intacta, UI mejorada) ──────────────────────

    private void abrirVentanaReserva(Pista pista) {
        final double[] precioMaterialesAcumulado = {0.0};
        final List<String> materialesSeleccionados = new ArrayList<>();

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirmar Reserva", true);
        dialogo.setSize(460, 640);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        // Cabecera
        JPanel pnlHead = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(14, 20, 34));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlHead.setBorder(new EmptyBorder(18, 22, 18, 22));
        JLabel tit = new JLabel("Reservar · " + pista.getDeporte());
        tit.setForeground(new Color(240, 244, 255));
        tit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel sub = new JLabel("Precio pista: " + pista.getPrecio() + "€/h");
        sub.setForeground(new Color(0, 212, 255));
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JPanel pnlHeadTexto = new JPanel();
        pnlHeadTexto.setLayout(new BoxLayout(pnlHeadTexto, BoxLayout.Y_AXIS));
        pnlHeadTexto.setOpaque(false);
        pnlHeadTexto.add(tit); pnlHeadTexto.add(Box.createVerticalStrut(3)); pnlHeadTexto.add(sub);
        pnlHead.add(pnlHeadTexto, BorderLayout.CENTER);
        dialogo.add(pnlHead, BorderLayout.NORTH);

        // Cuerpo
        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setBackground(new Color(16, 22, 36));
        pnlCentro.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel pnlFechaHora = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlFechaHora.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(28, 40, 62)),
                "Elige tu horario",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(120, 140, 170)
        ));
        pnlFechaHora.setBackground(new Color(20, 28, 46));
        pnlFechaHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lFecha = new JLabel("Fecha:"); lFecha.setForeground(new Color(120,140,170)); lFecha.setFont(new Font("Segoe UI",Font.PLAIN,12));
        JLabel lHora  = new JLabel("Hora:");  lHora.setForeground(new Color(120,140,170));  lHora.setFont(new Font("Segoe UI",Font.PLAIN,12));

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0); cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);       cal.set(java.util.Calendar.MILLISECOND, 0);
        SpinnerDateModel dateModel = new SpinnerDateModel(new java.util.Date(), cal.getTime(), null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinnerFecha = new JSpinner(dateModel);
        spinnerFecha.setEditor(new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy"));
        aplicarEstiloSpinner(spinnerFecha);

        JComboBox<String> comboHoras = new JComboBox<>();
        comboHoras.setBackground(new Color(24, 32, 50));
        comboHoras.setForeground(new Color(240, 244, 255));
        comboHoras.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        Runnable actualizarHoras = () -> {
            comboHoras.removeAllItems();
            String[] horas = {"09:00","11:00","13:00","17:00","19:00"};
            String fechaSelec = new SimpleDateFormat("dd/MM/yyyy").format(spinnerFecha.getValue());
            List<String> horasOcupadas = obtenerHorasOcupadas(pista.getId(), fechaSelec);
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (String h : horas) {
                if (!horasOcupadas.contains(h)) {
                    try {
                        if (LocalDateTime.parse(fechaSelec + " " + h, fmt).isAfter(ahora))
                            comboHoras.addItem(h);
                    } catch (Exception ex) {}
                }
            }
            if (comboHoras.getItemCount() == 0) comboHoras.addItem("Sin horarios disponibles");
        };
        spinnerFecha.addChangeListener(e -> actualizarHoras.run());
        actualizarHoras.run();

        pnlFechaHora.add(lFecha); pnlFechaHora.add(spinnerFecha);
        pnlFechaHora.add(lHora);  pnlFechaHora.add(comboHoras);

        pnlCentro.add(pnlFechaHora);
        pnlCentro.add(Box.createVerticalStrut(18));

        // Materiales
        MaterialDaoSQlite matDAO = new MaterialDaoSQlite();
        List<Material> materiales = matDAO.obtenerPorDeporte(pista.getDeporte());
        JPanel pnlMat = new JPanel();
        pnlMat.setLayout(new BoxLayout(pnlMat, BoxLayout.Y_AXIS));
        pnlMat.setBackground(new Color(20, 28, 46));
        pnlMat.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(28, 40, 62)),
                "Material opcional",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 11),
                new Color(120, 140, 170)
        ));
        pnlMat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        if (materiales.isEmpty()) {
            JLabel noMat = new JLabel("Sin materiales específicos para este deporte.");
            noMat.setForeground(new Color(120, 140, 170));
            noMat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            noMat.setBorder(new EmptyBorder(8, 10, 8, 10));
            pnlMat.add(noMat);
        } else {
            for (Material m : materiales) {
                JPanel fila = new JPanel(new BorderLayout(10, 0));
                fila.setOpaque(false);
                fila.setBorder(new EmptyBorder(6, 10, 6, 10));
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                JLabel info = new JLabel(m.getNombre() + "  +" + m.getPrecio() + "€  (Stock: " + m.getCantidad() + ")");
                info.setForeground(new Color(180, 200, 230));
                info.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                JButton btnAdd = new JButton("+ Añadir") {
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
                btnAdd.setPreferredSize(new Dimension(80, 28));
                btnAdd.setBackground(new Color(0, 100, 130));
                btnAdd.setForeground(new Color(0, 212, 255));
                btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btnAdd.setFocusPainted(false); btnAdd.setBorderPainted(false); btnAdd.setContentAreaFilled(false);
                btnAdd.setEnabled(m.getCantidad() > 0);
                btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btnAdd.addActionListener(ev -> {
                    if (matDAO.reservarMaterial(m.getId())) {
                        precioMaterialesAcumulado[0] += m.getPrecio();
                        materialesSeleccionados.add(m.getId());
                        info.setText(m.getNombre() + "  +" + m.getPrecio() + "€  (Stock: " + (m.getCantidad()-1) + ")");
                        JOptionPane.showMessageDialog(dialogo, "✓ Material añadido a tu reserva.");
                        btnAdd.setEnabled(false); btnAdd.setText("✓");
                    }
                });

                fila.add(info, BorderLayout.CENTER);
                fila.add(btnAdd, BorderLayout.EAST);
                pnlMat.add(fila);
            }
        }
        pnlCentro.add(pnlMat);
        dialogo.add(new JScrollPane(pnlCentro) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, BorderLayout.CENTER);

        // Botón confirmar
        JButton btnFin = new JButton("Confirmar y Pagar") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(10, 14, 23));
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                        (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btnFin.setBackground(new Color(0, 212, 255));
        btnFin.setForeground(new Color(10, 14, 23));
        btnFin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFin.setFocusPainted(false); btnFin.setBorderPainted(false); btnFin.setContentAreaFilled(false);
        btnFin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFin.setPreferredSize(new Dimension(0, 50));

        btnFin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnFin.setBackground(new Color(0, 230, 255)); btnFin.repaint(); }
            public void mouseExited(MouseEvent e)  { btnFin.setBackground(new Color(0, 212, 255)); btnFin.repaint(); }
        });

        btnFin.addActionListener(a -> {
            String horaSelec = (String) comboHoras.getSelectedItem();
            if (horaSelec == null || horaSelec.startsWith("Sin")) {
                JOptionPane.showMessageDialog(dialogo, "Selecciona un día y hora disponibles.");
                return;
            }
            double totalFinal = pista.getPrecio() + precioMaterialesAcumulado[0];
            String f = new SimpleDateFormat("dd/MM/yyyy").format(spinnerFecha.getValue()) + " " + horaSelec;
            String textoMateriales = String.join(",", materialesSeleccionados);

            if (guardarReservaEnBD_Completo(usuarioLogueado.getId(), pista.getId(), f, totalFinal, textoMateriales)) {
                JOptionPane.showMessageDialog(dialogo,
                        "¡Reserva confirmada!\nTotal a pagar en el club: " + totalFinal + "€");
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, "Hubo un error al guardar la reserva.");
            }
        });

        dialogo.add(btnFin, BorderLayout.SOUTH);
        dialogo.setBackground(new Color(16, 22, 36));
        dialogo.setVisible(true);
    }

    private void aplicarEstiloSpinner(JSpinner sp) {
        sp.setBackground(new Color(24, 32, 50));
        sp.setForeground(new Color(240, 244, 255));
        sp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(new Color(24, 32, 50));
            tf.setForeground(new Color(240, 244, 255));
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setBorder(new EmptyBorder(4, 8, 4, 4));
            tf.setCaretColor(new Color(0, 212, 255));
        }
    }

    // ── Lógica de BD (sin cambios) ─────────────────────────────────────────────

    private List<String> obtenerHorasOcupadas(String idPista, String fechaBusqueda) {
        List<String> ocupadas = new ArrayList<>();
        String sql = "SELECT fechaHoraInicio FROM Reservas WHERE idPista = ? AND fechaHoraInicio LIKE ?";
        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idPista);
            pstmt.setString(2, fechaBusqueda + "%");
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String full = rs.getString("fechaHoraInicio");
                    ocupadas.add(full.substring(full.length() - 5));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ocupadas;
    }

    private boolean guardarReservaEnBD_Completo(String idU, String idP, String fh, double precioCalculado, String materiales) {
        String sql = "INSERT INTO Reservas (id, idUsuario, idPista, fechaHoraInicio, precioTotal, estado, materiales) VALUES (?,?,?,?,?,?,?)";
        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, java.util.UUID.randomUUID().toString());
            pstmt.setString(2, idU); pstmt.setString(3, idP); pstmt.setString(4, fh);
            pstmt.setDouble(5, precioCalculado); pstmt.setString(6, "Confirmada");
            pstmt.setString(7, materiales);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}