package dao;

import entidades.Usuario;

public interface UsuarioDAO {
    Usuario buscarPorEmail(String email);
    void guardar(Usuario usuario);

    // Añadimos estos dos para que el Login funcione directo
    Usuario login(String email, String password);
    boolean registrar(Usuario usuario);
}