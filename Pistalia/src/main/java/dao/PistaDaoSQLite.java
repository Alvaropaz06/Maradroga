package dao;

import entidades.Pista;
import utilidades.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PistaDaoSQLite implements PistaDAO {

    @Override
    public List<Pista> obtenerTodas() {
        List<Pista> pistas = new ArrayList<>();
        String sql = "SELECT * FROM Pistas";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { pistas.add(mapear(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return pistas;
    }

    @Override
    public List<Pista> obtenerPorDeporte(String deporte) {
        List<Pista> pistas = new ArrayList<>();
        String sql = "SELECT * FROM Pistas WHERE LOWER(deporte) = LOWER(?) AND disponible = 1";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, deporte);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) { pistas.add(mapear(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return pistas;
    }

    @Override
    public boolean actualizarDisponibilidad(String idPista, boolean disponible) {
        String sql = "UPDATE Pistas SET disponible = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, disponible ? 1 : 0);
            pstmt.setString(2, idPista);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    @Override
    public boolean anadirPista(Pista pista) {
        String sql = "INSERT INTO Pistas (id, deporte, disponible, precio) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pista.getId());
            pstmt.setString(2, pista.getDeporte());
            pstmt.setInt(3, pista.isDisponible() ? 1 : 0);
            pstmt.setDouble(4, pista.getPrecio());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Si falta la columna precio, la creamos al vuelo
            try (Connection conn2 = ConexionBD.conectar()) {
                conn2.createStatement().execute("ALTER TABLE Pistas ADD COLUMN precio DOUBLE DEFAULT 10.0");
            } catch (Exception ex) {}
            return false;
        }
    }

    @Override
    public boolean eliminarPista(String idPista) {
        String sql = "DELETE FROM Pistas WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idPista);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    private Pista mapear(ResultSet rs) throws SQLException {
        Pista p = new Pista();
        p.setId(rs.getString("id"));
        p.setDeporte(rs.getString("deporte"));
        p.setDisponible(rs.getInt("disponible") == 1);
        try {
            p.setPrecio(rs.getDouble("precio"));
        } catch (Exception e) { p.setPrecio(10.0); } // Precio por defecto si hay lío
        return p;
    }
}