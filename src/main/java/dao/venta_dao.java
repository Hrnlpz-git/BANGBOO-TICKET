
package dao;

import conexion.conexionbd;
import modelo.venta;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class venta_dao {

    public long registrar_venta(venta nueva)
            throws SQLException, IOException {

        if (nueva == null) {
            throw new IllegalArgumentException(
                    "La venta es obligatoria.");
        }

        if (nueva.getId_vendedor() <= 0) {
            throw new IllegalArgumentException(
                    "El vendedor no es valido.");
        }

        if (nueva.getTotal_venta() == null
                || nueva.getTotal_venta().compareTo(
                        BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El total no puede ser negativo.");
        }

        String sql = "INSERT INTO ventas "
                + "(id_vendedor, total_venta) "
                + "VALUES (?, ?) "
                + "RETURNING id_venta";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(
                    1, nueva.getId_vendedor());

            consulta.setBigDecimal(
                    2, nueva.getTotal_venta());

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getLong("id_venta");
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID de la venta.");
    }

    public long registrar_venta(
            Connection conexion, venta nueva)
            throws SQLException {

        if (nueva == null) {
            throw new IllegalArgumentException(
                    "La venta es obligatoria.");
        }

        if (nueva.getId_vendedor() <= 0) {
            throw new IllegalArgumentException(
                    "El vendedor no es valido.");
        }

        if (nueva.getTotal_venta() == null
                || nueva.getTotal_venta().compareTo(
                        BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El total no puede ser negativo.");
        }

        String sql = "INSERT INTO ventas "
                + "(id_vendedor, total_venta) "
                + "VALUES (?, ?) "
                + "RETURNING id_venta";

        try (PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(
                    1, nueva.getId_vendedor());

            consulta.setBigDecimal(
                    2, nueva.getTotal_venta());

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getLong("id_venta");
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID de la venta.");
    }
    

public java.util.List<String[]> consultar_ventas(
        int id_vendedor,
        boolean consultar_todas)
        throws SQLException, IOException {

    java.util.List<String[]> ventas =
            new java.util.ArrayList<>();

    String sql =
            "SELECT v.id_venta, "
            + "v.fecha_venta, "
            + "u.nombre_completo, "
            + "COUNT(b.id_boleto) AS cantidad_boletos, "
            + "v.total_venta "
            + "FROM ventas v "
            + "INNER JOIN usuarios u "
            + "ON v.id_vendedor = u.id_usuario "
            + "LEFT JOIN detalle_ventas d "
            + "ON v.id_venta = d.id_venta "
            + "LEFT JOIN boletos b "
            + "ON d.id_detalle = b.id_detalle ";

    if (!consultar_todas) {
        sql += "WHERE v.id_vendedor = ? ";
    }

    sql +=
            "GROUP BY v.id_venta, v.fecha_venta, "
            + "u.nombre_completo, v.total_venta "
            + "ORDER BY v.fecha_venta DESC";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql)) {

        if (!consultar_todas) {
            consulta.setInt(1, id_vendedor);
        }

        try (ResultSet resultado =
                consulta.executeQuery()) {

            while (resultado.next()) {
                String[] fila = new String[5];

                fila[0] = String.valueOf(
                        resultado.getLong("id_venta"));

                fila[1] = resultado.getTimestamp("fecha_venta")
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern(
                "dd/MM/yyyy     HH:mm:ss"));

                fila[2] = resultado.getString(
                        "nombre_completo");

                fila[3] = String.valueOf(
                        resultado.getLong("cantidad_boletos"));

                fila[4] = "Q"
                        + resultado.getBigDecimal(
                                "total_venta").toPlainString();

                ventas.add(fila);
            }
        }
    }

    return ventas;
}

public java.util.List<String[]> consultar_ventas(
        int id_vendedor,
        boolean consultar_todas,
        LocalDate fecha_inicial,
        LocalDate fecha_final)
        throws SQLException, IOException {

    if (fecha_inicial == null || fecha_final == null) {
        throw new IllegalArgumentException(
                "Debes seleccionar ambas fechas.");
    }

    if (fecha_inicial.isAfter(fecha_final)) {
        throw new IllegalArgumentException(
                "La fecha inicial no puede ser posterior a la final.");
    }

    java.util.List<String[]> ventas =
            new java.util.ArrayList<>();

    String sql =
            "SELECT v.id_venta, "
            + "v.fecha_venta, "
            + "u.nombre_completo, "
            + "COUNT(b.id_boleto) AS cantidad_boletos, "
            + "v.total_venta "
            + "FROM ventas v "
            + "INNER JOIN usuarios u "
            + "ON v.id_vendedor = u.id_usuario "
            + "LEFT JOIN detalle_ventas d "
            + "ON v.id_venta = d.id_venta "
            + "LEFT JOIN boletos b "
            + "ON d.id_detalle = b.id_detalle "
            + "WHERE v.fecha_venta >= ? "
            + "AND v.fecha_venta < ? ";

    if (!consultar_todas) {
        sql += "AND v.id_vendedor = ? ";
    }

    sql +=
            "GROUP BY v.id_venta, v.fecha_venta, "
            + "u.nombre_completo, v.total_venta "
            + "ORDER BY v.fecha_venta DESC";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql)) {

        LocalDateTime inicio =
                fecha_inicial.atStartOfDay();

        LocalDateTime fin =
                fecha_final.plusDays(1).atStartOfDay();

        consulta.setTimestamp(
                1,
                java.sql.Timestamp.valueOf(inicio));

        consulta.setTimestamp(
                2,
                java.sql.Timestamp.valueOf(fin));

        if (!consultar_todas) {
            consulta.setInt(3, id_vendedor);
        }

        try (ResultSet resultado =
                consulta.executeQuery()) {

            while (resultado.next()) {

                String[] fila = new String[5];

                fila[0] = String.valueOf(
                        resultado.getLong("id_venta"));

                fila[1] = resultado.getTimestamp(
                        "fecha_venta")
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy     HH:mm:ss"));

                fila[2] = resultado.getString(
                        "nombre_completo");

                fila[3] = String.valueOf(
                        resultado.getLong("cantidad_boletos"));

                fila[4] = "Q"
                        + resultado.getBigDecimal(
                                "total_venta").toPlainString();

                ventas.add(fila);
            }
        }
    }

    return ventas;
}

}
