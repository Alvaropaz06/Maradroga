package servicios;

import dao.MaterialDAO;
import dao.PistaDAO;
import dao.ReservaDAO;
import entidades.Material;
import entidades.Reserva;
import entidades.Usuario;

public class ReservaService {
    private PistaDAO pistaDAO;
    private ReservaDAO reservaDAO;
    private MaterialDAO materialDAO;

    public ReservaService(PistaDAO pistaDAO, ReservaDAO reservaDAO, MaterialDAO materialDAO) {
        this.pistaDAO = pistaDAO;
        this.reservaDAO = reservaDAO;
        this.materialDAO = materialDAO;
    }

    // ARREGLADO: Ahora recibe la fecha como String para que coincida con tu BD y tus entidades
    public Reserva crearReserva(Usuario usuario, String idPista, String fechaHora) {
        Reserva reserva = new Reserva();
        reserva.setIdUsuario(usuario.getId());
        reserva.setIdPista(idPista);
        reserva.setFechaHoraInicio(fechaHora); // ¡Ya no choca!
        reserva.setEstado("Pendiente");

        reservaDAO.guardar(reserva);
        return reserva;
    }

    public void anadirMaterialAReserva(Reserva reserva, String idMaterial, int cantidad) throws Exception {
        Material material = materialDAO.buscarPorId(idMaterial);

        if (material == null) {
            throw new Exception("El material no existe.");
        }

        if (material.getCantidad() < cantidad) {
            throw new Exception("No hay stock disponible.");
        }

        // Actualizamos el stock en la base de datos
        materialDAO.actualizarStock(idMaterial, material.getCantidad() - cantidad);

        // Calculamos precio y guardamos
        reserva.setPrecioTotal(reserva.getPrecioTotal() + (material.getPrecio() * cantidad));
        reservaDAO.guardar(reserva);
    }

    public void cancelarReserva(String idReserva) throws Exception {
        // Como la cancelación ya la gestionamos directamente desde PanelMisReservas,
        // aquí simplemente llamamos a tu DAO actualizado.
        reservaDAO.eliminar(idReserva);
    }
}