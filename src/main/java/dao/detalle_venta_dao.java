
package dao;

import conexion.conexionbd;
import modelo.detalle_venta;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class detalle_venta_dao {

    public long registrar_detalle(detalle_venta nuevo)
            throws SQLException, IOException {

        if (nuevo == null) {
            throw new IllegalArgumentException(
                    "El detalle de la venta es obligatorio.");
        }

        if (nuevo.getId_venta() <= 0
                || nuevo.getId_inventario() <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores deben ser validos.");
        }

        if (nuevo.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        if (nuevo.getPrecio_unitario() == null
                || nuevo.getPrecio_unitario().compareTo(
                        BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio unitario no puede ser negativo.");
        }

        if (nuevo.getSubtotal() == null
                || nuevo.getSubtotal().compareTo(
                        BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El subtotal no puede ser negativo.");
        }

        BigDecimal subtotal_calculado =
                nuevo.getPrecio_unitario().multiply(
                        BigDecimal.valueOf(
                                nuevo.getCantidad()));

        if (subtotal_calculado.compareTo(
                nuevo.getSubtotal()) != 0) {
            throw new IllegalArgumentException(
                    "El subtotal no coincide con la cantidad "
                    + "y el precio unitario.");
        }

        String sql = "INSERT INTO detalle_ventas "
                + "(id_venta, id_inventario, cantidad, "
                + "precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "RETURNING id_detalle";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setLong(1, nuevo.getId_venta());
            consulta.setInt(2, nuevo.getId_inventario());
            consulta.setInt(3, nuevo.getCantidad());

            consulta.setBigDecimal(
                    4, nuevo.getPrecio_unitario());

            consulta.setBigDecimal(
                    5, nuevo.getSubtotal());

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getLong("id_detalle");
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID del detalle.");
        
         }
    
    public long registrar_detalle(
            Connection conexion, detalle_venta nuevo)
            throws SQLException {

        if (nuevo == null) {
            throw new IllegalArgumentException(
                    "El detalle de la venta es obligatorio.");
        }

        if (nuevo.getId_venta() <= 0
                || nuevo.getId_inventario() <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores deben ser validos.");
        }

        if (nuevo.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        if (nuevo.getPrecio_unitario() == null
                || nuevo.getPrecio_unitario().compareTo(
                        BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio unitario no puede ser negativo.");
        }

        BigDecimal subtotal_calculado =
                nuevo.getPrecio_unitario().multiply(
                        BigDecimal.valueOf(
                                nuevo.getCantidad()));

        if (nuevo.getSubtotal() == null
                || subtotal_calculado.compareTo(
                        nuevo.getSubtotal()) != 0) {
            throw new IllegalArgumentException(
                    "El subtotal no coincide con la cantidad "
                    + "y el precio unitario.");
        }

        String sql = "INSERT INTO detalle_ventas "
                + "(id_venta, id_inventario, cantidad, "
                + "precio_unitario, subtotal) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "RETURNING id_detalle";

        try (PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setLong(1, nuevo.getId_venta());
            consulta.setInt(2, nuevo.getId_inventario());
            consulta.setInt(3, nuevo.getCantidad());

            consulta.setBigDecimal(
                    4, nuevo.getPrecio_unitario());

            consulta.setBigDecimal(
                    5, nuevo.getSubtotal());

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getLong("id_detalle");
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID del detalle.");
    }


    public List<detalle_venta> listar_detalles_por_venta(
            long id_venta)
            throws SQLException, IOException {

        List<detalle_venta> lista = new ArrayList<>();

        String sql = "SELECT id_detalle, id_venta, "
                + "id_inventario, cantidad, precio_unitario, "
                + "subtotal FROM detalle_ventas "
                + "WHERE id_venta = ? ORDER BY id_detalle";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setLong(1, id_venta);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                while (resultado.next()) {

                    detalle_venta actual =
                            new detalle_venta();

                    actual.setId_detalle(
                            resultado.getLong("id_detalle"));

                    actual.setId_venta(
                            resultado.getLong("id_venta"));

                    actual.setId_inventario(
                            resultado.getInt("id_inventario"));

                    actual.setCantidad(
                            resultado.getInt("cantidad"));

                    actual.setPrecio_unitario(
                            resultado.getBigDecimal(
                                    "precio_unitario"));

                    actual.setSubtotal(
                            resultado.getBigDecimal("subtotal"));

                    lista.add(actual);
                }
            }
        }

        return lista;
    }
    

    public int contar_boletos_vendidos(int id_inventario)
            throws SQLException, IOException {

        if (id_inventario <= 0) {
            throw new IllegalArgumentException(
                    "El ID del inventario debe ser mayor que cero.");
        }

        String sql = "SELECT COALESCE(SUM(d.cantidad), 0) "
                + "AS total_vendidos "
                + "FROM detalle_ventas d "
                + "INNER JOIN ventas v "
                + "ON d.id_venta = v.id_venta "
                + "WHERE d.id_inventario = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, id_inventario);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getInt("total_vendidos");
                }
            }
        }

        return 0;
    }

}
