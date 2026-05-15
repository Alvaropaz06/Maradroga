package entidades;

public class Reserva {
    private String id;
    private String idUsuario;
    private String idPista;
    private String fechaHoraInicio;
    private double precioTotal;
    private String estado;
    private boolean pistaDisponible;
    private String deportePista;

    // NUEVO CAMPO: Guardará los materiales (ej: "idMaterial1,idMaterial2")
    private String materialesAlquilados;

    public Reserva() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getIdPista() { return idPista; }
    public void setIdPista(String idPista) { this.idPista = idPista; }
    public String getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(String fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
    public double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isPistaDisponible() { return pistaDisponible; }
    public void setPistaDisponible(boolean pistaDisponible) { this.pistaDisponible = pistaDisponible; }
    public String getDeportePista() { return deportePista; }
    public void setDeportePista(String deportePista) { this.deportePista = deportePista; }

    public String getMaterialesAlquilados() { return materialesAlquilados; }
    public void setMaterialesAlquilados(String materialesAlquilados) { this.materialesAlquilados = materialesAlquilados; }
}