
package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import servicio.sesion_usuario;
import utilidades.ventana_utilidad;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.animation.AnimationTimer;
import java.awt.image.BufferedImage;

import java.awt.Dimension;
import java.util.Arrays;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import servicio.validacion_boleto_servicio;

import java.util.UUID;

public class escanear_boleto {
    
    

    private final Stage ventana;
    private final sesion_usuario sesion;
    
    private Webcam camara;
    private AnimationTimer temporizador_camara;
    private ImageView vista_camara;
    private boolean camara_activa = false;

    private final validacion_boleto_servicio servicio_validacion =
            new validacion_boleto_servicio();
    
    private boolean validacion_en_progreso = false;
private String ultimo_codigo_detectado = null;
private int cuadros_sin_qr = 0;
    
    

    private static final String FONDO = "#080D18";
    private static final String PANEL = "#101827";
    private static final String NARANJA = "#F07832";
    private static final String TEXTO = "#F3F1EA";
    private static final String SECUNDARIO = "#9BAAC4";

    public escanear_boleto(
            Stage ventana,
            sesion_usuario sesion) {

        this.ventana = ventana;
        this.sesion = sesion;
    }
    


public void mostrar() {

    Label titulo = new Label("VALIDAR BOLETOS");
    titulo.setTextFill(Color.web(NARANJA));
    titulo.setFont(Font.font("Arial", 26));

    Label descripcion = new Label(
            "Escanea el código QR del boleto "
            + "con la cámara de tu laptop."
    );
    descripcion.setTextFill(Color.web(SECUNDARIO));
    descripcion.setWrapText(true);
    descripcion.setFont(Font.font("Arial", 14));

Label estado = new Label(
        "Presiona INICIAR ESCÁNER para activar la cámara."
);

estado.setTextFill(Color.web(TEXTO));
estado.setWrapText(true);
estado.setFont(Font.font("Arial", 15));
estado.setAlignment(Pos.CENTER);
estado.setMaxWidth(700);
estado.setPadding(new Insets(14, 20, 14, 20));

estado.setStyle(
        "-fx-background-color: #1B2638;"
        + "-fx-border-color: #344563;"
        + "-fx-border-radius: 10;"
        + "-fx-background-radius: 10;"
);

    vista_camara = new ImageView();
    vista_camara.setFitWidth(640);
    vista_camara.setFitHeight(400);
    vista_camara.setPreserveRatio(true);
    vista_camara.setSmooth(true);

    Button boton_iniciar = new Button("INICIAR ESCÁNER");
    boton_iniciar.setPrefSize(240, 48);
    boton_iniciar.setStyle(
            "-fx-background-color: " + NARANJA + ";"
            + "-fx-text-fill: #101827;"
            + "-fx-font-weight: bold;"
            + "-fx-background-radius: 8;"
            + "-fx-cursor: hand;"
    );

    boton_iniciar.setOnAction(evento -> {
        if (!camara_activa) {
            iniciar_camara(estado, boton_iniciar);
        }
    });

    Button boton_volver = new Button("VOLVER AL MENÚ");
    boton_volver.setStyle(
            "-fx-background-color: #263550;"
            + "-fx-text-fill: " + TEXTO + ";"
            + "-fx-font-weight: bold;"
            + "-fx-background-radius: 7;"
            + "-fx-cursor: hand;"
    );

    boton_volver.setOnAction(evento -> {
        detener_camara();
        new menu_principal(ventana, sesion).mostrar();
    });

    VBox contenido = new VBox(
            18,
            titulo,
            descripcion,
            vista_camara,
            estado,
            boton_iniciar,
            boton_volver
    );

    contenido.setAlignment(Pos.CENTER);
    contenido.setPadding(new Insets(25));
    contenido.setMaxWidth(800);
    contenido.setStyle(
            "-fx-background-color: " + PANEL + ";"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: #263550;"
            + "-fx-border-radius: 14;"
    );

    VBox centro = new VBox(contenido);
    centro.setAlignment(Pos.CENTER);
    centro.setPadding(new Insets(20));
    centro.setStyle("-fx-background-color: " + FONDO + ";");

    BorderPane raiz = new BorderPane();
    raiz.setCenter(centro);
    raiz.setStyle("-fx-background-color: " + FONDO + ";");

    Scene escena = new Scene(raiz, 1000, 850);

    ventana.setTitle("BANGBOO-TICKET | Validación de boletos");
    ventana.setScene(escena);
    ventana_utilidad.configurar(ventana);
    ventana.show();
}


private void iniciar_camara(
        Label estado,
        Button boton_iniciar) {

    estado.setText("Conectando con la cámara...");
    boton_iniciar.setDisable(true);

    Thread hilo = new Thread(() -> {
        try {
Webcam camara_local = Webcam.getDefault();

if (camara_local == null) {
    Platform.runLater(() -> {
        estado.setText("No se encontró una cámara conectada.");
        boton_iniciar.setDisable(false);
    });
    return;
}

// Intentar usar la resolución más alta disponible.
Dimension[] resoluciones = camara_local.getViewSizes();

if (resoluciones != null && resoluciones.length > 0) {
    Dimension mayor = Arrays.stream(resoluciones)
            .max(java.util.Comparator.comparingInt(
                    dimension -> dimension.width * dimension.height
            ))
            .orElse(resoluciones[0]);

    camara_local.setViewSize(mayor);
}

camara_local.open();
            if (camara_local == null) {
                Platform.runLater(() -> {
                    estado.setText(
                            "No se encontró una cámara conectada."
                    );
                    boton_iniciar.setDisable(false);
                });
                return;
            }

            camara_local.open();

            Platform.runLater(() -> {
                camara = camara_local;
                camara_activa = true;
                boton_iniciar.setText("CÁMARA ACTIVA");
                estado.setText(
                        "Cámara encendida. Preparado para escanear."
                );

                temporizador_camara = new AnimationTimer() {

@Override
public void handle(long ahora) {
    if (camara != null && camara.isOpen()) {
        BufferedImage imagen = camara.getImage();

        if (imagen != null) {
            vista_camara.setImage(
                    SwingFXUtils.toFXImage(imagen, null)
            );

            // Intentar leer el QR de la imagen.
            String codigo = leer_codigo_qr(imagen);

            if (codigo != null) {
                cuadros_sin_qr = 0;

                if (!codigo.equals(ultimo_codigo_detectado)
                        && !validacion_en_progreso) {

                    ultimo_codigo_detectado = codigo;
                    validacion_en_progreso = true;

                    validar_codigo_detectado(codigo, estado);
                }
                
                
            } else {
                cuadros_sin_qr++;

                // Permitir volver a escanear cuando se retire el QR.
                if (cuadros_sin_qr >= 20) {
                    ultimo_codigo_detectado = null;
                }
            }
        }
    }
}

                };

                temporizador_camara.start();
            });

        } catch (Exception error) {
            Platform.runLater(() -> {
                estado.setText(
                        "No se pudo iniciar la cámara: "
                        + error.getMessage()
                );
                boton_iniciar.setDisable(false);
            });
        }
    });

    hilo.setDaemon(true);
    hilo.start();
}

private void detener_camara() {

    camara_activa = false;

    if (temporizador_camara != null) {
        temporizador_camara.stop();
        temporizador_camara = null;
    }

    if (camara != null) {
        if (camara.isOpen()) {
            camara.close();
        }
        camara = null;
    }
}

private String leer_codigo_qr(BufferedImage imagen) {
    try {
        BinaryBitmap imagen_binaria = new BinaryBitmap(
                new HybridBinarizer(
                        new BufferedImageLuminanceSource(imagen)
                )
        );

        Result resultado = new QRCodeReader().decode(imagen_binaria);

        return resultado.getText();

    } catch (NotFoundException
            | ChecksumException
            | FormatException error) {
        return null;
    }
}



private void validar_codigo_detectado(
        String codigo,
        Label estado) {

    try {
        UUID uuid = UUID.fromString(codigo.trim());

        String resultado = servicio_validacion.validar_boleto(
                sesion,
                uuid
        );

        System.out.println(resultado);

Platform.runLater(() -> {

    boolean correcto = resultado.contains("CORRECTAMENTE");

    actualizar_estado(
            estado,
            resultado,
            correcto
    );
});

    } catch (IllegalArgumentException error) {
Platform.runLater(() -> {

    actualizar_estado(
            estado,
            "QR no válido: no contiene un código de boleto válido.",
            false
    );

});

        ultimo_codigo_detectado = null;

    } catch (Exception error) {
Platform.runLater(() -> {

    actualizar_estado(
            estado,
            "Error al validar: " + error.getMessage(),
            false
    );

});

    } finally {
        validacion_en_progreso = false;
    }
}

private void actualizar_estado(
        Label estado,
        String mensaje,
        boolean correcto) {

    estado.setText(mensaje);

    if (correcto) {

        // Aviso verde: boleto validado correctamente.
        estado.setTextFill(Color.web("#BDF7D0"));

        estado.setStyle(
                "-fx-background-color: #123D2C;"
                + "-fx-border-color: #258653;"
                + "-fx-border-width: 1.5;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 14 20 14 20;"
        );

    } else {

        // Aviso rojo: boleto utilizado, anulado o inválido.
        estado.setTextFill(Color.web("#FFC0C5"));

        estado.setStyle(
                "-fx-background-color: #491F27;"
                + "-fx-border-color: #B83F50;"
                + "-fx-border-width: 1.5;"
                + "-fx-border-radius: 10;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 14 20 14 20;"
        );
    }
}

}
