package entidades;

public class Usuario {
    private String id;
    private String nombre;
    private String email;
    private String password;
    private boolean esAdmin;
    private String preferencias; // NUEVA COLUMNA

    // Constructores
    public Usuario() {
    }

    public Usuario(String id, String nombre, String email, String password, boolean esAdmin, String preferencias) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.esAdmin = esAdmin;
        this.preferencias = preferencias;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEsAdmin() { return esAdmin; }
    public void setEsAdmin(boolean esAdmin) { this.esAdmin = esAdmin; }

    public String getPreferencias() { return preferencias; }
    public void setPreferencias(String preferencias) { this.preferencias = preferencias; }
}