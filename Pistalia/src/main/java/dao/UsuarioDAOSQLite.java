package dao;

import entidades.Usuario;
import utilidades.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UsuarioDAOSQLite implements UsuarioDAO {

    @Override
    public void guardar(Usuario usuario) {
        // Usamos tu lógica de guardar
        registrar(usuario);
    }

    @Override
    public boolean registrar(Usuario usuario) {
        String sql = "INSERT INTO Usuarios(id, nombre, email, password, esAdmin) VALUES(?,?,?,?,?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (usuario.getId() == null) {
                usuario.setId(UUID.randomUUID().toString());
            }

            pstmt.setString(1, usuario.getId());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getPassword());
            pstmt.setInt(5, usuario.isEsAdmin() ? 1 : 0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario login(String email, String password) {
        Usuario u = buscarPorEmail(email);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM Usuarios WHERE email = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getString("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setEsAdmin(rs.getInt("esAdmin") == 1);
                    usuario.setPreferencias(rs.getString("preferencias"));

                    calcularYActualizarPreferencia(usuario);
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void calcularYActualizarPreferencia(Usuario usuario) {
        String sql = "SELECT p.deporte, COUNT(p.deporte) as total_reservas " +
                "FROM Reservas r " +
                "JOIN Pistas p ON r.idPista = p.id " +
                "WHERE r.idUsuario = ? " +
                "GROUP BY p.deporte " +
                "ORDER BY total_reservas DESC LIMIT 1";

        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getId());

            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String deporteFavorito = rs.getString("deporte");
                    if (deporteFavorito != null && !deporteFavorito.equals(usuario.getPreferencias())) {
                        usuario.setPreferencias(deporteFavorito);
                        guardarPreferenciaBD(usuario.getId(), deporteFavorito);
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private void guardarPreferenciaBD(String idUsuario, String deporteFavorito) {
        String sql = "UPDATE Usuarios SET preferencias = ? WHERE id = ?";
        try (java.sql.Connection conn = utilidades.ConexionBD.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, deporteFavorito);
            pstmt.setString(2, idUsuario);
            pstmt.executeUpdate();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}