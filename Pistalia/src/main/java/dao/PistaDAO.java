package dao;

import entidades.Pista;
import java.util.List;

public interface PistaDAO {
    List<Pista> obtenerTodas();
    List<Pista> obtenerPorDeporte(String deporte);
    // Nuevos métodos para el administrador
    boolean actualizarDisponibilidad(String idPista, boolean disponible);
    boolean anadirPista(Pista pista);
    boolean eliminarPista(String idPista);
}