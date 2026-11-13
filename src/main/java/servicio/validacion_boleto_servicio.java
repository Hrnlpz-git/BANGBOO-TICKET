
package servicio;

import conexion.conexionbd;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import modelo.usuario;
import seguridad.control_acceso;

public class validacion_boleto_servicio {

    public String validar_boleto(
            sesion_usuario sesion, UUID codigo)
            throws SQLException, IOException {

        if (sesion == null || !sesion.hay_sesion_activa()) {
            throw new SecurityException(
                    "Debes iniciar sesion para validar boletos.");
        }

        usuario actual = sesion.obtener_usuario_actual();
        control_acceso acceso = new control_acceso(actual);

        if (!acceso.puede_vender_boletos()) {
            throw new SecurityException(
                    "No tienes permiso para validar boletos.");
        }

        if (codigo == null) {
            throw new IllegalArgumentException(
                    "El codigo del boleto es obligatorio.");
        }

        String sql = "UPDATE boletos "
                + "SET estado = 'utilizado' "
                + "WHERE codigo_unico = ? "
                + "AND estado = 'valido' "
                + "RETURNING id_boleto";

        try (Connection conexion = conexionbd.conectar()) {

            conexion.setAutoCommit(false);

            try {
                try (PreparedStatement consulta =
                        conexion.prepareStatement(sql)) {

                    consulta.setObject(1, codigo);

                    try (ResultSet resultado =
                            consulta.executeQuery()) {

                        if (resultado.next()) {
                            long id_boleto =
                                    resultado.getLong("id_boleto");

                            conexion.commit();

                            return "BOLETO VALIDADO CORRECTAMENTE. "
                                    + "ID: " + id_boleto;
                        }
                    }
                }

                conexion.rollback();

                String estado = consultar_estado(
                        conexion, codigo);

                if (estado == null) {
                    return "BOLETO NO ENCONTRADO.";
                }

                if ("utilizado".equals(estado)) {
                    return "BOLETO RECHAZADO: YA FUE UTILIZADO.";
                }

                if ("anulado".equals(estado)) {
                    return "BOLETO RECHAZADO: ESTA ANULADO.";
                }

                return "NO SE PUDO VALIDAR EL BOLETO.";

            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    private String consultar_estado(
            Connection conexion, UUID codigo)
            throws SQLException {

        String sql = "SELECT estado FROM boletos "
                + "WHERE codigo_unico = ?";

        try (PreparedStatement consulta =
                conexion.prepareStatement(sql)) {

            consulta.setObject(1, codigo);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getString("estado");
                }

                return null;
            }
        }
    }
}
