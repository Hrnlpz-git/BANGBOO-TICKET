
package utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class gestor_archivos {

    private static final String CARPETA_MULTIMEDIA =
            "multimedia";

    public static String copiar_archivo(
            File archivo_origen,
            String tipo) throws IOException {

        if (archivo_origen == null
                || !archivo_origen.isFile()) {
            throw new IOException(
                    "El archivo seleccionado no existe.");
        }

        String subcarpeta;

        switch (tipo.toLowerCase()) {
            case "artistas":
                subcarpeta = "artistas";
                break;

            case "videos":
                subcarpeta = "videos";
                break;

            case "carteles":
                subcarpeta = "carteles";
                break;

            case "conciertos":
                subcarpeta = "conciertos";
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de archivo no válido.");
        }

        Path carpeta_destino = Path.of(
                CARPETA_MULTIMEDIA,
                subcarpeta
        ).toAbsolutePath().normalize();

        Files.createDirectories(carpeta_destino);

        String nombre_original = archivo_origen.getName();

        String nombre_base = nombre_original;
        String extension = "";

        int punto = nombre_original.lastIndexOf('.');

        if (punto > 0) {
            nombre_base = nombre_original.substring(0, punto);
            extension = nombre_original.substring(punto);
        }

        String nombre_nuevo = nombre_original;
        Path destino = carpeta_destino.resolve(nombre_nuevo);

        int contador = 1;

        while (Files.exists(destino)) {
            nombre_nuevo = nombre_base
                    + "_" + contador
                    + extension;

            destino = carpeta_destino.resolve(nombre_nuevo);
            contador++;
        }

        Files.copy(
                archivo_origen.toPath(),
                destino
        );

        return destino.toString();
    }
}
