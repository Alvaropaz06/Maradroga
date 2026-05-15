package ui;

import dao.ReservaDAO;
import dao.ReservaDaoSQLite;
import entidades.Reserva;
import entidades.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelMisReservas extends JPanel {

    private Usuario usuario;
    private ReservaDAO reservaDAO;
    private JPanel panelProximas;
    private JPanel panelHistorial;
    private DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PanelMisReservas(Usuario usuario) {
        this.usuario = usuario;
        this.reservaDAO = new ReservaDaoSQLite();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Mis Reservas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBorder(new EmptyBorder(0, 10, 15, 0));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panelProximas = new JPanel();
        panelProximas.setLayout(new BoxLayout(panelProximas, BoxLayout.Y_AXIS));
        panelProximas.setBackground(new Color(245, 247, 250));

        panelHistorial = new JPanel();
        panelHistorial.setLayout(new BoxLayout(panelHistorial, BoxLayout.Y_AXIS));
        panelHistorial.setBackground(new Color(245, 247, 250));

        pestañas.addTab("Próximos Partidos", new JScrollPane(panelProximas));
        pestañas.addTab("Historial de Juego", new JScrollPane(panelHistorial));

        add(pestañas, BorderLayout.CENTER);

        organizarReservas();
        lanzarNotificacionEmergente();
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
                    panelHistorial.add(Box.createVerticalStrut(10));
                } else {
                    panelProximas.add(crearTarjetaReserva(r, true));
                    panelProximas.add(Box.createVerticalStrut(10));
                }
            } catch (Exception e) {
                panelProximas.add(crearTarjetaReserva(r, true));
            }
        }

        if (panelProximas.getComponentCount() == 0)
            panelProximas.add(new JLabel("No tienes partidos programados."));
        if (panelHistorial.getComponentCount() == 0)
            panelHistorial.add(new JLabel("Aún no tienes un historial de partidos jugados."));

        panelProximas.revalidate();
        panelHistorial.revalidate();
        panelProximas.repaint();
        panelHistorial.repaint();
    }

    private JLabel crearImagenPista(String idPista, String deporte, int width, int height) {
        JLabel lblFoto = new JLabel();
        lblFoto.setPreferredSize(new Dimension(width, height));
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
                lblFoto.setText("Foto " + numeroFoto);
                lblFoto.setOpaque(true);
                lblFoto.setBackground(new Color(220, 225, 230));
            }
        } catch (Exception e) {
            lblFoto.setText("Foto");
        }
        return lblFoto;
    }

    private JPanel crearTarjetaReserva(Reserva r, boolean esFutura) {
        JPanel tarjeta = new JPanel(new BorderLayout(15, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                new EmptyBorder(15, 20, 15, 20)
        ));
        tarjeta.setMaximumSize(new Dimension(850, 160));

        JLabel lblImagen = crearImagenPista(r.getIdPista(), r.getDeportePista(), 120, 100);
        tarjeta.add(lblImagen, BorderLayout.WEST);

        String idCompleto = r.getIdPista();
        String nombrePista = idCompleto;
        if (idCompleto.contains(" (") && idCompleto.endsWith(")")) {
            nombrePista = idCompleto.substring(0, idCompleto.lastIndexOf(" ("));
        }

        String deporte = r.getDeportePista() != null ? " - " + r.getDeportePista() : "";
        String avisoMantenimiento = (esFutura && !r.isPistaDisponible()) ?
                "<br><b style='color:red;'>¡ATENCIÓN! PISTA CERRADA POR MANTENIMIENTO</b>" : "";

        String colorEstado = esFutura ? "green" : "gray";
        String textoEstado = esFutura ? r.getEstado() : "Finalizada";

        String info = "<html><div style='padding: 5px 0px;'>" +
                "<font size='5'><b>" + r.getFechaHoraInicio() + "</b></font><br>" +
                "Pista: <b>" + nombrePista + deporte + "</b><br>" +
                "Total a pagar: <b style='color:blue;'>" + String.format("%.2f", r.getPrecioTotal()) + "€</b><br>" +
                "Estado: <font color='"+colorEstado+"'>" + textoEstado + "</font>" + avisoMantenimiento +
                "</div></html>";

        JLabel lblInfo = new JLabel(info);
        tarjeta.add(lblInfo, BorderLayout.CENTER);

        if (esFutura) {
            JButton btnCancelar = new JButton("Cancelar");
            btnCancelar.setBackground(new Color(231, 76, 60));
            btnCancelar.setForeground(Color.WHITE);
            btnCancelar.setFocusPainted(false);

            btnCancelar.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Cancelar este partido?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (r.getMaterialesAlquilados() != null && !r.getMaterialesAlquilados().isEmpty()) {
                        dao.MaterialDaoSQLite matDao = new dao.MaterialDaoSQLite();
                        String[] idsMateriales = r.getMaterialesAlquilados().split(",");
                        for (String idMat : idsMateriales) {
                            if (!idMat.trim().isEmpty()) {
                                matDao.devolverMaterial(idMat.trim());
                            }
                        }
                    }
                    if (reservaDAO.eliminar(r.getId())) organizarReservas();
                }
            });
            tarjeta.add(btnCancelar, BorderLayout.EAST);
        } else {
            JLabel lblCheck = new JLabel("✓ Jugado");
            lblCheck.setForeground(new Color(180, 180, 180));
            tarjeta.add(lblCheck, BorderLayout.EAST);
        }

        return tarjeta;
    }

    private void lanzarNotificacionEmergente() {
        List<Reserva> reservas = reservaDAO.obtenerPorUsuario(usuario.getId());
        LocalDateTime ahora = LocalDateTime.now();
        boolean hayProblemas = false;

        for (Reserva r : reservas) {
            try {
                LocalDateTime fecha = LocalDateTime.parse(r.getFechaHoraInicio(), formateador);
                if (fecha.isAfter(ahora) && !r.isPistaDisponible()) {
                    hayProblemas = true;
                    break;
                }
            } catch(Exception e) {}
        }

        if (hayProblemas) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "¡Atención! Tienes partidos próximos en pistas cerradas.\nRevisa la pestaña de 'Próximos Partidos'.",
                        "Aviso de Mantenimiento",
                        JOptionPane.WARNING_MESSAGE);
            });
        }
    }
}