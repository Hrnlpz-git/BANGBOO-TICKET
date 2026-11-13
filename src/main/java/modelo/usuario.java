
package modelo;

public class usuario {

    private int id_usuario;
    private String nombre_usuario;
    private String contrasena_hash;
    private String nombre_completo;
    private String rol;

    public usuario() {
    }

    public usuario(int id_usuario, String nombre_usuario,
                   String contrasena_hash,
                   String nombre_completo, String rol) {

        this.id_usuario = id_usuario;
        this.nombre_usuario = nombre_usuario;
        this.contrasena_hash = contrasena_hash;
        this.nombre_completo = nombre_completo;
        this.rol = rol;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getContrasena_hash() {
        return contrasena_hash;
    }

    public void setContrasena_hash(String contrasena_hash) {
        this.contrasena_hash = contrasena_hash;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
