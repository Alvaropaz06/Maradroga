package ui;

import entidades.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PantallaPrincipal extends JFrame {

    private Usuario usuarioLogueado;
    private CardLayout cardLayout;
    private JPanel panelContenedor;

    public PantallaPrincipal(Usuario usuario) {
        this.usuarioLogueado = usuario;

        setTitle("Pistalia - Panel Principal");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));
        panelMenu.setBackground(new Color(44, 62, 80));
        panelMenu.setPreferredSize(new Dimension(230, 0));
        panelMenu.setBorder(new EmptyBorder(25, 15, 25, 15));

        JPanel pnlLogoBg = new JPanel(new BorderLayout());
        pnlLogoBg.setBackground(Color.WHITE);
        pnlLogoBg.setMaximumSize(new Dimension(180, 180));
        pnlLogoBg.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlLogoBg.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblLogoSide = new JLabel();
        lblLogoSide.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            URL imgURL = getClass().getResource("/img/logo.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage();
                int origW = img.getWidth(null);
                int origH = img.getHeight(null);
                int targetW = 150;
                int targetH = (origH * targetW) / origW;
                Image scaled = img.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                lblLogoSide.setIcon(new ImageIcon(scaled));
            } else {
                lblLogoSide.setText("PISTALIA");
                lblLogoSide.setFont(new Font("Segoe UI", Font.BOLD, 22));
            }
        } catch (Exception ex) {
            lblLogoSide.setText("PISTALIA");
            lblLogoSide.setFont(new Font("Segoe UI", Font.BOLD, 22));
        }
        pnlLogoBg.add(lblLogoSide, BorderLayout.CENTER);

        panelMenu.add(pnlLogoBg);

        JButton btnInicio = crearBotonMenu("Inicio");
        JButton btnReservar = crearBotonMenu("Reservar Pista");
        JButton btnMisReservas = crearBotonMenu("Mis Reservas");
        JButton btnCerrarSesion = crearBotonMenu("Cerrar Sesión");

        panelMenu.add(Box.createVerticalStrut(30));
        panelMenu.add(btnInicio);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnReservar);
        panelMenu.add(Box.createVerticalStrut(15));
        panelMenu.add(btnMisReservas);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        panelContenedor.add(crearPanelBienvenida(), "INICIO");
        panelContenedor.add(new PanelPistas(usuarioLogueado), "PISTAS");
        panelContenedor.add(new PanelMisReservas(usuarioLogueado), "MIS_RESERVAS");

        btnInicio.addActionListener(e -> {
            panelContenedor.add(crearPanelBienvenida(), "INICIO_RECARGADO");
            cardLayout.show(panelContenedor, "INICIO_RECARGADO");
        });

        btnReservar.addActionListener(e -> cardLayout.show(panelContenedor, "PISTAS"));

        btnMisReservas.addActionListener(e -> {
            panelContenedor.add(new PanelMisReservas(usuarioLogueado), "MIS_RESERVAS");
            cardLayout.show(panelContenedor, "MIS_RESERVAS");
        });

        btnCerrarSesion.addActionListener(e -> {
            new PantallaLogin().setVisible(true);
            dispose();
        });

        if (usuarioLogueado != null && usuarioLogueado.isEsAdmin()) {
            JButton btnAdminPistas = crearBotonMenu("⚙ Gestión Pistas");
            JButton btnAdminMaterial = crearBotonMenu("⚙ Gestión Material");

            panelMenu.add(Box.createVerticalStrut(15));
            panelMenu.add(btnAdminPistas);
            panelMenu.add(Box.createVerticalStrut(10));
            panelMenu.add(btnAdminMaterial);

            panelContenedor.add(new PanelAdminPistas(), "ADMIN_PISTAS");
            panelContenedor.add(new PanelAdminMateriales(), "ADMIN_MATERIAL");

            btnAdminPistas.addActionListener(e -> cardLayout.show(panelContenedor, "ADMIN_PISTAS"));
            btnAdminMaterial.addActionListener(e -> cardLayout.show(panelContenedor, "ADMIN_MATERIAL"));
        }

        panelMenu.add(Box.createVerticalGlue());
        panelMenu.add(btnCerrarSesion);

        add(panelMenu, BorderLayout.WEST);
        add(panelContenedor, BorderLayout.CENTER);

        if (usuarioLogueado != null && !"Invitado".equals(usuarioLogueado.getNombre())) {
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

    private JLabel crearImagenPista(String idPista, String deporte, int width, int height) {
        JLabel lblFoto = new JLabel();
        lblFoto.setPreferredSize(new Dimension(width, height));
        lblFoto.setMaximumSize(new Dimension(width, height));
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            String depLimpio = deporte != null ? deporte.toLowerCase()
                    .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u") : "default";

            // LÓGICA DE FOTOS POR ID NUMÉRICO
            int numeroFoto = 1;
            String numerosEnId = idPista.replaceAll("\\D+", "");
            if (!numerosEnId.isEmpty()) {
                numeroFoto = Integer.parseInt(numerosEnId);
                numeroFoto = ((numeroFoto - 1) % 5) + 1;
            } else {
                numeroFoto = (Math.abs(idPista.hashCode()) % 5) + 1;
            }

            java.net.URL imgURL = getClass().getResource("/img/" + depLimpio + numeroFoto + ".jpg");

            if (imgURL != null) {
                ImageIcon icono = new ImageIcon(imgURL);
                Image img = icono.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                lblFoto.setIcon(new ImageIcon(img));
            } else {
                lblFoto.setText("Foto " + deporte + " " + numeroFoto);
                lblFoto.setOpaque(true);
                lblFoto.setBackground(new Color(220, 225, 230));
            }
        } catch (Exception e) {
            lblFoto.setText("Foto");
        }
        return lblFoto;
    }

    private JPanel crearPanelBienvenida() {
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(new Color(245, 247, 250));
        panelCentral.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel saludo = new JLabel("¡Bienvenido, " + usuarioLogueado.getNombre() + "!");
        saludo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        saludo.setForeground(new Color(44, 62, 80));
        panelCentral.add(saludo, BorderLayout.NORTH);

        JPanel pnlRecomendaciones = new JPanel();
        pnlRecomendaciones.setLayout(new BoxLayout(pnlRecomendaciones, BoxLayout.Y_AXIS));
        pnlRecomendaciones.setBackground(new Color(245, 247, 250));
        pnlRecomendaciones.setBorder(new EmptyBorder(30, 0, 0, 0));

        String deporteFav = usuarioLogueado.getPreferencias();

        if (deporteFav == null || deporteFav.trim().isEmpty() || "Invitado".equalsIgnoreCase(usuarioLogueado.getNombre())) {
            JLabel lblAviso = new JLabel("Reserva pistas habitualmente y te mostraremos tus favoritas aquí.");
            lblAviso.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblAviso.setForeground(Color.GRAY);
            pnlRecomendaciones.add(lblAviso);
        } else {
            JLabel lblTituloRec = new JLabel("Recomendado para ti: Pistas de " + deporteFav + " disponibles");
            lblTituloRec.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlRecomendaciones.add(lblTituloRec);
            pnlRecomendaciones.add(Box.createVerticalStrut(15));

            JPanel cuadriculaRec = new JPanel(new GridLayout(0, 2, 15, 15));
            cuadriculaRec.setBackground(new Color(245, 247, 250));

            dao.PistaDaoSQLite pistaDAO = new dao.PistaDaoSQLite();
            List<entidades.Pista> pistasRecomendadas = pistaDAO.obtenerPorDeporte(deporteFav);

            if (pistasRecomendadas.isEmpty()) {
                cuadriculaRec.add(new JLabel("Vaya, ahora mismo no hay pistas de " + deporteFav + " disponibles."));
            } else {
                for (entidades.Pista pista : pistasRecomendadas) {
                    JPanel tarjeta = new JPanel();
                    tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
                    tarjeta.setBackground(Color.WHITE);
                    tarjeta.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
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
                    info.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JButton btnReservarRec = new JButton("Reservar");
                    btnReservarRec.setBackground(new Color(52, 152, 219));
                    btnReservarRec.setForeground(Color.WHITE);
                    btnReservarRec.setFocusPainted(false);
                    btnReservarRec.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btnReservarRec.setAlignmentX(Component.LEFT_ALIGNMENT);

                    btnReservarRec.addActionListener(e -> abrirVentanaReservaInicio(pista));

                    tarjeta.add(info);
                    tarjeta.add(Box.createVerticalStrut(10));
                    tarjeta.add(btnReservarRec);

                    cuadriculaRec.add(tarjeta);
                }
            }
            pnlRecomendaciones.add(cuadriculaRec);
        }

        JScrollPane scroll = new JScrollPane(pnlRecomendaciones);
        scroll.setBorder(null);
        panelCentral.add(scroll, BorderLayout.CENTER);
        return panelCentral;
    }

    private JButton crearBotonMenu(String t) {
        JButton b = new JButton(t);
        b.setMaximumSize(new Dimension(200, 45));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void abrirVentanaReservaInicio(entidades.Pista pista) {
        final double[] precioMaterialesAcumulado = {0.0};
        final List<String> materialesSeleccionados = new ArrayList<>();

        JDialog dialogo = new JDialog(this, "Confirmar Reserva Rápida", true);
        dialogo.setSize(450, 650);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel pnlHead = new JPanel();
        pnlHead.setBackground(new Color(44, 62, 80));
        JLabel tit = new JLabel("Reserva: " + pista.getDeporte());
        tit.setForeground(Color.WHITE);
        tit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnlHead.add(tit);
        dialogo.add(pnlHead, BorderLayout.NORTH);

        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlFechaHora = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlFechaHora.setBorder(BorderFactory.createTitledBorder("Elige tu horario"));
        pnlFechaHora.setMaximumSize(new Dimension(400, 80));

        pnlFechaHora.add(new JLabel("Fecha:"));

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Date hoyLimite = cal.getTime();

        SpinnerDateModel dateModel = new SpinnerDateModel(new java.util.Date(), hoyLimite, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinnerFecha = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(dateEditor);
        pnlFechaHora.add(spinnerFecha);

        pnlFechaHora.add(new JLabel("Hora:"));
        JComboBox<String> comboHoras = new JComboBox<>();
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
        pnlMat.setBorder(BorderFactory.createTitledBorder("Añadir Material (Precio Pista: " + pista.getPrecio() + "€)"));

        dao.MaterialDaoSQLite matDAO = new dao.MaterialDaoSQLite();
        List<entidades.Material> materiales = matDAO.obtenerPorDeporte(pista.getDeporte());

        if (materiales.isEmpty()) {
            pnlMat.add(new JLabel("No hay materiales específicos para este deporte."));
        } else {
            for (entidades.Material m : materiales) {
                JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel info = new JLabel(m.getNombre() + " (+" + m.getPrecio() + "€) - Stock: " + m.getCantidad());
                JButton btnAdd = new JButton("Añadir");

                btnAdd.setEnabled(m.getCantidad() > 0);
                btnAdd.addActionListener(ev -> {
                    if(matDAO.reservarMaterial(m.getId())) {
                        precioMaterialesAcumulado[0] += m.getPrecio();
                        materialesSeleccionados.add(m.getId());
                        info.setText(m.getNombre() + " (+" + m.getPrecio() + "€) - Stock: " + (m.getCantidad() - 1));
                        JOptionPane.showMessageDialog(dialogo, "Material añadido.");
                        btnAdd.setEnabled(false);
                        btnAdd.setText("Añadido");
                    }
                });
                fila.add(info);
                fila.add(btnAdd);
                pnlMat.add(fila);
            }
        }

        pnlCentro.add(pnlMat);
        dialogo.add(new JScrollPane(pnlCentro), BorderLayout.CENTER);

        JButton btnFin = new JButton("Confirmar y Pagar");
        btnFin.setBackground(new Color(46, 204, 113));
        btnFin.setForeground(Color.WHITE);
        btnFin.setFont(new Font("Segoe UI", Font.BOLD, 14));

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