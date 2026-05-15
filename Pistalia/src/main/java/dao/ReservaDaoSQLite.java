package dao;

import entidades.Reserva;
import utilidades.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDaoSQLite implements ReservaDAO {

    public ReservaDaoSQLite() {
        // Truco para añadir la columna automáticamente si no existe en tu BD antigua
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE Reservas ADD COLUMN materiales TEXT DEFAULT ''");
        } catch (Exception e) { /* Ignorar si la columna ya existe */ }
    }

    @Override
    public boolean guardar(Reserva r) {
        // AHORA GUARDAMOS TAMBIÉN LOS MATERIALES
        String sql = "INSERT INTO Reservas (id, idUsuario, idPista, fechaHoraInicio, precioTotal, estado, materiales) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getId());
            pstmt.setString(2, r.getIdUsuario());
            pstmt.setString(3, r.getIdPista());
            pstmt.setString(4, r.getFechaHoraInicio());
            pstmt.setDouble(5, r.getPrecioTotal());
            pstmt.setString(6, r.getEstado());
            pstmt.setString(7, r.getMaterialesAlquilados() != null ? r.getMaterialesAlquilados() : "");
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Reserva> obtenerPorUsuario(String idUsuario) {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT r.*, p.disponible, p.deporte FROM Reservas r JOIN Pistas p ON r.idPista = p.id WHERE r.idUsuario = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idUsuario);

            // EL FIX: Envolvemos el ResultSet en un try para que se cierre SIEMPRE
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reserva r = new Reserva();
                    r.setId(rs.getString("id"));
                    r.setIdUsuario(rs.getString("idUsuario"));
                    r.setIdPista(rs.getString("idPista"));
                    r.setFechaHoraInicio(rs.getString("fechaHoraInicio"));
                    r.setPrecioTotal(rs.getDouble("precioTotal"));
                    r.setEstado(rs.getString("estado"));
                    r.setPistaDisponible(rs.getInt("disponible") == 1);
                    r.setDeportePista(rs.getString("deporte"));

                    try {
                        r.setMaterialesAlquilados(rs.getString("materiales"));
                    } catch (Exception e) { r.setMaterialesAlquilados(""); }

                    lista.add(r);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    @Override
    public boolean eliminar(String idReserva) {
        String sql = "DELETE FROM Reservas WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idReserva);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}