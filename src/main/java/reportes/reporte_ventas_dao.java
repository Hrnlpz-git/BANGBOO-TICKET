
package reportes;

import conexion.conexionbd;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class reporte_ventas_dao {

    public BigDecimal obtener_ingresos_totales()
            throws SQLException, IOException {

String sql = "SELECT COALESCE(SUM(total_venta), 0) AS ingresos_totales FROM ventas";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {
                return resultado.getBigDecimal("ingresos_totales");
            }
        }

        return BigDecimal.ZERO;
    }
    
    public List<Object[]> obtener_boletos_por_concierto()
        throws SQLException, IOException {

    List<Object[]> lista = new ArrayList<>();

    String sql =
            "SELECT c.titulo_evento, "
            + "COALESCE(SUM(d.cantidad), 0) AS vendidos "
            + "FROM conciertos c "
            + "LEFT JOIN inventario_boletos i "
            + "ON c.id_concierto = i.id_concierto "
            + "LEFT JOIN detalle_ventas d "
            + "ON i.id_inventario = d.id_inventario "
            + "GROUP BY c.id_concierto, c.titulo_evento "
            + "ORDER BY vendidos DESC";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql);
         ResultSet resultado = consulta.executeQuery()) {

        while (resultado.next()) {

            Object[] fila = new Object[2];

            fila[0] = resultado.getString("titulo_evento");
            fila[1] = resultado.getInt("vendidos");

            lista.add(fila);
        }
    }

    return lista;
}

    public int obtener_cantidad_ventas()
            throws SQLException, IOException {

        String sql = "SELECT COUNT(*) AS cantidad "
                + "FROM ventas";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {
                return resultado.getInt("cantidad");
            }
        }

        return 0;
    }

    public int obtener_boletos_vendidos()
            throws SQLException, IOException {

        String sql = "SELECT COALESCE(SUM(cantidad), 0) "
                + "AS boletos_vendidos "
                + "FROM detalle_ventas";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (resultado.next()) {
                return resultado.getInt("boletos_vendidos");
            }
        }

        return 0;
    }
}
