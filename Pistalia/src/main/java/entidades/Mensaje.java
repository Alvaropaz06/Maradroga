package entidades;

public class Mensaje {
    private String id;
    private String idUsuario;
    private String nombreUsuario;
    private String textoMensaje;
    private String respuesta;
    private String fecha;

    public Mensaje() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getTextoMensaje() { return textoMensaje; }
    public void setTextoMensaje(String textoMensaje) { this.textoMensaje = textoMensaje; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}