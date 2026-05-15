package entidades;

public class Pista {
    private String id;
    private String deporte;
    private boolean disponible;
    private double precio; // <--- NUEVO

    public Pista() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDeporte() { return deporte; }
    public void setDeporte(String deporte) { this.deporte = deporte; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}