
package modelo;

import java.math.BigDecimal;

public class detalle_venta {

    private long id_detalle;
    private long id_venta;
    private int id_inventario;
    private int cantidad;
    private BigDecimal precio_unitario;
    private BigDecimal subtotal;

    public detalle_venta() {
    }

    public detalle_venta(
            long id_detalle,
            long id_venta,
            int id_inventario,
            int cantidad,
            BigDecimal precio_unitario,
            BigDecimal subtotal) {

        this.id_detalle = id_detalle;
        this.id_venta = id_venta;
        this.id_inventario = id_inventario;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
        this.subtotal = subtotal;
    }

    public long getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(long id_detalle) {
        this.id_detalle = id_detalle;
    }

    public long getId_venta() {
        return id_venta;
    }

    public void setId_venta(long id_venta) {
        this.id_venta = id_venta;
    }

    public int getId_inventario() {
        return id_inventario;
    }

    public void setId_inventario(int id_inventario) {
        this.id_inventario = id_inventario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(
            BigDecimal precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
