
package seguridad;

import modelo.usuario;

public class control_acceso {

    private final usuario usuario_actual;

    public control_acceso(usuario usuario_actual) {
        this.usuario_actual = usuario_actual;
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

    public boolean puede_gestionar_usuarios() {
        return es_administrador();
    }

    public boolean puede_gestionar_artistas() {
        return es_administrador();
    }

    public boolean puede_gestionar_conciertos() {
        return es_administrador();
    }

    public boolean puede_gestionar_localidades() {
        return es_administrador();
    }

    public boolean puede_gestionar_inventario() {
        return es_administrador();
    }

    public boolean puede_vender_boletos() {
        return es_administrador() || es_vendedor();
    }

    public boolean puede_consultar_ventas() {
        return es_administrador() || es_vendedor();
    }

    public boolean puede_consultar_reportes_generales() {
        return es_administrador();
    }
}
