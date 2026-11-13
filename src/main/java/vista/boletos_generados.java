
package vista;

import conexion.conexionbd;
import servicio.sesion_usuario;
import utilidades.ventana_utilidad;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import utilidades.generador_qr;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import java.io.IOException;
import com.google.zxing.WriterException;

import utilidades.generador_pdf_entrada;
import java.awt.Desktop;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;



public class boletos_generados {

    private final Stage ventana;
    private final sesion_usuario sesion;
    private final long id_venta;
    private final String nombre_concierto;
    private final String nombre_localidad;
    private final BigDecimal precio;
    private final int cantidad;
    private final BigDecimal total;

    private final String FONDO = "#101827";
    private final String PANEL = "#1C2940";
    private final String NARANJA = "#FF9D42";
    private final String TEXTO = "#F4F7FC";
    private final String SUAVE = "#AAB7CC";

    public boletos_generados(
            Stage ventana,
            sesion_usuario sesion,
            long id_venta,
            String nombre_concierto,
            String nombre_localidad,
            BigDecimal precio,
            int cantidad,
            BigDecimal total) {

        this.ventana = ventana;
        this.sesion = sesion;
        this.id_venta = id_venta;
        this.nombre_concierto = nombre_concierto;
        this.nombre_localidad = nombre_localidad;
        this.precio = precio;
        this.cantidad = cantidad;
        this.total = total;
    }

