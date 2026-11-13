
package servicio;

import conexion.conexionbd;
import dao.venta_dao;
import dao.detalle_venta_dao;
import dao.inventario_boletos_dao;
import dao.boleto_dao;

import modelo.usuario;
import modelo.venta;
import modelo.detalle_venta;
import modelo.boleto;

import seguridad.control_acceso;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class venta_servicio {

    private final venta_dao venta_dao =
            new venta_dao();

    private final detalle_venta_dao detalle_dao =
            new detalle_venta_dao();

    private final inventario_boletos_dao inventario_dao =
            new inventario_boletos_dao();

    private final boleto_dao boleto_dao =
            new boleto_dao();

    public long registrar_compra(
            sesion_usuario sesion,
            int id_inventario,
            int cantidad)
            throws SQLException, IOException {

        // 1. Verificar que exista una sesion.
        if (sesion == null || !sesion.hay_sesion_activa()) {
            throw new SecurityException(
                    "Debes iniciar sesion para vender boletos.");
        }

        // 2. Comprobar el permiso de venta.
        usuario actual = sesion.obtener_usuario_actual();

        control_acceso acceso = new control_acceso(actual);

        if (!acceso.puede_vender_boletos()) {
            throw new SecurityException(
                    "No tienes permiso para vender boletos.");
        }

        // 3. Obtener el vendedor de la sesion autenticada.
        int id_vendedor = actual.getId_usuario();

        if (id_vendedor <= 0 || id_inventario <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores deben ser validos.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        try (Connection conexion = conexionbd.conectar()) {

            conexion.setAutoCommit(false);

            try {
                BigDecimal precio_unitario;
                String estado_concierto;

                String sql = "SELECT i.precio, c.estado "
                        + "FROM inventario_boletos i "
                        + "INNER JOIN conciertos c "
                        + "ON i.id_concierto = c.id_concierto "
                        + "WHERE i.id_inventario = ? "
                        + "FOR UPDATE OF i";

                try (PreparedStatement consulta =
                        conexion.prepareStatement(sql)) {

                    consulta.setInt(1, id_inventario);

                    try (ResultSet resultado =
                            consulta.executeQuery()) {

                        if (!resultado.next()) {
                            throw new SQLException(
                                    "El inventario o el concierto no existe.");
                        }

                        precio_unitario =
                                resultado.getBigDecimal("precio");

                        estado_concierto =
                                resultado.getString("estado");
                    }
                }

                if (!"programado".equals(estado_concierto)
                        && !"activo".equals(estado_concierto)) {

                    throw new SQLException(
                            "No se pueden vender boletos de un concierto "
                            + "que no este programado o activo.");
                }

                BigDecimal total = precio_unitario.multiply(
                        BigDecimal.valueOf(cantidad));

                venta nueva = new venta();
                nueva.setId_vendedor(id_vendedor);
                nueva.setTotal_venta(total);

                long id_venta = venta_dao.registrar_venta(
                        conexion, nueva);

                detalle_venta detalle =
                        new detalle_venta();

                detalle.setId_venta(id_venta);
                detalle.setId_inventario(id_inventario);
                detalle.setCantidad(cantidad);
                detalle.setPrecio_unitario(precio_unitario);
                detalle.setSubtotal(total);

                long id_detalle = detalle_dao.registrar_detalle(
                        conexion, detalle);

                inventario_dao.descontar_existencias(
                        conexion, id_inventario, cantidad);

                for (int i = 0; i < cantidad; i++) {
                    boleto nuevo_boleto = new boleto();

                    nuevo_boleto.setId_detalle(id_detalle);
                    nuevo_boleto.setEstado("valido");

                    boleto_dao.registrar_boleto(
                            conexion, nuevo_boleto);
                }

                conexion.commit();

                return id_venta;

            } catch (SQLException | RuntimeException e) {

                try {
                    conexion.rollback();
                } catch (SQLException error_rollback) {
                    e.addSuppressed(error_rollback);
                }

                throw e;
            }
        }
    }
}
