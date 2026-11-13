
package utilidades;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class generador_pdf_entrada {

    private static final float ANCHO = 720;
    private static final float ALTO = 320;

    private static final Color FONDO =
            new Color(10, 13, 23);
    private static final Color PANEL =
            new Color(21, 26, 39);
    private static final Color NARANJA =
            new Color(240, 120, 50);
    private static final Color BLANCO =
            new Color(245, 246, 250);
    private static final Color GRIS =
            new Color(166, 174, 190);

    public Path generar_entrada(
        String titulo_concierto,
        String artista,
        String fecha,
        String hora,
        String lugar,
        String localidad,
        String precio,
        String codigo,
        Path ruta_qr,
        Path ruta_cartel,
        int id_boleto) throws IOException {

        Path carpeta = Paths.get("entradas_pdf");
        Files.createDirectories(carpeta);

        Path archivo_pdf = carpeta.resolve(
                "entrada_" + id_boleto + ".pdf"
        );

        try (PDDocument documento = new PDDocument()) {

            PDPage pagina = new PDPage(
                    new PDRectangle(ANCHO, ALTO)
            );
            documento.addPage(pagina);

            PDType1Font negrita = new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_BOLD
            );

            PDType1Font normal = new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA
            );

            try (PDPageContentStream contenido =
                         new PDPageContentStream(documento, pagina)) {

                // Fondo general.
                contenido.setNonStrokingColor(FONDO);
                contenido.addRect(0, 0, ANCHO, ALTO);
                contenido.fill();

                // Franja superior de marca.
                contenido.setNonStrokingColor(NARANJA);
                contenido.addRect(0, ALTO - 7, ANCHO, 7);
                contenido.fill();

                // Panel del talon lateral.
                contenido.setNonStrokingColor(PANEL);
                contenido.addRect(550, 7, 163, 306);
                contenido.fill();

                // Marca.
                escribir(contenido, negrita, 12,
                        25, 282, "BANGBOO-TICKET", NARANJA);

                escribir(contenido, normal, 7,
                        25, 266, "MUSICA EN VIVO  /  ENTRADA OFICIAL",
                        GRIS);

                // Cartel del evento.
                boolean cartel_cargado = false;

                if (ruta_cartel != null
                        && Files.isRegularFile(ruta_cartel)) {

                    try {
                        PDImageXObject cartel =
                                PDImageXObject.createFromFileByContent(
                                        ruta_cartel.toFile(),
                                        documento
                                );

                        float x = 25;
                        float y = 65;
                        float ancho_cartel = 145;
                        float alto_cartel = 180;

                        float escala = Math.min(
                                ancho_cartel / cartel.getWidth(),
                                alto_cartel / cartel.getHeight()
                        );

                        float ancho_final =
                                cartel.getWidth() * escala;
                        float alto_final =
                                cartel.getHeight() * escala;

                        contenido.drawImage(
                                cartel,
                                x + (ancho_cartel - ancho_final) / 2,
                                y + (alto_cartel - alto_final) / 2,
                                ancho_final,
                                alto_final
                        );

                        cartel_cargado = true;

                    } catch (IOException error_cartel) {
                        System.out.println(
                                "No se pudo cargar el cartel: "
                                + error_cartel.getMessage()
                        );
                    }
                }

                // Si no hay cartel disponible, mostrar un panel decorativo.
                if (!cartel_cargado) {
                    contenido.setNonStrokingColor(
                            new Color(34, 39, 54));
                    contenido.addRect(25, 65, 145, 180);
                    contenido.fill();

                    escribir(contenido, negrita, 13,
                            43, 163, "LIVE", NARANJA);

                    escribir(contenido, normal, 8,
                            43, 145, "MUSIC EVENT", BLANCO);
                }

                // Datos principales del concierto.
                escribir(contenido, normal, 8,
                        190, 239, "EVENTO", NARANJA);

                escribir_largo(contenido, negrita, 20,
                        190, 211, titulo_concierto, BLANCO, 29);

                escribir_largo(contenido, negrita, 12,
                        190, 184, artista, BLANCO, 38);

                // Separador.
                contenido.setStrokingColor(
                        new Color(55, 62, 79));
                contenido.setLineWidth(0.8f);
                contenido.moveTo(190, 170);
                contenido.lineTo(525, 170);
                contenido.stroke();

                // Información del evento.

// Fecha.
escribir(contenido, normal, 7,
        190, 150, "FECHA", GRIS);

escribir_largo(contenido, negrita, 10,
        190, 134, fecha, BLANCO, 32);

// Hora.
escribir(contenido, normal, 7,
        355, 150, "HORA", GRIS);

escribir_largo(contenido, negrita, 10,
        355, 134, hora, NARANJA, 20);

// Recinto.
escribir(contenido, normal, 7,
        190, 105, "RECINTO", GRIS);

escribir_largo(contenido, negrita, 10,
        190, 89, lugar, BLANCO, 32);

// Localidad.
escribir(contenido, normal, 7,
        355, 105, "LOCALIDAD", GRIS);

escribir_largo(contenido, negrita, 12,
        355, 89, localidad, NARANJA, 22);

// Precio.
escribir(contenido, normal, 7,
        190, 62, "PRECIO", GRIS);

escribir_largo(contenido, negrita, 12,
        190, 46, precio, BLANCO, 20);

                // Identificador de la entrada.
                escribir(contenido, normal, 8,
                        25, 43, "ENTRADA No.", GRIS);

                escribir(contenido, negrita, 14,
                        25, 24, String.valueOf(id_boleto), NARANJA);

                escribir(contenido, normal, 7,
                        190, 24, "CONSERVA ESTA ENTRADA PARA EL ACCESO",
                        GRIS);

                // Linea de perforacion entre el boleto y el talon.
                contenido.setStrokingColor(
                        new Color(105, 112, 130));
                contenido.setLineWidth(1);

                float y = 12;
                while (y < 307) {
                    contenido.moveTo(550, y);
                    contenido.lineTo(550, y + 5);
                    contenido.stroke();
                    y += 10;
                }

                // Talon lateral.
                escribir(contenido, negrita, 9,
                        566, 282, "CONTROL DE ACCESO", NARANJA);

                escribir(contenido, normal, 7,
                        566, 267, "ESCANEA AL INGRESAR", GRIS);

                // QR real del boleto.
                if (ruta_qr != null
                        && Files.isRegularFile(ruta_qr)) {

                    PDImageXObject qr =
                            PDImageXObject.createFromFileByContent(
                                    ruta_qr.toFile(),
                                    documento
                            );

                    float tamano_qr = 112;
                    float x_qr = 575;
                    float y_qr = 137;

                    // Fondo blanco para facilitar la lectura del QR.
                    contenido.setNonStrokingColor(Color.WHITE);
                    contenido.addRect(
                            x_qr - 5, y_qr - 5,
                            tamano_qr + 10, tamano_qr + 10
                    );
                    contenido.fill();

                    contenido.drawImage(
                            qr, x_qr, y_qr,
                            tamano_qr, tamano_qr
                    );

                } else {
                    escribir(contenido, negrita, 9,
                            568, 190, "QR NO DISPONIBLE", NARANJA);
                }

                escribir(contenido, normal, 8,
                        566, 119, "CODIGO UNICO", GRIS);

                escribir_largo(contenido, negrita, 6.5f,
                        566, 105, codigo, BLANCO, 25);

                contenido.setStrokingColor(
                        new Color(65, 72, 89));
                contenido.setLineWidth(0.7f);
                contenido.moveTo(566, 88);
                contenido.lineTo(700, 88);
                contenido.stroke();

                escribir(contenido, negrita, 8,
                        566, 69, "ENTRADA PERSONAL", BLANCO);

                escribir(contenido, normal, 7,
                        566, 55, "Un solo acceso", GRIS);

                escribir(contenido, normal, 7,
                        566, 28, "BANGBOO-TICKET", NARANJA);
            }

            documento.save(archivo_pdf.toFile());
        }

        return archivo_pdf;
    }

    private void escribir(
            PDPageContentStream contenido,
            PDType1Font fuente,
            float tamano,
            float x,
            float y,
            String texto,
            Color color) throws IOException {

        String texto_limpio = limpiar_texto(texto);

        contenido.beginText();
        contenido.setFont(fuente, tamano);
        contenido.setNonStrokingColor(color);
        contenido.newLineAtOffset(x, y);
        contenido.showText(texto_limpio);
        contenido.endText();
    }

    private void escribir_largo(
            PDPageContentStream contenido,
            PDType1Font fuente,
            float tamano,
            float x,
            float y,
            String texto,
            Color color,
            int max_caracteres) throws IOException {

        String texto_limpio = limpiar_texto(texto);

        if (texto_limpio.length() > max_caracteres) {
            texto_limpio = texto_limpio.substring(
                    0, Math.max(1, max_caracteres - 3)
            ) + "...";
        }

        escribir(contenido, fuente, tamano,
                x, y, texto_limpio, color);
    }

    private String limpiar_texto(String texto) {

        if (texto == null) {
            return "";
        }

        String limpio = texto
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("ñ", "n")
                .replace("Ñ", "N")
                .replace("¿", "")
                .replace("¡", "");

        StringBuilder resultado = new StringBuilder();

        for (char caracter : limpio.toCharArray()) {
            if (caracter >= 32 && caracter <= 255) {
                resultado.append(caracter);
            } else {
                resultado.append('?');
            }
        }

        return resultado.toString();
    }
}
