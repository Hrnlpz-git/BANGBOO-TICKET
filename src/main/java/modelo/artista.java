
package modelo;

public class artista {

    private int id_artista;
    private String nombre_artistico;
    private String genero_musical;
    private String pais_origen;
    private String ruta_imagen;
    private String ruta_video;

    public artista() {
    }

    public artista(int id_artista, String nombre_artistico,
                   String genero_musical, String pais_origen,
                   String ruta_imagen) {

        this.id_artista = id_artista;
        this.nombre_artistico = nombre_artistico;
        this.genero_musical = genero_musical;
        this.pais_origen = pais_origen;
        this.ruta_imagen = ruta_imagen;
    }

    public int getId_artista() {
        return id_artista;
    }

    public void setId_artista(int id_artista) {
        this.id_artista = id_artista;
    }

    public String getNombre_artistico() {
        return nombre_artistico;
    }

    public void setNombre_artistico(String nombre_artistico) {
        this.nombre_artistico = nombre_artistico;
    }

    public String getGenero_musical() {
        return genero_musical;
    }

    public void setGenero_musical(String genero_musical) {
        this.genero_musical = genero_musical;
    }

    public String getPais_origen() {
        return pais_origen;
    }

    public void setPais_origen(String pais_origen) {
        this.pais_origen = pais_origen;
    }

    public String getRuta_imagen() {
        return ruta_imagen;
    }

    public void setRuta_imagen(String ruta_imagen) {
        this.ruta_imagen = ruta_imagen;
    }
    
public String getRuta_video() {
    return ruta_video;
}

public void setRuta_video(String ruta_video) {
    this.ruta_video = ruta_video;
}
}
