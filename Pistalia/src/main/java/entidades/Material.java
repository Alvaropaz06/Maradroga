package entidades;

public class Material {
    private String id;
    private String nombre;
    private String deporte; // ¡AQUÍ ESTÁ LA VARIABLE QUE FALTABA!
    private boolean stockDisponible;
    private double precio;
    private int cantidad;

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDeporte() { return deporte; }
    public void setDeporte(String deporte) { this.deporte = deporte; } // ¡ESTE ES EL MÉTODO QUE PEDÍA EL ERROR!

    public boolean isStockDisponible() { return stockDisponible; }
    public void setStockDisponible(boolean stockDisponible) { this.stockDisponible = stockDisponible; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}