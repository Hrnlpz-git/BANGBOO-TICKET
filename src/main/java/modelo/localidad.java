
package modelo;

public class localidad {

    private int id_localidad;
    private String nombre;

    public localidad() {
    }

    public localidad(int id_localidad, String nombre) {
        this.id_localidad = id_localidad;
        this.nombre = nombre;
    }

    public int getId_localidad() {
        return id_localidad;
    }

    public void setId_localidad(int id_localidad) {
        this.id_localidad = id_localidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
