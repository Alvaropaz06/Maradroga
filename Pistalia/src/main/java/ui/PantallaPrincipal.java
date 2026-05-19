package ui;

import dao.MaterialDaoSQlite;
import entidades.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PantallaPrincipal extends JFrame {

    // ── Paleta Estética Oscura ────────────────────────────────────────────────
    private static final Color BG_DARK        = new Color(10, 14, 23);
    private static final Color SIDEBAR_BG     = new Color(14, 20, 34);
    private static final Color CONTENT_BG     = new Color(16, 22, 36);
    private static final Color ACCENT_CYAN    = new Color(0, 212, 255);
    private static final Color TEXT_PRIMARY   = new Color(240, 244, 255);
    private static final Color TEXT_MUTED     = new Color(120, 140, 170);
    private static final Color BORDER_SUBTLE  = new Color(25, 38, 58);
    private static final Color ITEM_HOVER     = new Color(22, 32, 52);
    private static final Color ITEM_SELECTED  = new Color(0, 212, 255, 30);

    private Usuario usuarioLogueado;
    private CardLayout cardLayout;
    private JPanel panelContenedor;

    public PantallaPrincipal(Usuario usuario) {
        this.usuarioLogueado = usuario;

        setTitle("Pistalia — " + usuario.getNombre());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // ── Layout raíz ───────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_DARK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);

        // ── Sidebar ───────────────────────────────────────────────────────────
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Línea derecha
                g2.setColor(BORDER_SUBTLE);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setOpaque(false);
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // ── Logo Original en Sidebar ──────────────────────────────────────────
        JPanel pnlBrand = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        pnlBrand.setOpaque(false);
        pnlBrand.setMaximumSize(new Dimension(220, 120));

        JLabel lblLogoSide = new JLabel();
        lblLogoSide.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL imgURL = getClass().getResource("/img/logo.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                int origW = img.getWidth(null);
                int origH = img.getHeight(null);
                int targetW = 140;
                int targetH = (origH * targetW) / origW;
                Image scaled = img.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                lblLogoSide.setIcon(new ImageIcon(scaled));
            } else {
                lblLogoSide.setText("PISTALIA");
                lblLogoSide.setFont(new Font("Segoe UI", Font.BOLD, 22));
                lblLogoSide.setForeground(TEXT_PRIMARY);
            }
        } catch (Exception ex) {
            lblLogoSide.setText("PISTALIA");
            lblLogoSide.setFont(new Font("Segoe UI", Font.BOLD, 22));
            lblLogoSide.setForeground(TEXT_PRIMARY);
        }
        pnlBrand.add(lblLogoSide);

        sidebar.add(pnlBrand);
        sidebar.add(crearLinea());
        sidebar.add(Box.createVerticalStrut(15));

        // ── Preparar Contenedor Central y Paneles ─────────────────────────────
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(CONTENT_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelContenedor.setOpaque(false);

        // AÑADIMOS SOLO LOS PANELES NECESARIOS SEGÚN EL ROL
        if (usuarioLogueado.isEsAdmin()) {
            panelContenedor.add(new PanelAdminPistas(), "ADMIN_PISTAS");
            panelContenedor.add(new PanelAdminMateriales(), "ADMIN_MATERIAL");
            panelContenedor.add(new PanelBuzonAdmin(), "ADMIN_BUZON"); // NUEVO
        } else {
            panelContenedor.add(crearPanelBienvenida(), "INICIO");
            panelContenedor.add(new PanelPistas(usuarioLogueado), "PISTAS");
            panelContenedor.add(new PanelMisReservas(usuarioLogueado), "MIS_RESERVAS");
            panelContenedor.add(new PanelBuzonUsuario(usuarioLogueado), "BUZON"); // NUEVO
        }

        // ── Menú de Navegación ────────────────────────────────────────────────
        String[][] navData;
        // MENÚ EXCLUSIVO PARA ADMINISTRADORES
        if (usuarioLogueado.isEsAdmin()) {
            navData = new String[][]{
                    {"⚙", "Gestión Pistas", "ADMIN_PISTAS"},
                    {"📦", "Gestión Material", "ADMIN_MATERIAL"},
                    {"✉", "Buzón de Usuarios", "ADMIN_BUZON"} // NUEVO
            };
        }
        // MENÚ EXCLUSIVO PARA USUARIOS E INVITADOS
        else {
            navData = new String[][]{
                    {"🏠", "Inicio", "INICIO"},
                    {"🏟", "Reservar Pista", "PISTAS"},
                    {"📋", "Mis Reservas", "MIS_RESERVAS"},
                    {"✉", "Buzón y Ayuda", "BUZON"} // NUEVO
            };
        }

        JPanel[] navPanels = new JPanel[navData.length];

        for (int i = 0; i < navData.length; i++) {
            final int index = i;
            String iconTxt = navData[i][0];
            String titleTxt = navData[i][1];
            String cardRef = navData[i][2];

            JPanel navItem = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    boolean sel = Boolean.TRUE.equals(getClientProperty("seleccionado"));
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (sel) {
                        g2.setColor(ITEM_SELECTED);
                        g2.fill(new RoundRectangle2D.Float(8, 2, getWidth() - 16, getHeight() - 4, 8, 8));
                        g2.setColor(ACCENT_CYAN);
                        g2.fill(new RoundRectangle2D.Float(4, 8, 3, getHeight() - 16, 3, 3));
                    } else if (getBackground() != null && getBackground().getAlpha() > 0) {
                        g2.setColor(getBackground());
                        g2.fill(new RoundRectangle2D.Float(8, 2, getWidth() - 16, getHeight() - 4, 8, 8));
                    }
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            navItem.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
            navItem.setOpaque(false);
            navItem.setMaximumSize(new Dimension(220, 42));
            navItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navItem.putClientProperty("seleccionado", false);

            JLabel ico = new JLabel(iconTxt);
            ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            ico.setForeground(TEXT_MUTED);

            JLabel name = new JLabel(titleTxt);
            name.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            name.setForeground(TEXT_MUTED);

            navItem.add(ico);
            navItem.add(name);

            navItem.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!Boolean.TRUE.equals(navItem.getClientProperty("seleccionado"))) {
                        navItem.setBackground(ITEM_HOVER);
                        navItem.repaint();
                    }
                }
                public void mouseExited(MouseEvent e) {
                    if (!Boolean.TRUE.equals(navItem.getClientProperty("seleccionado"))) {
                        navItem.setBackground(new Color(0,0,0,0));
                        navItem.repaint();
                    }
                }
                public void mouseClicked(MouseEvent e) {
                    // Seleccionar visualmente
                    for (int j = 0; j < navPanels.length; j++) {
                        boolean isMe = (j == index);
                        navPanels[j].putClientProperty("seleccionado", isMe);
                        navPanels[j].setBackground(new Color(0,0,0,0));
                        ((JLabel)navPanels[j].getComponent(0)).setForeground(isMe ? ACCENT_CYAN : TEXT_MUTED);
                        ((JLabel)navPanels[j].getComponent(1)).setForeground(isMe ? ACCENT_CYAN : TEXT_MUTED);
                        navPanels[j].repaint();
                    }

                    // Lógica de recarga de paneles
                    if (cardRef.equals("INICIO")) {
                        panelContenedor.add(crearPanelBienvenida(), "INICIO_RECARGADO");
                        cardLayout.show(panelContenedor, "INICIO_RECARGADO");
                    } else if (cardRef.equals("MIS_RESERVAS")) {
                        panelContenedor.add(new PanelMisReservas(usuarioLogueado), "MIS_RESERVAS");
                        cardLayout.show(panelContenedor, "MIS_RESERVAS");
                    } else {
                        cardLayout.show(panelContenedor, cardRef);
                    }
                }
            });

            navPanels[i] = navItem;
            sidebar.add(navItem);
            sidebar.add(Box.createVerticalStrut(4));
        }

        // ── Info de usuario y Botón Salir ─────────────────────────────────────
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearLinea());

        JPanel pnlUser = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 14));
        pnlUser.setOpaque(false);
        pnlUser.setMaximumSize(new Dimension(220, 60));

        JLabel lblAvatar = new JLabel(String.valueOf(usuario.getNombre().charAt(0)).toUpperCase()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_CYAN);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(BG_DARK);
                FontMetrics fm = g2.getFontMetrics(getFont());
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setFont(getFont());
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAvatar.setPreferredSize(new Dimension(34, 34));
        lblAvatar.setOpaque(false);

        JPanel pnlUserInfo = new JPanel();
        pnlUserInfo.setLayout(new BoxLayout(pnlUserInfo, BoxLayout.Y_AXIS));
        pnlUserInfo.setOpaque(false);
        JLabel lblUserName = new JLabel(usuario.getNombre());
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserName.setForeground(TEXT_PRIMARY);
        JLabel lblUserRole = new JLabel(usuario.isEsAdmin() ? "Administrador" : "Usuario");
        lblUserRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUserRole.setForeground(usuario.isEsAdmin() ? ACCENT_CYAN : TEXT_MUTED);
        pnlUserInfo.add(lblUserName);
        pnlUserInfo.add(lblUserRole);

        pnlUser.add(lblAvatar);
        pnlUser.add(pnlUserInfo);

        JButton btnCerrar = new JButton("↩ Cerrar Sesión") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics(getFont());
                g2.setFont(getFont());
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnCerrar.setMaximumSize(new Dimension(190, 34));
        btnCerrar.setPreferredSize(new Dimension(190, 34));
        btnCerrar.setBackground(new Color(40, 20, 20));
        btnCerrar.setForeground(new Color(255, 90, 80));
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCerrar.addActionListener(e -> { new PantallaLogin().setVisible(true); dispose(); });
        btnCerrar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnCerrar.setBackground(new Color(70, 20, 20)); btnCerrar.repaint(); }
            public void mouseExited(MouseEvent e)  { btnCerrar.setBackground(new Color(40, 20, 20));  btnCerrar.repaint(); }
        });

        sidebar.add(pnlUser);
        JPanel pnlCerrarWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        pnlCerrarWrap.setOpaque(false);
        pnlCerrarWrap.add(btnCerrar);
        sidebar.add(pnlCerrarWrap);
        sidebar.add(Box.createVerticalStrut(10));

        root.add(sidebar, BorderLayout.WEST);
        root.add(panelContenedor, BorderLayout.CENTER);
        setContentPane(root);

        // Activar el primer item por defecto (Para User será "Inicio", para Admin será "Gestión Pistas")
        navPanels[0].dispatchEvent(new MouseEvent(navPanels[0], MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false));

        // ── Notificación Mantenimiento Original (Solo para Usuarios Registrados) ──
        if (usuarioLogueado != null && !usuarioLogueado.isEsAdmin() && !"Invitado".equals(usuarioLogueado.getNombre())) {
            dao.ReservaDaoSQLite daoRes = new dao.ReservaDaoSQLite();
            List<entidades.Reserva> misReservas = daoRes.obtenerPorUsuario(usuarioLogueado.getId());
            boolean hayAviso = false;
            for (entidades.Reserva r : misReservas) {
                if (!r.isPistaDisponible()) {
                    hayAviso = true;
                    break;
                }
            }
            if (hayAviso) {
                JOptionPane.showMessageDialog(this,
                        "¡Atención! Tienes reservas en pistas que han entrado en mantenimiento.\nPor favor, revisa 'Mis Reservas'.",
                        "Aviso de Mantenimiento",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ── Panel de Bienvenida Adaptado a Tema Oscuro ────────────────────────────
    private JPanel crearPanelBienvenida() {
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(CONTENT_BG);
        panelCentral.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel saludo = new JLabel("¡Bienvenido, " + usuarioLogueado.getNombre() + "!");
        saludo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        saludo.setForeground(TEXT_PRIMARY);
        panelCentral.add(saludo, BorderLayout.NORTH);

        JPanel pnlRecomendaciones = new JPanel();
        pnlRecomendaciones.setLayout(new BoxLayout(pnlRecomendaciones, BoxLayout.Y_AXIS));
        pnlRecomendaciones.setBackground(CONTENT_BG);
        pnlRecomendaciones.setBorder(new EmptyBorder(30, 0, 0, 0));

        String deporteFav = usuarioLogueado.getPreferencias();

        if (deporteFav == null || deporteFav.trim().isEmpty() || "Invitado".equalsIgnoreCase(usuarioLogueado.getNombre())) {
            JLabel lblAviso = new JLabel("Reserva pistas habitualmente y te mostraremos tus favoritas aquí.");
            lblAviso.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblAviso.setForeground(TEXT_MUTED);
            pnlRecomendaciones.add(lblAviso);
        } else {
            JLabel lblTituloRec = new JLabel("Recomendado para ti: Pistas de " + deporteFav + " disponibles");
            lblTituloRec.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTituloRec.setForeground(TEXT_PRIMARY);
            pnlRecomendaciones.add(lblTituloRec);
            pnlRecomendaciones.add(Box.createVerticalStrut(20));

            JPanel cuadriculaRec = new JPanel(new GridLayout(0, 2, 20, 20));
            cuadriculaRec.setBackground(CONTENT_BG);

            dao.PistaDaoSQLite pistaDAO = new dao.PistaDaoSQLite();
            List<entidades.Pista> pistasRecomendadas = pistaDAO.obtenerPorDeporte(deporteFav);

            if (pistasRecomendadas.isEmpty()) {
                JLabel noData = new JLabel("Vaya, ahora mismo no hay pistas de " + deporteFav + " disponibles.");
                noData.setForeground(TEXT_MUTED);
                cuadriculaRec.add(noData);
            } else {
                for (entidades.Pista pista : pistasRecomendadas) {
                    JPanel tarjeta = new JPanel();
                    tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
                    tarjeta.setBackground(SIDEBAR_BG);
                    tarjeta.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_SUBTLE, 1),
                            BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));

                    JLabel lblImagen = crearImagenPista(pista.getId(), pista.getDeporte(), 280, 110);
                    lblImagen.setAlignmentX(Component.LEFT_ALIGNMENT);
                    tarjeta.add(lblImagen);
                    tarjeta.add(Box.createVerticalStrut(15));

                    String nombreLimpio = pista.getId();
                    if (nombreLimpio.contains(" (")) {
                        nombreLimpio = nombreLimpio.substring(0, nombreLimpio.lastIndexOf(" ("));
                    }

                    JLabel info = new JLabel("Pista " + nombreLimpio + " - " + pista.getDeporte() + " (" + pista.getPrecio() + "€/h)");
                    info.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    info.setForeground(TEXT_PRIMARY);
                    info.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JButton btnReservarRec = new JButton("Reservar Ahora");
                    btnReservarRec.setBackground(ACCENT_CYAN);
                    btnReservarRec.setForeground(BG_DARK);
                    btnReservarRec.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    btnReservarRec.setFocusPainted(false);
                    btnReservarRec.setBorderPainted(false);
                    btnReservarRec.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btnReservarRec.setAlignmentX(Component.LEFT_ALIGNMENT);

                    btnReservarRec.addActionListener(e -> abrirVentanaReservaInicio(pista));

                    tarjeta.add(info);
                    tarjeta.add(Box.createVerticalStrut(12));
                    tarjeta.add(btnReservarRec);

                    cuadriculaRec.add(tarjeta);
                }
            }
            pnlRecomendaciones.add(cuadriculaRec);
        }

        JScrollPane scroll = new JScrollPane(pnlRecomendaciones);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CONTENT_BG);
        panelCentral.add(scroll, BorderLayout.CENTER);
        return panelCentral;
    }

    // ── Lógica Original ───────────────────────────────────────────────────────

    private JLabel crearImagenPista(String idPista, String deporte, int width, int height) {
        JLabel lblFoto = new JLabel();
        lblFoto.setPreferredSize(new Dimension(width, height));
        lblFoto.setMaximumSize(new Dimension(width, height));
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            String depLimpio = deporte != null ? deporte.toLowerCase()
                    .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u") : "default";

            int numeroFoto = 1;
            String numerosEnId = idPista.replaceAll("\\D+", "");
            if (!numerosEnId.isEmpty()) {
                numeroFoto = Integer.parseInt(numerosEnId);
                numeroFoto = ((numeroFoto - 1) % 5) + 1;
            } else {
                numeroFoto = (Math.abs(idPista.hashCode()) % 5) + 1;
            }

            URL imgURL = getClass().getResource("/img/" + depLimpio + numeroFoto + ".jpg");

            if (imgURL != null) {
                ImageIcon icono = new ImageIcon(imgURL);
                Image img = icono.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                lblFoto.setIcon(new ImageIcon(img));
            } else {
                lblFoto.setText("Foto " + deporte + " " + numeroFoto);
                lblFoto.setOpaque(true);
                lblFoto.setBackground(BORDER_SUBTLE);
                lblFoto.setForeground(TEXT_MUTED);
            }
        } catch (Exception e) {
            lblFoto.setText("Foto");
        }
        return lblFoto;
    }

    private JSeparator crearLinea() {
        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BORDER_SUBTLE);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private void abrirVentanaReservaInicio(entidades.Pista pista) {
        final double[] precioMaterialesAcumulado = {0.0};
        final java.util.List<String> materialesSeleccionados = new java.util.ArrayList<>();

        JDialog dialogo = new JDialog(this, "Confirmar Reserva Rápida", true);
        dialogo.setSize(450, 650);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        Color bgDark = new Color(16, 22, 36);
        Color inputBg = new Color(24, 32, 50);
        Color textPrimary = new Color(240, 244, 255);
        Color accentCyan = new Color(0, 212, 255);

        JPanel pnlHead = new JPanel();
        pnlHead.setBackground(new Color(14, 20, 34));
        JLabel tit = new JLabel("Reserva: " + pista.getDeporte());
        tit.setForeground(textPrimary);
        tit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHead.add(tit);
        dialogo.add(pnlHead, BorderLayout.NORTH);

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setBackground(bgDark);
        pnlCentro.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlFechaHora = new JPanel(new GridLayout(2, 2, 10, 15));
        pnlFechaHora.setBackground(bgDark);

        javax.swing.border.TitledBorder borderFecha = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(28, 40, 62)), "Elige tu horario"
        );
        borderFecha.setTitleColor(textPrimary);
        borderFecha.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlFechaHora.setBorder(borderFecha);
        pnlFechaHora.setMaximumSize(new Dimension(400, 100));

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setForeground(textPrimary);
        pnlFechaHora.add(lblFecha);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0); cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0); cal.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Date hoyLimite = cal.getTime();

        SpinnerDateModel dateModel = new SpinnerDateModel(new java.util.Date(), hoyLimite, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinnerFecha = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");

        dateEditor.getTextField().setBackground(inputBg);
        dateEditor.getTextField().setForeground(textPrimary);
        dateEditor.getTextField().setCaretColor(textPrimary);
        dateEditor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinnerFecha.setEditor(dateEditor);
        spinnerFecha.setBackground(inputBg);
        pnlFechaHora.add(spinnerFecha);

        JLabel lblHora = new JLabel("Hora:");
        lblHora.setForeground(textPrimary);
        pnlFechaHora.add(lblHora);

        JComboBox<String> comboHoras = new JComboBox<>();
        comboHoras.setBackground(inputBg);
        comboHoras.setForeground(textPrimary);
        comboHoras.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlFechaHora.add(comboHoras);

        Runnable actualizarHoras = () -> {
            comboHoras.removeAllItems();
            String[] horas = {"09:00", "11:00", "13:00", "17:00", "19:00"};
            String fechaSelec = new SimpleDateFormat("dd/MM/yyyy").format(spinnerFecha.getValue());
            List<String> horasOcupadas = obtenerHorasOcupadas(pista.getId(), fechaSelec);

            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (String h : horas) {
                if (!horasOcupadas.contains(h)) {
                    try {
                        LocalDateTime opcion = LocalDateTime.parse(fechaSelec + " " + h, fmt);
                        if (opcion.isAfter(ahora)) {
                            comboHoras.addItem(h);
                        }
                    } catch (Exception ex) {}
                }
            }
            if (comboHoras.getItemCount() == 0) comboHoras.addItem("Horarios no disponibles");
        };

        spinnerFecha.addChangeListener(e -> actualizarHoras.run());
        actualizarHoras.run();

        pnlCentro.add(pnlFechaHora);
        pnlCentro.add(Box.createVerticalStrut(20));

        JPanel pnlMat = new JPanel();
        pnlMat.setLayout(new BoxLayout(pnlMat, BoxLayout.Y_AXIS));
        pnlMat.setBackground(bgDark);

        javax.swing.border.TitledBorder borderMat = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(28, 40, 62)), "Material opcional (Precio Pista: " + pista.getPrecio() + "€)"
        );
        borderMat.setTitleColor(textPrimary);
        borderMat.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        pnlMat.setBorder(borderMat);

        MaterialDaoSQlite matDAO = new MaterialDaoSQlite();
        List<entidades.Material> materiales = matDAO.obtenerPorDeporte(pista.getDeporte());

        if (materiales.isEmpty()) {
            JLabel noMat = new JLabel(" No hay materiales para este deporte.");
            noMat.setForeground(new Color(120, 140, 170));
            pnlMat.add(noMat);
        } else {
            for (entidades.Material m : materiales) {
                JPanel fila = new JPanel(new BorderLayout(10, 0));
                fila.setBackground(bgDark);
                fila.setBorder(new EmptyBorder(8, 5, 8, 5));

                JLabel info = new JLabel(m.getNombre() + " (+" + m.getPrecio() + "€) - Stock: " + m.getCantidad());
                info.setForeground(textPrimary);
                info.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                JButton btnAdd = new JButton("Añadir");
                btnAdd.setPreferredSize(new Dimension(85, 30));
                btnAdd.setFocusPainted(false);
                btnAdd.setBackground(accentCyan);
                btnAdd.setForeground(new Color(10, 14, 23));
                btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btnAdd.setEnabled(m.getCantidad() > 0);
                btnAdd.addActionListener(ev -> {
                    if(matDAO.reservarMaterial(m.getId())) {
                        precioMaterialesAcumulado[0] += m.getPrecio();
                        materialesSeleccionados.add(m.getId());
                        info.setText(m.getNombre() + " (+" + m.getPrecio() + "€) - Stock: " + (m.getCantidad() - 1));

                        btnAdd.setEnabled(false);
                        btnAdd.setText("Añadido");
                        btnAdd.setBackground(new Color(0, 230, 118));
                    }
                });
                fila.add(info, BorderLayout.CENTER);
                fila.add(btnAdd, BorderLayout.EAST);
                pnlMat.add(fila);
            }
        }

        pnlCentro.add(pnlMat);
        dialogo.add(new JScrollPane(pnlCentro), BorderLayout.CENTER);

        JButton btnFin = new JButton("Confirmar y Pagar");
        btnFin.setBackground(new Color(0, 230, 118));
        btnFin.setForeground(new Color(10, 14, 23));
        btnFin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnFin.setFocusPainted(false);
        btnFin.setPreferredSize(new Dimension(0, 50));

        btnFin.addActionListener(a -> {
            String horaSelec = (String) comboHoras.getSelectedItem();
            if (horaSelec == null || horaSelec.equals("Horarios no disponibles")) {
                JOptionPane.showMessageDialog(dialogo, "Selecciona un día y hora disponibles.");
                return;
            }

            double totalFinal = pista.getPrecio() + precioMaterialesAcumulado[0];
            String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy").format(spinnerFecha.getValue());
            String fechaYHoraFinal = fechaFormateada + " " + horaSelec;
            String textoMateriales = String.join(",", materialesSeleccionados);

            boolean exito = guardarReservaEnBD_Completo(usuarioLogueado.getId(), pista.getId(), fechaYHoraFinal, totalFinal, textoMateriales);
            if (exito) {
                JOptionPane.showMessageDialog(dialogo, "¡Reserva confirmada!\nTotal a pagar en el club: " + totalFinal + "€");
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, "Hubo un error al guardar la reserva.");
            }
        });

        dialogo.add(btnFin, BorderLayout.SOUTH);
        dialogo.getContentPane().setBackground(bgDark);
        dialogo.setVisible(true);
    }

    private List<String> obtenerHorasOcupadas(String idPista, String fechaBusqueda) {
        List<String> ocupadas = new ArrayList<>();
        String sql = "SELECT fechaHoraInicio FROM Reservas WHERE idPista = ? AND fechaHoraInicio LIKE ?";
        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idPista);
            pstmt.setString(2, fechaBusqueda + "%");
            try(java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String fechaHoraCompleta = rs.getString("fechaHoraInicio");
                    String soloHora = fechaHoraCompleta.substring(fechaHoraCompleta.length() - 5);
                    ocupadas.add(soloHora);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ocupadas;
    }

    private boolean guardarReservaEnBD_Completo(String idUsuario, String idPista, String fechaYHoraFinal, double precioCalculado, String materiales) {
        String sql = "INSERT INTO Reservas (id, idUsuario, idPista, fechaHoraInicio, precioTotal, estado, materiales) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, java.util.UUID.randomUUID().toString());
            pstmt.setString(2, idUsuario);
            pstmt.setString(3, idPista);
            pstmt.setString(4, fechaYHoraFinal);
            pstmt.setDouble(5, precioCalculado);
            pstmt.setString(6, "Confirmada");
            pstmt.setString(7, materiales);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}