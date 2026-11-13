
package modelo;

import java.sql.Timestamp;
import java.util.UUID;

public class boleto {

    private long id_boleto;
    private long id_detalle;
    private UUID codigo_unico;
    private String estado;
    private Timestamp fecha_creacion;

    public boleto() {
    }

    public boleto(long id_boleto, long id_detalle,
                  UUID codigo_unico, String estado,
                  Timestamp fecha_creacion) {

        this.id_boleto = id_boleto;
        this.id_detalle = id_detalle;
        this.codigo_unico = codigo_unico;
        this.estado = estado;
        this.fecha_creacion = fecha_creacion;
    }

    public long getId_boleto() {
        return id_boleto;
    }

    public void setId_boleto(long id_boleto) {
        this.id_boleto = id_boleto;
    }

    public long getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(long id_detalle) {
        this.id_detalle = id_detalle;
    }

    public UUID getCodigo_unico() {
        return codigo_unico;
    }

    public void setCodigo_unico(UUID codigo_unico) {
        this.codigo_unico = codigo_unico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(
            Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
}
