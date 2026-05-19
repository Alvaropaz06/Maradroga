package dao;

import entidades.Mensaje;
import utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MensajeDaoSQLite {

    public MensajeDaoSQLite() {
        // Crea la tabla automáticamente si no existe
        String sql = "CREATE TABLE IF NOT EXISTS Mensajes (" +
                "id TEXT PRIMARY KEY, " +
                "idUsuario TEXT, " +
                "nombreUsuario TEXT, " +
                "textoMensaje TEXT, " +
                "respuesta TEXT, " +
                "fecha TEXT)";
        try (Connection conn = ConexionBD.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean enviarMensaje(Mensaje m) {
        String sql = "INSERT INTO Mensajes (id, idUsuario, nombreUsuario, textoMensaje, respuesta, fecha) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getId());
            pstmt.setString(2, m.getIdUsuario());
            pstmt.setString(3, m.getNombreUsuario());
            pstmt.setString(4, m.getTextoMensaje());
            pstmt.setString(5, m.getRespuesta());
            pstmt.setString(6, m.getFecha());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean responderMensaje(String idMensaje, String respuesta) {
        String sql = "UPDATE Mensajes SET respuesta = ? WHERE id = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, respuesta);
            pstmt.setString(2, idMensaje);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<Mensaje> obtenerPorUsuario(String idUsuario) {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM Mensajes WHERE idUsuario = ? ORDER BY fecha DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearMensaje(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    public List<Mensaje> obtenerTodos() {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM Mensajes ORDER BY fecha DESC";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) lista.add(mapearMensaje(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return lista;
    }

    private Mensaje mapearMensaje(ResultSet rs) throws Exception {
        Mensaje m = new Mensaje();
        m.setId(rs.getString("id"));
        m.setIdUsuario(rs.getString("idUsuario"));
        m.setNombreUsuario(rs.getString("nombreUsuario"));
        m.setTextoMensaje(rs.getString("textoMensaje")); // Corregido
        m.setRespuesta(rs.getString("respuesta"));
        m.setFecha(rs.getString("fecha"));
        return m;
    }
}