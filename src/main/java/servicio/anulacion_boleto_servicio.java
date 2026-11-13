
package servicio;

import dao.boleto_dao;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import modelo.usuario;
import seguridad.control_acceso;

public class anulacion_boleto_servicio {

    private final boleto_dao dao_boleto;

    public anulacion_boleto_servicio() {
        dao_boleto = new boleto_dao();
    }

    public boolean anular_boleto(
            sesion_usuario sesion, UUID codigo)
            throws SQLException, IOException {

        if (sesion == null || !sesion.hay_sesion_activa()) {
            throw new SecurityException(
                    "Debes iniciar sesion para anular boletos.");
        }

        usuario actual = sesion.obtener_usuario_actual();
        control_acceso acceso = new control_acceso(actual);

        if (!acceso.puede_gestionar_conciertos()) {
            throw new SecurityException(
                    "Solo un administrador puede anular boletos.");
        }

        if (codigo == null) {
            throw new IllegalArgumentException(
                    "El codigo del boleto es obligatorio.");
        }

        return dao_boleto.anular_boleto(codigo);
    }
}
