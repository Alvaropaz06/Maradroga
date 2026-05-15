package servicios;

import dao.PistaDAO;
import entidades.Pista;
import entidades.Usuario;

public class AdminService {
    private PistaDAO pistaDAO;

    public AdminService(PistaDAO pistaDAO) {
        this.pistaDAO = pistaDAO;
    }

    public void guardarPista(Pista pista, Usuario admin) throws Exception {
        if (!admin.isEsAdmin()) throw new Exception("Acceso denegado");

        // ACTUALIZADO: Usamos el nombre del método nuevo (anadirPista)
        pistaDAO.anadirPista(pista);
    }

    public void borrarPista(String idPista, Usuario admin, boolean confirmacionExtra) throws Exception {
        if (!admin.isEsAdmin()) throw new Exception("Acceso denegado");

        // ACTUALIZADO: Usamos el nombre del método nuevo (eliminarPista)
        // La comprobación de reservas activas ya la hacemos visualmente en el panel
        pistaDAO.eliminarPista(idPista);
    }
}