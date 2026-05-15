package servicios;

import dao.UsuarioDAO;
import entidades.Usuario;

public class UsuarioService {
    private UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public void registrarUsuario(String nombre, String email, String password) throws Exception {
        if (nombre == null || email == null || password == null) {
            throw new Exception("Error: Todos los campos son obligatorios.");
        }
        if (usuarioDAO.buscarPorEmail(email) != null) {
            throw new Exception("Error: El email ya está registrado.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(password);

        usuarioDAO.guardar(nuevoUsuario);
        System.out.println("Mensaje de bienvenida enviado al usuario.");
    }

    public Usuario iniciarSesion(String email, String password) throws Exception {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario != null && usuario.getPassword().equals(password)) {
            return usuario;
        } else {
            throw new Exception("Credenciales incorrectas. Se ha enviado correo de restablecimiento.");
        }
    }
}