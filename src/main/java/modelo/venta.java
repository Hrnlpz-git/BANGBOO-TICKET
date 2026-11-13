
package modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class venta {

    private long id_venta;
    private int id_vendedor;
    private Timestamp fecha_venta;
    private BigDecimal total_venta;

    public venta() {
    }

    public venta(long id_venta, int id_vendedor,
                 Timestamp fecha_venta,
                 BigDecimal total_venta) {

        this.id_venta = id_venta;
        this.id_vendedor = id_vendedor;
        this.fecha_venta = fecha_venta;
        this.total_venta = total_venta;
    }

    public long getId_venta() {
        return id_venta;
    }

    public void setId_venta(long id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(int id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public Timestamp getFecha_venta() {
        return fecha_venta;
    }

    public void setFecha_venta(Timestamp fecha_venta) {
        this.fecha_venta = fecha_venta;
    }

    public BigDecimal getTotal_venta() {
        return total_venta;
    }

    public void setTotal_venta(BigDecimal total_venta) {
        this.total_venta = total_venta;
    }
}
