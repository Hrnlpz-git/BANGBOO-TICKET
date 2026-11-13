
package dao;

import conexion.conexionbd;
import modelo.localidad;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class localidad_dao {

    public void registrar_localidad(localidad nueva)
            throws SQLException, IOException {

        if (nueva == null
                || nueva.getNombre() == null
                || nueva.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El nombre de la localidad es obligatorio.");
        }

        String sql = "INSERT INTO localidades (nombre) "
                + "VALUES (?)";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(
                    1, nueva.getNombre().trim());

            consulta.executeUpdate();
        }
    }

    public List<localidad> listar_localidades()
            throws SQLException, IOException {

        List<localidad> lista = new ArrayList<>();

        String sql = "SELECT id_localidad, nombre "
                + "FROM localidades ORDER BY id_localidad";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                localidad actual = new localidad();

                actual.setId_localidad(
                        resultado.getInt("id_localidad"));

                actual.setNombre(
                        resultado.getString("nombre"));

                lista.add(actual);
            }
        }

        return lista;
    }
    
    
   public void eliminar_localidad(int id_localidad)
        throws SQLException, IOException {

    if (id_localidad <= 0) {
        throw new IllegalArgumentException(
                "Selecciona una localidad válida.");
    }

    String sql = "DELETE FROM localidades "
            + "WHERE id_localidad = ?";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql)) {

        consulta.setInt(1, id_localidad);

        int filas = consulta.executeUpdate();

        if (filas == 0) {
            throw new SQLException(
                    "No se encontró la localidad seleccionada.");
        }

    } catch (SQLException e) {

        // PostgreSQL: error por una llave foránea.
        if ("23503".equals(e.getSQLState())) {
            throw new SQLException(
                    "No se puede eliminar esta localidad porque "
                    + "está relacionada con inventarios o boletos.");
        }

        throw e;
    }
}
}
