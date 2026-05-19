package dao;

import entidades.Material;
import utilidades.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDaoSQlite implements MaterialDAO {

    @Override
    public List<Material> obtenerPorDeporte(String deporte) {
        List<Material> materiales = new ArrayList<>();
        String sql = "SELECT * FROM Materiales WHERE LOWER(deporte) LIKE LOWER(?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + deporte.trim() + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                materiales.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materiales;
    }

    @Override
    public Material buscarPorId(String id) {
        String sql = "SELECT * FROM Materiales WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean reservarMaterial(String idMaterial) {
        String sql = "UPDATE Materiales SET Cantidad = Cantidad - 1 WHERE id = ? AND Cantidad > 0";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idMaterial);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean actualizarStock(String idMaterial, int nuevaCantidad) {
        String sql = "UPDATE Materiales SET Cantidad = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuevaCantidad);
            pstmt.setString(2, idMaterial);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<Material> obtenerTodos() {
        List<Material> materiales = new ArrayList<>();
        String sql = "SELECT * FROM Materiales";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                materiales.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materiales;
    }

    @Override
    public boolean anadirMaterial(Material m) {
        String sql = "INSERT INTO Materiales (id, nombre, deporte, precio, Cantidad, stockDisponible) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String idNuevo = (m.getId() == null || m.getId().isEmpty()) ? java.util.UUID.randomUUID().toString() : m.getId();

            pstmt.setString(1, idNuevo);
            pstmt.setString(2, m.getNombre());
            pstmt.setString(3, m.getDeporte());
            pstmt.setDouble(4, m.getPrecio());
            pstmt.setInt(5, m.getCantidad());
            pstmt.setInt(6, m.isStockDisponible() ? 1 : 0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al añadir material: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarMaterial(String idMaterial) {
        String sql = "DELETE FROM Materiales WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idMaterial);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar material: " + e.getMessage());
            return false;
        }
    }

    private Material mapear(ResultSet rs) throws SQLException {
        Material m = new Material();
        m.setId(rs.getString("id"));
        m.setNombre(rs.getString("nombre"));
        m.setDeporte(rs.getString("deporte"));
        m.setPrecio(rs.getDouble("precio"));
        m.setCantidad(rs.getInt("Cantidad"));
        m.setStockDisponible(rs.getBoolean("stockDisponible"));
        return m;
    }

    // Añade este método dentro de tu MaterialDaoSQLite.java
    public boolean devolverMaterial(String idMaterial) {
        String sql = "UPDATE Materiales SET cantidad = cantidad + 1 WHERE id = ?";
        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idMaterial);
            return pstmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}