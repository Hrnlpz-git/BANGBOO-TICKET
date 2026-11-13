
package servicio;

import dao.detalle_venta_dao;
import dao.inventario_boletos_dao;
import modelo.inventario_boletos;

import java.io.IOException;
import java.sql.SQLException;

public class control_inventario_servicio {

    private final inventario_boletos_dao inventario_dao =
            new inventario_boletos_dao();

    private final detalle_venta_dao detalle_dao =
            new detalle_venta_dao();

    public void consultar_inventario(int id_inventario)
            throws SQLException, IOException {

        if (id_inventario <= 0) {
            throw new IllegalArgumentException(
                    "El ID del inventario debe ser mayor que cero.");
        }

        inventario_boletos inventario =
                inventario_dao.buscar_por_id(id_inventario);

        if (inventario == null) {
            throw new IllegalArgumentException(
                    "No existe el inventario indicado.");
        }

        int cantidad_total =
                inventario.getCantidad_total();

        int cantidad_disponible =
                inventario.getCantidad_disponible();

        int boletos_descontados =
                cantidad_total - cantidad_disponible;

        int boletos_registrados =
                detalle_dao.contar_boletos_vendidos(
                        id_inventario);

        int diferencia =
                boletos_registrados - boletos_descontados;

        System.out.println("====================================");
        System.out.println("CONTROL DE INVENTARIO");
        System.out.println("====================================");

        System.out.println(
                "ID del inventario: "
                + inventario.getId_inventario());

        System.out.println(
                "Precio por boleto: Q"
                + inventario.getPrecio());

        System.out.println(
                "Cantidad total: "
                + cantidad_total);

        System.out.println(
                "Cantidad disponible: "
                + cantidad_disponible);

        System.out.println(
                "Boletos descontados: "
                + boletos_descontados);

        System.out.println(
                "Boletos registrados en ventas: "
                + boletos_registrados);

        System.out.println(
                "Diferencia encontrada: "
                + diferencia);

        if (diferencia == 0) {
            System.out.println(
                    "RESULTADO: Inventario consistente.");
        } else {
            System.out.println(
                    "RESULTADO: Se requiere revisar "
                    + "los registros de ventas.");
        }

        System.out.println("====================================");
    }
}
