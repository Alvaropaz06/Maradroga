package ui;

import dao.PistaDAO;
import dao.PistaDaoSQLite;
import entidades.Pista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PanelAdminPistas extends JPanel {

    private PistaDAO pistaDAO;
    private JPanel panelLista;

    public PanelAdminPistas() {
        this.pistaDAO = new PistaDaoSQLite();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Pistas y Mantenimiento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(44, 62, 80));

        JButton btnAnadir = new JButton("+ Añadir Pista Nueva");
        btnAnadir.setBackground(new Color(46, 204, 113));
        btnAnadir.setForeground(Color.WHITE);
        btnAnadir.addActionListener(e -> abrirDialogoNuevaPista());

        panelNorte.add(titulo, BorderLayout.WEST);
        panelNorte.add(btnAnadir, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setBackground(new Color(245, 247, 250));

        JScrollPane scrollPane = new JScrollPane(panelLista);
        scrollPane.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        cargarPistas();
    }

    private void cargarPistas() {
        panelLista.removeAll();
        List<Pista> lista = pistaDAO.obtenerTodas();

        if (lista.isEmpty()) {
            panelLista.add(new JLabel("No hay pistas registradas."));
        } else {
            for (Pista p : lista) {
                JPanel tarjeta = new JPanel(new BorderLayout());
                tarjeta.setBackground(Color.WHITE);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 230)),
                        new EmptyBorder(10, 15, 10, 15)
                ));
                tarjeta.setMaximumSize(new Dimension(800, 60));

                // --- LIMPIEZA VISUAL DEL NOMBRE ---
                String nombreLimpio = p.getId();
                if (nombreLimpio.contains(" (")) {
                    nombreLimpio = nombreLimpio.substring(0, nombreLimpio.lastIndexOf(" ("));
                }

                String estado = p.isDisponible() ? "<font color='green'>Operativa</font>" : "<font color='red'>En Mantenimiento</font>";

                // Ahora mostramos el nombre limpio y le añadimos el deporte explícitamente
                String info = String.format("<html><b>Pista %s - %s</b> | Estado: %s</html>", nombreLimpio, p.getDeporte(), estado);
                tarjeta.add(new JLabel(info), BorderLayout.CENTER);

                JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                pnlBotones.setBackground(Color.WHITE);

                JButton btnEstado = new JButton(p.isDisponible() ? "Bloquear" : "Habilitar");
                btnEstado.setBackground(p.isDisponible() ? new Color(230, 126, 34) : new Color(46, 204, 113));
                btnEstado.setForeground(Color.WHITE);
                btnEstado.addActionListener(e -> {
                    pistaDAO.actualizarDisponibilidad(p.getId(), !p.isDisponible());
                    cargarPistas();
                });

                JButton btnEliminar = new JButton("Eliminar");
                btnEliminar.setBackground(new Color(231, 76, 60));
                btnEliminar.setForeground(Color.WHITE);
                btnEliminar.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "¿Borrar pista definitivamente?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        pistaDAO.eliminarPista(p.getId());
                        cargarPistas();
                    }
                });

                pnlBotones.add(btnEstado);
                pnlBotones.add(btnEliminar);
                tarjeta.add(pnlBotones, BorderLayout.EAST);

                panelLista.add(tarjeta);
                panelLista.add(Box.createVerticalStrut(10));
            }
        }
        panelLista.revalidate();
        panelLista.repaint();
    }

    // Busca este método en tu PanelAdminPistas y sustitúyelo
    private void abrirDialogoNuevaPista() {
        JPanel panelDialogo = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField txtNombrePista = new JTextField();
        JTextField txtPrecio = new JTextField("15.0"); // Precio por defecto
        String[] deportes = {"Pádel", "Tenis", "Fútbol", "Baloncesto"};
        JComboBox<String> comboDeportes = new JComboBox<>(deportes);

        panelDialogo.add(new JLabel("Nombre (Ej: Central):"));
        panelDialogo.add(txtNombrePista);
        panelDialogo.add(new JLabel("Deporte:"));
        panelDialogo.add(comboDeportes);
        panelDialogo.add(new JLabel("Precio por hora (€):"));
        panelDialogo.add(txtPrecio);

        int result = JOptionPane.showConfirmDialog(this, panelDialogo, "Nueva Pista", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double precio = Double.parseDouble(txtPrecio.getText().replace(",", "."));
                Pista p = new Pista();
                p.setId(txtNombrePista.getText().trim() + " (" + comboDeportes.getSelectedItem() + ")");
                p.setDeporte((String) comboDeportes.getSelectedItem());
                p.setDisponible(true);
                p.setPrecio(precio);

                if (pistaDAO.anadirPista(p)) cargarPistas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Precio no válido.");
            }
        }
    }
}