
package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class conexionbd {

    private static final String ARCHIVO_CONFIGURACION =
            "/db_config.properties";

    public static Connection conectar()
            throws SQLException, IOException {

        Properties propiedades = new Properties();

        try (InputStream archivo = conexionbd.class
                .getResourceAsStream(ARCHIVO_CONFIGURACION)) {

            if (archivo == null) {
                throw new IOException(
                        "No se encontro el archivo db_config.properties"
                );
            }

            propiedades.load(archivo);
        }

        String hostname = propiedades.getProperty("hostname");
        String port = propiedades.getProperty("port");
        String username = propiedades.getProperty("username");
        String database = propiedades.getProperty("database");
        String password = propiedades.getProperty("password");

        if (hostname == null || port == null
                || username == null || database == null
                || password == null) {
            throw new IOException(
                    "Faltan propiedades en db_config.properties"
            );
        }

        String url = "jdbc:postgresql://" + hostname
                + ":" + port + "/" + database;

        return DriverManager.getConnection(
                url, username, password
        );
    }
}
