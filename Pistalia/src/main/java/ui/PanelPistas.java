package ui;

import dao.MaterialDaoSQLite;
import dao.PistaDAO;
import dao.PistaDaoSQLite;
import entidades.Material;
import entidades.Pista;
import entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanelPistas extends JPanel {

    private Usuario usuarioLogueado;
    private PistaDAO pistaDAO;
    private JPanel panelCuadricula;

    public PanelPistas(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.pistaDAO = new PistaDaoSQLite();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Pistas Disponibles");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(44, 62, 80));
        panelNorte.add(titulo, BorderLayout.WEST);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFiltros.setBackground(new Color(245, 247, 250));

        String[] deportes = {"Todas", "Pádel", "Tenis", "Fútbol", "Baloncesto"};
        for (String dep : deportes) {
            JButton btnFiltro = new JButton(dep);
            btnFiltro.setFocusPainted(false);
            btnFiltro.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnFiltro.addActionListener(e -> filtrarPistas(dep));
            panelFiltros.add(btnFiltro);
        }
        panelNorte.add(panelFiltros, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        panelCuadricula = new JPanel();
        panelCuadricula.setLayout(new GridLayout(0, 2, 20, 20));
        panelCuadricula.setBackground(new Color(245, 247, 250));
        panelCuadricula.setBorder(new EmptyBorder(20, 0, 0, 0));

        actualizarVistaPistas(pistaDAO.obtenerTodas());

        JScrollPane scrollPane = new JScrollPane(panelCuadricula);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void filtrarPistas(String deporte) {
        List<Pista> filtradas;
        if (deporte.equals("Todas")) {
            filtradas = pistaDAO.obtenerTodas();
        } else {
            filtradas = pistaDAO.obtenerPorDeporte(deporte);
        }
        actualizarVistaPistas(filtradas);
    }

    private void actualizarVistaPistas(List<Pista> listaPistas) {
        panelCuadricula.removeAll();
        if (listaPistas != null && !listaPistas.isEmpty()) {
            for (Pista pista : listaPistas) {
                panelCuadricula.add(crearTarjetaPista(pista));
            }
        } else {
            panelCuadricula.add(new JLabel("No hay pistas disponibles para este deporte."));
        }
        panelCuadricula.revalidate();
        panelCuadricula.repaint();
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
            String numerosEnId = idPista.replaceAll("\\D+", ""); // Extrae solo los números del nombre/ID
            if (!numerosEnId.isEmpty()) {
                numeroFoto = Integer.parseInt(numerosEnId);
                numeroFoto = ((numeroFoto - 1) % 5) + 1; // Fuerza a que esté entre 1 y 5
            } else {
                numeroFoto = (Math.abs(idPista.hashCode()) % 5) + 1; // Por si escribes letras sin números
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

    private JPanel crearTarjetaPista(Pista pista) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblImagen = crearImagenPista(pista.getId(), pista.getDeporte(), 350, 140);
        lblImagen.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblImagen);
        tarjeta.add(Box.createVerticalStrut(15));

        String nombreLimpio = pista.getId();
        if (nombreLimpio.contains(" (")) {
            nombreLimpio = nombreLimpio.substring(0, nombreLimpio.lastIndexOf(" ("));
        }

        JLabel lblNombre = new JLabel("Pista " + nombreLimpio + " - " + pista.getDeporte() + " (" + pista.getPrecio() + "€/h)");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        boolean operativa = pista.isDisponible();
        JLabel lblEstado = new JLabel(operativa ? "Operativa (Ver Horarios)" : "En Mantenimiento");
        lblEstado.setForeground(operativa ? new Color(46, 204, 113) : Color.RED);
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReservar = new JButton("Reservar");
        btnReservar.setBackground(operativa ? new Color(52, 152, 219) : Color.GRAY);
        btnReservar.setForeground(Color.WHITE);
        btnReservar.setEnabled(operativa);
        btnReservar.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnReservar.addActionListener(e -> {
            if (usuarioLogueado == null || "Invitado".equalsIgnoreCase(usuarioLogueado.getNombre())) {
                JOptionPane.showMessageDialog(this, "Inicia sesión para reservar.");
            } else {
                abrirVentanaReserva(pista);
            }
        });

        tarjeta.add(lblNombre);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(lblEstado);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(btnReservar);

        return tarjeta;
    }

    private void abrirVentanaReserva(Pista pista) {
        final double[] precioMaterialesAcumulado = {0.0};
        final List<String> materialesSeleccionados = new ArrayList<>();

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirmar Reserva", true);
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

        MaterialDaoSQLite matDAO = new MaterialDaoSQLite();
        List<Material> materiales = matDAO.obtenerPorDeporte(pista.getDeporte());
        JPanel pnlMat = new JPanel();
        pnlMat.setLayout(new BoxLayout(pnlMat, BoxLayout.Y_AXIS));
        pnlMat.setBorder(BorderFactory.createTitledBorder("Añadir Material (Precio Pista: " + pista.getPrecio() + "€)"));

        if (materiales.isEmpty()) {
            pnlMat.add(new JLabel("No hay materiales específicos para este deporte."));
        } else {
            for (Material m : materiales) {
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
            String f = new SimpleDateFormat("dd/MM/yyyy").format(spinnerFecha.getValue()) + " " + horaSelec;
            String textoMateriales = String.join(",", materialesSeleccionados);

            if (guardarReservaEnBD_Completo(usuarioLogueado.getId(), pista.getId(), f, totalFinal, textoMateriales)) {
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
                    String full = rs.getString("fechaHoraInicio");
                    ocupadas.add(full.substring(full.length() - 5));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ocupadas;
    }

    private boolean guardarReservaEnBD_Completo(String idU, String idP, String fh, double precioCalculado, String materiales) {
        String sql = "INSERT INTO Reservas (id, idUsuario, idPista, fechaHoraInicio, precioTotal, estado, materiales) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, java.util.UUID.randomUUID().toString());
            pstmt.setString(2, idU);
            pstmt.setString(3, idP);
            pstmt.setString(4, fh);
            pstmt.setDouble(5, precioCalculado);
            pstmt.setString(6, "Confirmada");
            pstmt.setString(7, materiales);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}