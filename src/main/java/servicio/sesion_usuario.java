
package servicio;

import dao.usuario_dao;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import modelo.usuario;

public class sesion_usuario {

    private usuario usuario_actual;

    private final usuario_dao dao_usuario;

    public sesion_usuario() {
        dao_usuario = new usuario_dao();
        usuario_actual = null;
    }

    public boolean iniciar_sesion(
            String nombre_usuario,
            char[] contrasena)
            throws SQLException, IOException,
            GeneralSecurityException {

        if (nombre_usuario == null
                || nombre_usuario.trim().isEmpty()
                || contrasena == null
                || contrasena.length == 0) {

            return false;
        }

        usuario encontrado = dao_usuario.autenticar(
                nombre_usuario.trim(),
                contrasena);

        if (encontrado == null) {
            usuario_actual = null;
            return false;
        }

        usuario_actual = encontrado;
        return true;
    }

    public void cerrar_sesion() {
        usuario_actual = null;
    }

    public boolean hay_sesion_activa() {
        return usuario_actual != null;
    }

    public usuario obtener_usuario_actual() {
        return usuario_actual;
    }

    public boolean es_administrador() {
        return usuario_actual != null
                && "administrador".equals(
                        usuario_actual.getRol());
    }

    public boolean es_vendedor() {
        return usuario_actual != null
                && "vendedor".equals(
                        usuario_actual.getRol());
    }
}
