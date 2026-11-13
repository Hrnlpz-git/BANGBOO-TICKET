
package dao;

import conexion.conexionbd;
import modelo.boleto;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class boleto_dao {

    public long registrar_boleto(boleto nuevo)
            throws SQLException, IOException {

        if (nuevo == null) {
            throw new IllegalArgumentException(
                    "El boleto es obligatorio.");
        }

        if (nuevo.getId_detalle() <= 0) {
            throw new IllegalArgumentException(
                    "El ID del detalle no es valido.");
        }

        UUID codigo = nuevo.getCodigo_unico();

        if (codigo == null) {
            codigo = UUID.randomUUID();
        }

        String estado = nuevo.getEstado();

        if (estado == null || estado.trim().isEmpty()) {
            estado = "valido";
        }

        if (!estado.equals("valido")
                && !estado.equals("utilizado")
                && !estado.equals("anulado")) {
            throw new IllegalArgumentException(
                    "El estado del boleto no es valido.");
        }

        String sql = "INSERT INTO boletos "
                + "(id_detalle, codigo_unico, estado) "
                + "VALUES (?, ?, ?) "
                + "RETURNING id_boleto, fecha_creacion";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setLong(1, nuevo.getId_detalle());
            consulta.setObject(2, codigo);
            consulta.setString(3, estado);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {

                    long id = resultado.getLong("id_boleto");

                    nuevo.setId_boleto(id);
                    nuevo.setCodigo_unico(codigo);
                    nuevo.setEstado(estado);
                    nuevo.setFecha_creacion(
                            resultado.getTimestamp(
                                    "fecha_creacion"));

                    return id;
                }
            }
        }

        throw new SQLException(
                "No se pudo registrar el boleto.");
        
    }
    
    public long registrar_boleto(
            Connection conexion, boleto nuevo)
            throws SQLException {

        if (nuevo == null) {
            throw new IllegalArgumentException(
                    "El boleto es obligatorio.");
        }

        if (nuevo.getId_detalle() <= 0) {
            throw new IllegalArgumentException(
                    "El ID del detalle no es valido.");
        }

        UUID codigo = nuevo.getCodigo_unico();

        if (codigo == null) {
            codigo = UUID.randomUUID();
        }

        String estado = nuevo.getEstado();

        if (estado == null || estado.trim().isEmpty()) {
            estado = "valido";
        }

        if (!estado.equals("valido")
                && !estado.equals("utilizado")
                && !estado.equals("anulado")) {
            throw new IllegalArgumentException(
                    "El estado del boleto no es valido.");
        }

        String sql = "INSERT INTO boletos "
                + "(id_detalle, codigo_unico, estado) "
                + "VALUES (?, ?, ?) "
                + "RETURNING id_boleto, fecha_creacion";

        try (PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setLong(1, nuevo.getId_detalle());
            consulta.setObject(2, codigo);
            consulta.setString(3, estado);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {

                    long id = resultado.getLong("id_boleto");

                    nuevo.setId_boleto(id);
                    nuevo.setCodigo_unico(codigo);
                    nuevo.setEstado(estado);

                    nuevo.setFecha_creacion(
                            resultado.getTimestamp(
                                    "fecha_creacion"));

                    return id;
                }
            }
        }

        throw new SQLException(
                "No se pudo registrar el boleto.");
    }


    public List<boleto> listar_boletos_por_detalle(
            long id_detalle)
            throws SQLException, IOException {

        List<boleto> lista = new ArrayList<>();

        String sql = "SELECT id_boleto, id_detalle, "
                + "codigo_unico, estado, fecha_creacion "
                + "FROM boletos "
                + "WHERE id_detalle = ? "
                + "ORDER BY id_boleto";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setLong(1, id_detalle);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                while (resultado.next()) {

                    boleto actual = new boleto();

                    actual.setId_boleto(
                            resultado.getLong("id_boleto"));

                    actual.setId_detalle(
                            resultado.getLong("id_detalle"));

                    actual.setCodigo_unico(
                            resultado.getObject(
                                    "codigo_unico", UUID.class));

                    actual.setEstado(
                            resultado.getString("estado"));

                    actual.setFecha_creacion(
                            resultado.getTimestamp(
                                    "fecha_creacion"));

                    lista.add(actual);
                }
            }
        }

        return lista;
    }
    
    public boleto buscar_por_codigo(UUID codigo)
            throws SQLException, IOException {

        if (codigo == null) {
            throw new IllegalArgumentException(
                    "El codigo del boleto es obligatorio.");
        }

        String sql = "SELECT id_boleto, id_detalle, "
                + "codigo_unico, estado, fecha_creacion "
                + "FROM boletos "
                + "WHERE codigo_unico = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setObject(1, codigo);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {

                    boleto actual = new boleto();

                    actual.setId_boleto(
                            resultado.getLong("id_boleto"));

                    actual.setId_detalle(
                            resultado.getLong("id_detalle"));

                    actual.setCodigo_unico(
                            resultado.getObject(
                                    "codigo_unico", UUID.class));

                    actual.setEstado(
                            resultado.getString("estado"));

                    actual.setFecha_creacion(
                            resultado.getTimestamp(
                                    "fecha_creacion"));

                    return actual;
                }
            }
        }

        return null;
    }
    
    public boolean anular_boleto(UUID codigo)
            throws SQLException, IOException {

        if (codigo == null) {
            throw new IllegalArgumentException(
                    "El codigo del boleto es obligatorio.");
        }

        String sql = "UPDATE boletos "
                + "SET estado = 'anulado' "
                + "WHERE codigo_unico = ? "
                + "AND estado = 'valido'";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setObject(1, codigo);

            int filas_actualizadas = consulta.executeUpdate();

            return filas_actualizadas == 1;
        }
    }


}
