
package conexion;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

public class prueba_conexion {

    public static void main(String[] args) {

        try (Connection conexion = conexionbd.conectar()) {

            if (conexion != null && conexion.isValid(5)) {
                System.out.println(
                        "CONEXION EXITOSA A BANGBOO-TICKET"
                );
                System.out.println(
                        "Base de datos: "
                        + conexion.getCatalog()
                );
            }

        } catch (SQLException | IOException e) {
            System.out.println("ERROR AL CONECTAR:");
            System.out.println(e.getMessage());
        }
    }
}

