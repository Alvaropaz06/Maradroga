package ui;

import dao.MaterialDAO;
import dao.MaterialDaoSQLite;
import entidades.Material;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PanelAdminMateriales extends JPanel {

    private MaterialDAO materialDAO;
    private JPanel panelLista;

    public PanelAdminMateriales() {
        this.materialDAO = new MaterialDaoSQLite();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Gestión de Materiales");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(44, 62, 80));

        JButton btnAnadir = new JButton("+ Añadir Material Nuevo");
        btnAnadir.setBackground(new Color(46, 204, 113));
        btnAnadir.setForeground(Color.WHITE);
        btnAnadir.addActionListener(e -> abrirDialogoNuevoMaterial());

        panelNorte.add(titulo, BorderLayout.WEST);
        panelNorte.add(btnAnadir, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        panelLista = new JPanel();
        panelLista.setLayout(new BoxLayout(panelLista, BoxLayout.Y_AXIS));
        panelLista.setBackground(new Color(245, 247, 250));

        JScrollPane scrollPane = new JScrollPane(panelLista);
        scrollPane.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        cargarMateriales();
    }

    private void cargarMateriales() {
        panelLista.removeAll();
        List<Material> lista = materialDAO.obtenerTodos();

        if (lista.isEmpty()) {
            panelLista.add(new JLabel("No hay materiales en la base de datos."));
        } else {
            for (Material m : lista) {
                JPanel tarjeta = new JPanel(new BorderLayout());
                tarjeta.setBackground(Color.WHITE);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 230)),
                        new EmptyBorder(10, 15, 10, 15)
                ));
                tarjeta.setMaximumSize(new Dimension(800, 60));

                String info = String.format("<html><b>%s</b> - %s | Precio: %.2f€ | Stock: %d</html>",
                        m.getNombre(), m.getDeporte(), m.getPrecio(), m.getCantidad());
                tarjeta.add(new JLabel(info), BorderLayout.CENTER);

                JButton btnEliminar = new JButton("Eliminar");
                btnEliminar.setBackground(new Color(231, 76, 60));
                btnEliminar.setForeground(Color.WHITE);
                btnEliminar.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar este material?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        materialDAO.eliminarMaterial(m.getId());
                        cargarMateriales();
                    }
                });

                tarjeta.add(btnEliminar, BorderLayout.EAST);
                panelLista.add(tarjeta);
                panelLista.add(Box.createVerticalStrut(10));
            }
        }
        panelLista.revalidate();
        panelLista.repaint();
    }

    private void abrirDialogoNuevoMaterial() {
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nuevo Material", true);
        dialogo.setSize(350, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField txtNombre = new JTextField();
        JTextField txtDeporte = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtCantidad = new JTextField();

        dialogo.add(new JLabel(" Nombre:")); dialogo.add(txtNombre);
        dialogo.add(new JLabel(" Deporte:")); dialogo.add(txtDeporte);
        dialogo.add(new JLabel(" Precio (€):")); dialogo.add(txtPrecio);
        dialogo.add(new JLabel(" Cantidad Inicial:")); dialogo.add(txtCantidad);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                Material m = new Material();
                m.setNombre(txtNombre.getText());
                m.setDeporte(txtDeporte.getText());
                m.setPrecio(Double.parseDouble(txtPrecio.getText()));
                m.setCantidad(Integer.parseInt(txtCantidad.getText()));
                m.setStockDisponible(m.getCantidad() > 0);

                if(materialDAO.anadirMaterial(m)) {
                    JOptionPane.showMessageDialog(dialogo, "Material añadido con éxito");
                    dialogo.dispose();
                    cargarMateriales();
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Error al añadir material en la Base de Datos");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Por favor, revisa los datos introducidos.");
            }
        });

        dialogo.add(new JLabel());
        dialogo.add(btnGuardar);

        dialogo.setVisible(true);
    }
}