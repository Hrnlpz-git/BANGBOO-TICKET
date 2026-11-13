
package utilidades;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class generador_qr {

    public static void generar_qr(String contenido, Path destino)
            throws WriterException, IOException {

        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException(
                    "El contenido del QR no puede estar vacío."
            );
        }

        if (destino == null) {
            throw new IllegalArgumentException(
                    "La ruta del archivo QR no puede estar vacía."
            );
        }

        Map<EncodeHintType, Object> opciones = new HashMap<>();
        opciones.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        opciones.put(EncodeHintType.MARGIN, 2);

        QRCodeWriter generador = new QRCodeWriter();

        BitMatrix matriz = generador.encode(
                contenido,
                BarcodeFormat.QR_CODE,
                300,
                300,
                opciones
        );

        Path carpeta = destino.toAbsolutePath().getParent();

        if (carpeta != null) {
            java.nio.file.Files.createDirectories(carpeta);
        }

        MatrixToImageWriter.writeToPath(
                matriz,
                "PNG",
                destino
        );
    }
}
