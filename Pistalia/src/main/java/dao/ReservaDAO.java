package dao;

import entidades.Reserva;
import java.util.List;

public interface ReservaDAO {
    boolean guardar(Reserva reserva);
    List<Reserva> obtenerPorUsuario(String idUsuario);
    boolean eliminar(String idReserva);
}