    public void mostrar() {

        VBox contenido = new VBox(22);
        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setPadding(new Insets(30));
        contenido.setStyle("-fx-background-color: " + FONDO + ";");

        Label titulo = new Label("¡TU COMPRA ESTÁ LISTA!");
        titulo.setFont(Font.font("Arial", 25));
        titulo.setTextFill(Color.web(NARANJA));

        Label subtitulo = new Label(
                "BANGBOO-TICKET  |  ENTRADAS DIGITALES");
        subtitulo.setTextFill(Color.web(SUAVE));
        subtitulo.setFont(Font.font("Arial", 13));

        Label numero_venta = new Label(
                "VENTA #" + id_venta);
        numero_venta.setTextFill(Color.web(TEXTO));
        numero_venta.setFont(Font.font("Arial", 15));

        contenido.getChildren().addAll(
                titulo, subtitulo, numero_venta);

        ScrollPane desplazamiento = new ScrollPane();
        VBox lista_boletos = new VBox(18);

        lista_boletos.setPadding(new Insets(12));
        lista_boletos.setAlignment(Pos.TOP_CENTER);
        lista_boletos.setStyle(
                "-fx-background-color: transparent;");

        desplazamiento.setContent(lista_boletos);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setStyle(
                "-fx-background: " + FONDO + ";"
                + "-fx-background-color: " + FONDO + ";");

        try (Connection conexion = conexionbd.conectar()) {

String sql =
        "SELECT b.id_boleto, b.codigo_unico, b.estado, "
        + "c.titulo_evento, c.fecha_concierto, c.hora_concierto, c.recinto, "
        + "c.ruta_cartel, a.nombre_artistico "
        + "FROM boletos b "
        + "INNER JOIN detalle_ventas d "
        + "ON b.id_detalle = d.id_detalle "
        + "INNER JOIN inventario_boletos i "
        + "ON d.id_inventario = i.id_inventario "
        + "INNER JOIN conciertos c "
        + "ON i.id_concierto = c.id_concierto "
        + "INNER JOIN artistas a "
        + "ON c.id_artista = a.id_artista "
        + "WHERE d.id_venta = ? "
        + "ORDER BY b.id_boleto";

            try (PreparedStatement consulta =
                    conexion.prepareStatement(sql)) {

                consulta.setLong(1, id_venta);

                try (ResultSet resultado =
                        consulta.executeQuery()) {

                    int numero = 1;

                    while (resultado.next()) {

long id_boleto = resultado.getLong("id_boleto");

String codigo = resultado.getString("codigo_unico");
String estado = resultado.getString("estado");

String titulo_pdf = resultado.getString("titulo_evento");
String artista_pdf = resultado.getString("nombre_artistico");
String recinto_pdf = resultado.getString("recinto");
String cartel_texto = resultado.getString("ruta_cartel");

java.sql.Date fecha_sql =
        resultado.getDate("fecha_concierto");

String fecha_pdf = fecha_sql == null
        ? "Fecha por confirmar"
        : fecha_sql.toLocalDate().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));

java.sql.Time hora_sql =
        resultado.getTime("hora_concierto");

String hora_pdf = hora_sql == null
        ? "Por confirmar"
        : hora_sql.toLocalTime().format(
                DateTimeFormatter.ofPattern("HH:mm"));

                        VBox tarjeta = new VBox(12);
                        tarjeta.setMaxWidth(570);
                        tarjeta.setPadding(new Insets(22));
                        tarjeta.setStyle(
                                "-fx-background-color: " + PANEL + ";"
                                + "-fx-background-radius: 14;"
                                + "-fx-border-color: " + NARANJA + ";"
                                + "-fx-border-radius: 14;"
                                + "-fx-border-width: 1.5;");

                        HBox encabezado = new HBox();
                        encabezado.setAlignment(Pos.CENTER_LEFT);

                        Label marca = new Label("BANGBOO");
                        marca.setTextFill(Color.web(NARANJA));
                        marca.setFont(Font.font("Arial", 20));

                        Label numero_boleto =
                                new Label("  TICKET #" + numero);

                        numero_boleto.setTextFill(Color.web(TEXTO));
                        numero_boleto.setFont(Font.font("Arial", 13));

                        encabezado.getChildren().addAll(
                                marca, numero_boleto);

                        Label concierto = new Label(
                                nombre_concierto);
                        concierto.setTextFill(Color.web(TEXTO));
                        concierto.setFont(Font.font("Arial", 21));

                        Label localidad = new Label(
                                "LOCALIDAD: " + nombre_localidad);
                        localidad.setTextFill(Color.web(SUAVE));

                        Label precio_boleto = new Label(
                                "PRECIO: Q"
                                + precio.toPlainString());
                        precio_boleto.setTextFill(Color.web(TEXTO));

                        Label codigo_titulo = new Label(
                                "CÓDIGO ÚNICO DEL BOLETO");
                        codigo_titulo.setTextFill(Color.web(NARANJA));
                        codigo_titulo.setFont(Font.font("Arial", 11));

                        Label codigo_texto = new Label(codigo);
                        codigo_texto.setTextFill(Color.web(TEXTO));
                        codigo_texto.setWrapText(true);
                        codigo_texto.setFont(Font.font("Arial", 12));
                        
Path ruta_qr = Paths.get(
        "boletos_qr",
        "boleto_venta_" + id_venta
                + "_numero_" + numero + ".png"
);

ImageView imagen_qr = new ImageView();

try {
    generador_qr.generar_qr(codigo, ruta_qr);

    Image imagen = new Image(
            ruta_qr.toUri().toString()
    );

    imagen_qr.setImage(imagen);
    imagen_qr.setFitWidth(180);
    imagen_qr.setFitHeight(180);
    imagen_qr.setPreserveRatio(true);

} catch (WriterException | IOException error) {
    Label error_qr = new Label(
            "No se pudo generar el QR: "
                    + error.getMessage()
    );

    error_qr.setTextFill(Color.web("#FF7777"));
    error_qr.setWrapText(true);

    tarjeta.getChildren().add(error_qr);
}                        

                        Button copiar = new Button("COPIAR CÓDIGO");
                        copiar.setMaxWidth(Double.MAX_VALUE);
                        copiar.setStyle(
                                "-fx-background-color: " + NARANJA + ";"
                                + "-fx-text-fill: #101827;"
                                + "-fx-font-weight: bold;"
                                + "-fx-background-radius: 7;"
                                + "-fx-cursor: hand;");

                        copiar.setOnAction(evento -> {
                            javafx.scene.input.ClipboardContent datos =
                                    new javafx.scene.input.ClipboardContent();

                            datos.putString(codigo);

                            javafx.scene.input.Clipboard.getSystemClipboard()
                                    .setContent(datos);

                            copiar.setText("¡CÓDIGO COPIADO!");
                        });
                        
Button descargar_pdf = new Button("DESCARGAR ENTRADA PDF");
descargar_pdf.setMaxWidth(Double.MAX_VALUE);
descargar_pdf.setStyle(
        "-fx-background-color: #FF9D42;"
        + "-fx-text-fill: #101827;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 7;"
        + "-fx-cursor: hand;");

descargar_pdf.setOnAction(evento -> {
    System.out.println("BOTON PDF PRESIONADO");
    try {
        Path ruta_cartel = null;

        if (cartel_texto != null && !cartel_texto.trim().isEmpty()) {
            Path posible_cartel = Paths.get(cartel_texto);

            if (Files.exists(posible_cartel)) {
                ruta_cartel = posible_cartel;
            }
        }

        Path archivo_qr = ruta_qr;

        if (!Files.exists(archivo_qr)) {
            generador_qr.generar_qr(codigo, archivo_qr);
        }

        Path archivo_pdf = new generador_pdf_entrada().generar_entrada(
                titulo_pdf,
                artista_pdf,
                fecha_pdf,
                hora_pdf,
                recinto_pdf,
                nombre_localidad,
                "Q" + precio.toPlainString(),
                codigo,
                archivo_qr,
                ruta_cartel,
                (int) id_boleto
        );

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Entrada PDF generada");
        alerta.setHeaderText("¡Tu entrada está lista!");
        alerta.setContentText(
                "Se guardó el archivo en:\n"
                + archivo_pdf.toAbsolutePath());
        alerta.showAndWait();

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(archivo_pdf.toFile());
        }

} catch (Exception error) {
    error.printStackTrace();

    Alert alerta = new Alert(Alert.AlertType.ERROR);
    alerta.setTitle("Error al generar la entrada");
    alerta.setHeaderText("No se pudo crear el PDF");
    alerta.setContentText(
            error.getClass().getSimpleName()
            + ": " + error.getMessage());
    alerta.showAndWait();
}
});

                        Label estado_boleto = new Label(
                                "ESTADO: " + estado.toUpperCase());
                        estado_boleto.setTextFill(
                                Color.web("#80E0AA"));

tarjeta.getChildren().addAll(
        encabezado,
        concierto,
        localidad,
        precio_boleto,
        imagen_qr,
        codigo_titulo,
        codigo_texto,
        copiar,
        descargar_pdf,
        estado_boleto);

                        lista_boletos.getChildren().add(tarjeta);
                        numero++;
                    }
                }
            }

        } catch (SQLException | java.io.IOException error) {

            Label mensaje_error = new Label(
                    "No se pudieron cargar los boletos: "
                    + error.getMessage());

            mensaje_error.setTextFill(Color.web("#FF7777"));
            mensaje_error.setWrapText(true);

            lista_boletos.getChildren().add(mensaje_error);
        }

        Label resumen = new Label(
                "Cantidad: " + cantidad
                + "     |     Total pagado: Q"
                + total.toPlainString());

        resumen.setTextFill(Color.web(TEXTO));
        resumen.setFont(Font.font("Arial", 15));

        Button volver = new Button("VOLVER A VENDER");
        volver.setPrefSize(240, 44);
        volver.setStyle(
                "-fx-background-color: " + NARANJA + ";"
                + "-fx-text-fill: #101827;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;");

        volver.setOnAction(evento ->
                new vender_boletos(ventana, sesion).mostrar());

        contenido.getChildren().addAll(
                desplazamiento, resumen, volver);

        Scene escena = new Scene(contenido, 950, 720);

        ventana.setTitle(
                "BANGBOO-TICKET | Boletos generados");

        ventana.setScene(escena);
        ventana_utilidad.configurar(ventana);
        ventana.show();
    }
}
