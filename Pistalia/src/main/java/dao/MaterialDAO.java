package dao;

import entidades.Material;
import java.util.List;

public interface MaterialDAO {
    List<Material> obtenerPorDeporte(String deporte);
    Material buscarPorId(String id);
    boolean reservarMaterial(String idMaterial);
    boolean actualizarStock(String idMaterial, int nuevaCantidad);

    // Métodos de Administrador
    List<Material> obtenerTodos();
    boolean anadirMaterial(Material material);
    boolean eliminarMaterial(String idMaterial);
}