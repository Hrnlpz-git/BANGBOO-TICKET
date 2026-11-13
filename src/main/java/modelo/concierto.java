
package modelo;

import java.sql.Date;
import java.sql.Time;

public class concierto {

    private int id_concierto;
    private int id_artista;
    private String titulo_evento;
    private Date fecha_concierto;
    private Time hora_concierto;
    private String recinto;
    private String estado;
    private String ruta_cartel;
    private String ruta_video;

    public concierto() {
    }

    public concierto(int id_concierto, int id_artista,
                     String titulo_evento,
                     Date fecha_concierto,
                     Time hora_concierto,
                     String recinto, String estado,
                     String ruta_cartel, String ruta_video) {

        this.id_concierto = id_concierto;
        this.id_artista = id_artista;
        this.titulo_evento = titulo_evento;
        this.fecha_concierto = fecha_concierto;
        this.hora_concierto = hora_concierto;
        this.recinto = recinto;
        this.estado = estado;
        this.ruta_cartel = ruta_cartel;
        this.ruta_video = ruta_video;
    }

    public int getId_concierto() {
        return id_concierto;
    }

    public void setId_concierto(int id_concierto) {
        this.id_concierto = id_concierto;
    }

    public int getId_artista() {
        return id_artista;
    }

    public void setId_artista(int id_artista) {
        this.id_artista = id_artista;
    }

    public String getTitulo_evento() {
        return titulo_evento;
    }

    public void setTitulo_evento(String titulo_evento) {
        this.titulo_evento = titulo_evento;
    }

    public Date getFecha_concierto() {
        return fecha_concierto;
    }

    public void setFecha_concierto(Date fecha_concierto) {
        this.fecha_concierto = fecha_concierto;
    }

    public Time getHora_concierto() {
        return hora_concierto;
    }

    public void setHora_concierto(Time hora_concierto) {
        this.hora_concierto = hora_concierto;
    }

    public String getRecinto() {
        return recinto;
    }

    public void setRecinto(String recinto) {
        this.recinto = recinto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRuta_cartel() {
        return ruta_cartel;
    }

    public void setRuta_cartel(String ruta_cartel) {
        this.ruta_cartel = ruta_cartel;
    }

    public String getRuta_video() {
        return ruta_video;
    }

    public void setRuta_video(String ruta_video) {
        this.ruta_video = ruta_video;
    }
    
    public concierto(int id_concierto, int id_artista,
                 String titulo_evento,
                 Date fecha_concierto,
                 String recinto, String estado,
                 String ruta_cartel, String ruta_video) {

    this(id_concierto, id_artista, titulo_evento,
         fecha_concierto, null, recinto, estado,
         ruta_cartel, ruta_video);
}
}
