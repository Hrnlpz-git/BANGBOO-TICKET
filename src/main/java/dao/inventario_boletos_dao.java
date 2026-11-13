
package dao;

import conexion.conexionbd;
import modelo.inventario_boletos;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class inventario_boletos_dao {

    public void registrar_inventario(
            inventario_boletos nuevo)
            throws SQLException, IOException {

        if (nuevo == null) {
            throw new IllegalArgumentException(
                    "El inventario es obligatorio.");
        }

        if (nuevo.getPrecio() == null
                || nuevo.getPrecio().compareTo(
                        BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El precio no puede ser negativo.");
        }

        if (nuevo.getCantidad_total() < 0) {
            throw new IllegalArgumentException(
                    "La cantidad total no puede ser negativa.");
        }

        if (nuevo.getCantidad_disponible() < 0
                || nuevo.getCantidad_disponible()
                > nuevo.getCantidad_total()) {

            throw new IllegalArgumentException(
                    "La cantidad disponible debe estar "
                    + "entre cero y la cantidad total.");
        }

        String sql = "INSERT INTO inventario_boletos "
                + "(id_concierto, id_localidad, precio, "
                + "cantidad_total, cantidad_disponible) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(
                    1, nuevo.getId_concierto());

            consulta.setInt(
                    2, nuevo.getId_localidad());

            consulta.setBigDecimal(
                    3, nuevo.getPrecio());

            consulta.setInt(
                    4, nuevo.getCantidad_total());

            consulta.setInt(
                    5, nuevo.getCantidad_disponible());

            consulta.executeUpdate();
        }
    }

    public List<inventario_boletos> listar_inventario()
            throws SQLException, IOException {

        List<inventario_boletos> lista = new ArrayList<>();

        String sql = "SELECT id_inventario, id_concierto, "
                + "id_localidad, precio, cantidad_total, "
                + "cantidad_disponible "
                + "FROM inventario_boletos "
                + "ORDER BY id_inventario";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                inventario_boletos actual =
                        new inventario_boletos();

                actual.setId_inventario(
                        resultado.getInt("id_inventario"));

                actual.setId_concierto(
                        resultado.getInt("id_concierto"));

                actual.setId_localidad(
                        resultado.getInt("id_localidad"));

                actual.setPrecio(
                        resultado.getBigDecimal("precio"));

                actual.setCantidad_total(
                        resultado.getInt("cantidad_total"));

                actual.setCantidad_disponible(
                        resultado.getInt("cantidad_disponible"));

                lista.add(actual);
            }
        }

        return lista;
    }

    public void descontar_existencias(
            Connection conexion,
            int id_inventario,
            int cantidad)
            throws SQLException {

        if (id_inventario <= 0) {
            throw new IllegalArgumentException(
                    "El ID del inventario no es valido.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        String sql = "UPDATE inventario_boletos "
                + "SET cantidad_disponible = "
                + "cantidad_disponible - ? "
                + "WHERE id_inventario = ? "
                + "AND cantidad_disponible >= ?";

        try (PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, cantidad);
            consulta.setInt(2, id_inventario);
            consulta.setInt(3, cantidad);

            int filas_actualizadas =
                    consulta.executeUpdate();

            if (filas_actualizadas == 0) {
                throw new SQLException(
                        "No hay suficientes boletos disponibles "
                        + "o el inventario no existe.");
               

            }
        }
    }
    

    public inventario_boletos buscar_por_id(int id_inventario)
            throws SQLException, IOException {

        if (id_inventario <= 0) {
            throw new IllegalArgumentException(
                    "El ID del inventario debe ser mayor que cero.");
        }

        String sql = "SELECT id_inventario, id_concierto, "
                + "id_localidad, precio, cantidad_total, "
                + "cantidad_disponible "
                + "FROM inventario_boletos "
                + "WHERE id_inventario = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, id_inventario);

            try (ResultSet resultado = consulta.executeQuery()) {

                if (resultado.next()) {

                    inventario_boletos actual =
                            new inventario_boletos();

                    actual.setId_inventario(
                            resultado.getInt("id_inventario"));

                    actual.setId_concierto(
                            resultado.getInt("id_concierto"));

                    actual.setId_localidad(
                            resultado.getInt("id_localidad"));

                    actual.setPrecio(
                            resultado.getBigDecimal("precio"));

                    actual.setCantidad_total(
                            resultado.getInt("cantidad_total"));

                    actual.setCantidad_disponible(
                            resultado.getInt("cantidad_disponible"));

                    return actual;
                }
            }
        }

        return null;
    }
    
    public void actualizar_inventario(inventario_boletos actualizado)
        throws SQLException, IOException {

    if (actualizado == null) {
        throw new IllegalArgumentException(
                "El inventario es obligatorio.");
    }

    if (actualizado.getId_inventario() <= 0
            || actualizado.getId_concierto() <= 0
            || actualizado.getId_localidad() <= 0) {

        throw new IllegalArgumentException(
                "Los identificadores del inventario no son válidos.");
    }

    if (actualizado.getPrecio() == null
            || actualizado.getPrecio().compareTo(
                    BigDecimal.ZERO) < 0) {

        throw new IllegalArgumentException(
                "El precio no puede ser negativo.");
    }

    if (actualizado.getCantidad_total() < 0) {
        throw new IllegalArgumentException(
                "La cantidad total no puede ser negativa.");
    }

    String sql_buscar =
            "SELECT cantidad_total, cantidad_disponible "
            + "FROM inventario_boletos "
            + "WHERE id_inventario = ? "
            + "FOR UPDATE";

    String sql_actualizar =
            "UPDATE inventario_boletos "
            + "SET id_concierto = ?, "
            + "id_localidad = ?, "
            + "precio = ?, "
            + "cantidad_total = ?, "
            + "cantidad_disponible = ? "
            + "WHERE id_inventario = ?";

    try (Connection conexion = conexionbd.conectar()) {

        conexion.setAutoCommit(false);

        try {

            int cantidad_vendida;

            // Consultar y bloquear el inventario.
            try (PreparedStatement consulta =
                         conexion.prepareStatement(sql_buscar)) {

                consulta.setInt(
                        1, actualizado.getId_inventario());

                try (ResultSet resultado =
                             consulta.executeQuery()) {

                    if (!resultado.next()) {
                        throw new SQLException(
                                "El inventario seleccionado no existe.");
                    }

                    int cantidad_total_actual =
                            resultado.getInt("cantidad_total");

                    int cantidad_disponible_actual =
                            resultado.getInt("cantidad_disponible");

                    cantidad_vendida =
                            cantidad_total_actual
                            - cantidad_disponible_actual;
                }
            }

            // Evitar que la cantidad total sea menor
            // que los boletos que ya fueron vendidos.
            if (actualizado.getCantidad_total()
                    < cantidad_vendida) {

                throw new IllegalArgumentException(
                        "No puedes establecer una cantidad total "
                        + "menor a los boletos que ya fueron vendidos.\n"
                        + "Boletos vendidos: " + cantidad_vendida);
            }

            int nueva_cantidad_disponible =
                    actualizado.getCantidad_total()
                    - cantidad_vendida;

            // Actualizar los datos.
            try (PreparedStatement consulta =
                         conexion.prepareStatement(sql_actualizar)) {

                consulta.setInt(
                        1, actualizado.getId_concierto());

                consulta.setInt(
                        2, actualizado.getId_localidad());

                consulta.setBigDecimal(
                        3, actualizado.getPrecio());

                consulta.setInt(
                        4, actualizado.getCantidad_total());

                consulta.setInt(
                        5, nueva_cantidad_disponible);

                consulta.setInt(
                        6, actualizado.getId_inventario());

                consulta.executeUpdate();
            }

            conexion.commit();

        } catch (SQLException | RuntimeException error) {

            try {
                conexion.rollback();
            } catch (SQLException error_reversion) {
                error.addSuppressed(error_reversion);
            }

            throw error;
        }

    }
}

public void eliminar_inventario(int id_inventario)
        throws SQLException, IOException {

    if (id_inventario <= 0) {
        throw new IllegalArgumentException(
                "El ID del inventario no es válido.");
    }

    String sql = "DELETE FROM inventario_boletos "
            + "WHERE id_inventario = ?";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql)) {

        consulta.setInt(1, id_inventario);

        int filas_eliminadas = consulta.executeUpdate();

        if (filas_eliminadas == 0) {
            throw new SQLException(
                    "El inventario seleccionado ya no existe.");
        }

    } catch (SQLException error) {

        if ("23503".equals(error.getSQLState())) {
            throw new SQLException(
                    "No se puede eliminar este inventario porque "
                    + "tiene boletos o ventas relacionados.\n"
                    + "Se debe conservar el historial de ventas.");
        }

        throw error;
    }
}
}
