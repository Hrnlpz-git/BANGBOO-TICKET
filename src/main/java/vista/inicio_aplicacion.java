
package vista;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.layout.Priority;

import servicio.sesion_usuario;

import utilidades.ventana_utilidad;

import javafx.scene.shape.Rectangle;

public class inicio_aplicacion extends Application {

    private final sesion_usuario sesion = new sesion_usuario();

    private static final String FONDO = "#080D18";
    private static final String PANEL = "#0B1220";
    private static final String AZUL = "#2855E8";
    private static final String NARANJA = "#F07832";
    private static final String TEXTO = "#F3F1EA";
    private static final String SECUNDARIO = "#9BAAC4";

    @Override
    public void start(Stage ventana) {
        mostrar_inicio(ventana);
    }

    public void mostrar_inicio(Stage ventana) {

        // ENCABEZADO
        ImageView icono = cargar_imagen(
                "/imagenes/bangboo_ticket_icono.png",
                58, 65
        );
        if (icono == null) {
    icono = new ImageView();
}

        Label nombre = new Label("BANGBOO-");
        nombre.setFont(Font.font("Arial", 29));
        nombre.setTextFill(Color.web(TEXTO));

        Label ticket = new Label("TICKET");
        ticket.setFont(Font.font("Arial", 29));
        ticket.setTextFill(Color.web(AZUL));

        HBox nombre_marca = new HBox(0, nombre, ticket);
        nombre_marca.setAlignment(Pos.CENTER_LEFT);

        Label lema = new Label(
                "TU ENTRADA AL MUNDO DE LA MÚSICA"
        );
        lema.setFont(Font.font("Arial", 11));
        lema.setTextFill(Color.web(SECUNDARIO));

        VBox marca_texto = new VBox(3, nombre_marca, lema);
        marca_texto.setAlignment(Pos.CENTER_LEFT);

        HBox encabezado = new HBox(15, icono, marca_texto);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        encabezado.setPadding(
                new Insets(15, 28, 15, 28)
        );
        encabezado.setStyle(
                "-fx-background-color: " + FONDO + ";"
                + "-fx-border-color: transparent transparent #263550 transparent;"
        );


    
        // FONDO DEL LOGIN: BELLE Y BANGBOO
        ImageView fondo_login = cargar_imagen(
                "/imagenes/fondo_login.png",
                1200, 760
        );

        StackPane panel_izquierdo = new StackPane();

        if (fondo_login != null) {

            fondo_login.setPreserveRatio(true);
            fondo_login.setSmooth(true);

            double proporcion =
                    fondo_login.getImage().getWidth()
                    / fondo_login.getImage().getHeight();

            fondo_login.fitWidthProperty().bind(
                    javafx.beans.binding.Bindings.max(
                            panel_izquierdo.widthProperty(),
                            panel_izquierdo.heightProperty()
                                    .multiply(proporcion)
                    )
            );

            fondo_login.fitHeightProperty().bind(
                    fondo_login.fitWidthProperty()
                            .divide(proporcion)
            );

            StackPane.setAlignment(
                    fondo_login, Pos.CENTER
            );

            panel_izquierdo.getChildren().add(fondo_login);

            Rectangle recorte = new Rectangle();
            recorte.widthProperty().bind(
                    panel_izquierdo.widthProperty()
            );
            recorte.heightProperty().bind(
                    panel_izquierdo.heightProperty()
            );

            panel_izquierdo.setClip(recorte);

        } else {

            panel_izquierdo.setStyle(
                    "-fx-background-color: " + FONDO + ";"
            );
        }

        panel_izquierdo.setMinWidth(0);
        panel_izquierdo.setPrefWidth(1000);
        panel_izquierdo.setMaxWidth(Double.MAX_VALUE);
        panel_izquierdo.setMinHeight(0);
        panel_izquierdo.setMaxHeight(Double.MAX_VALUE);



        // PANEL DERECHO: INICIO DE SESIÓN
        ImageView icono_login = cargar_imagen(
                "/imagenes/bangboo_ticket_icono.png",
                58, 65
        );
        if (icono_login == null) {
    icono_login = new ImageView();
}

        Label marca_login = new Label("BANGBOO-");
        marca_login.setFont(Font.font("Arial", 26));
        marca_login.setTextFill(Color.web(TEXTO));

        Label ticket_login = new Label("TICKET");
        ticket_login.setFont(Font.font("Arial", 26));
        ticket_login.setTextFill(Color.web(AZUL));

        HBox titulo_marca = new HBox(
                0, marca_login, ticket_login
        );
        titulo_marca.setAlignment(Pos.CENTER_LEFT);

        Label subtitulo_marca = new Label(
                "SISTEMA DE VENTA DE BOLETOS"
        );
        subtitulo_marca.setFont(Font.font("Arial", 10));
        subtitulo_marca.setTextFill(Color.web(SECUNDARIO));

        VBox texto_marca = new VBox(
                4, titulo_marca, subtitulo_marca
        );

        HBox cabecera_login = new HBox(
                12, icono_login, texto_marca
        );
        cabecera_login.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("Iniciar sesión");
        titulo.setFont(Font.font("Arial", 29));
        titulo.setTextFill(Color.web(TEXTO));

        Label descripcion = new Label(
                "Ingresa tus credenciales para continuar"
        );
        descripcion.setFont(Font.font("Arial", 14));
        descripcion.setTextFill(Color.web(SECUNDARIO));
        descripcion.setWrapText(true);

        TextField campo_usuario = new TextField();
        campo_usuario.setPromptText("Usuario");
        campo_usuario.setPrefHeight(46);
        campo_usuario.setMaxWidth(Double.MAX_VALUE);
        campo_usuario.setStyle(estilo_campo());

        PasswordField campo_contrasena = new PasswordField();
        campo_contrasena.setPromptText("Contraseña");
        campo_contrasena.setPrefHeight(46);
        campo_contrasena.setMaxWidth(Double.MAX_VALUE);
        campo_contrasena.setStyle(estilo_campo());

        Button boton_iniciar = new Button(
                "➜    INICIAR SESIÓN"
        );
        boton_iniciar.setPrefHeight(48);
        boton_iniciar.setMaxWidth(Double.MAX_VALUE);
        boton_iniciar.setStyle(estilo_boton());

        Label mensaje = new Label("");
        mensaje.setWrapText(true);
        mensaje.setTextFill(Color.web(NARANJA));
        mensaje.setMaxWidth(Double.MAX_VALUE);

        // SE CONSERVA LA AUTENTICACIÓN EXISTENTE
        boton_iniciar.setOnAction(evento -> {

            String nombre_usuario =
                    campo_usuario.getText().trim();

            char[] contrasena =
                    campo_contrasena.getText().toCharArray();

            if (nombre_usuario.isEmpty()
                    || contrasena.length == 0) {

                mensaje.setText(
                        "Ingresa tu usuario y contraseña."
                );
                Arrays.fill(contrasena, '\0');
                return;
            }

            boton_iniciar.setDisable(true);
            mensaje.setText("");

            try {
                boolean acceso_correcto =
                        sesion.iniciar_sesion(
                                nombre_usuario,
                                contrasena
                        );

                if (acceso_correcto) {

                    if (sesion.es_administrador()
                            || sesion.es_vendedor()) {

                        new menu_principal(
                                ventana, sesion
                        ).mostrar();

                    } else {
                        mensaje.setText(
                                "El usuario no tiene un rol válido."
                        );
                        sesion.cerrar_sesion();
                    }

                } else {
                    mensaje.setText(
                            "Usuario o contraseña incorrectos."
                    );
                }

            } catch (SQLException | IOException
                    | GeneralSecurityException ex) {

                mensaje.setText(
                        "No fue posible iniciar sesión. "
                        + "Revisa la conexión."
                );
                ex.printStackTrace();

            } finally {
                Arrays.fill(contrasena, '\0');
                campo_contrasena.clear();
                boton_iniciar.setDisable(false);
            }
        });

        campo_contrasena.setOnAction(
                evento -> boton_iniciar.fire()
        );

        Label separador = new Label(
                "──────────   BANGBOO-TICKET   ──────────"
        );
        separador.setFont(Font.font("Arial", 10));
        separador.setTextFill(Color.web(SECUNDARIO));
        separador.setMaxWidth(Double.MAX_VALUE);
        separador.setAlignment(Pos.CENTER);
        separador.setWrapText(true);

        Label buena_suerte = new Label("¡MÁS MÚSICA, MÁS MOMENTOS!");
        buena_suerte.setFont(Font.font("Arial", 11));
        buena_suerte.setMaxWidth(Double.MAX_VALUE);
        buena_suerte.setAlignment(Pos.CENTER);
        buena_suerte.setTextFill(Color.web(AZUL));
        buena_suerte.setWrapText(true);


VBox formulario = new VBox(
        8,
        cabecera_login,
        crear_espacio(8),
        titulo,
        descripcion,
        crear_espacio(2),
        campo_usuario,
        campo_contrasena,
        crear_espacio(1),
        boton_iniciar,
        mensaje,
        crear_espacio(3),
        separador,
        crear_espacio(4),
        buena_suerte
);


formulario.setPadding(new Insets(18));
formulario.setAlignment(Pos.CENTER_LEFT);
formulario.setMaxWidth(400);
        formulario.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #263550;"
                + "-fx-border-radius: 16;"
        );

        StackPane panel_formulario = new StackPane(formulario);
        panel_formulario.setPadding(new Insets(12));
        panel_formulario.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );
        panel_formulario.setMinWidth(420);
        panel_formulario.setPrefWidth(550);



        // DISTRIBUCIÓN: IMAGEN Y FORMULARIO
        HBox centro = new HBox(0);

        centro.setAlignment(Pos.CENTER_LEFT);
        centro.setFillHeight(true);
        centro.setSpacing(0);
        centro.setMaxWidth(Double.MAX_VALUE);
        centro.setMaxHeight(Double.MAX_VALUE);

        HBox.setHgrow(panel_izquierdo, Priority.ALWAYS);
        HBox.setHgrow(panel_formulario, Priority.NEVER);

        panel_izquierdo.setMinWidth(0);
        panel_izquierdo.setMaxWidth(Double.MAX_VALUE);

        panel_formulario.setMinWidth(420);
        panel_formulario.setPrefWidth(480);
        panel_formulario.setMaxWidth(480);

        centro.getChildren().addAll(
                panel_izquierdo,
                panel_formulario
        );





        BorderPane raiz = new BorderPane();

        raiz.setTop(encabezado);
        raiz.setCenter(centro);

        raiz.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centro.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        centro.setMinSize(0, 0);

        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );


        Scene escena = new Scene(raiz, 1200, 760);


ventana.setTitle("BANGBOO-TICKET | Iniciar sesión");
ventana.setResizable(true);
ventana.setScene(escena);

ventana.sizeToScene();
ventana.centerOnScreen();

ventana_utilidad.configurar(ventana);
ventana.show();
ventana.setFullScreen(true);


    }

    private ImageView cargar_imagen(
            String ruta, double ancho, double alto) {

        try {
            var recurso = getClass().getResource(ruta);

            if (recurso == null) {
                return null;
            }

            Image imagen = new Image(
                    recurso.toExternalForm()
            );

            if (imagen.isError()) {
                return null;
            }

            ImageView vista_imagen = new ImageView(imagen);
            vista_imagen.setFitWidth(ancho);
            vista_imagen.setFitHeight(alto);
            vista_imagen.setPreserveRatio(true);
            vista_imagen.setSmooth(true);

            return vista_imagen;

        } catch (RuntimeException ex) {
            return null;
        }
    }

    private VBox crear_espacio(double alto) {
        VBox espacio = new VBox();
        espacio.setMinHeight(alto);
        espacio.setPrefHeight(alto);
        espacio.setMaxHeight(alto);
        return espacio;
    }

    private String estilo_campo() {
        return "-fx-background-color: #0D1525;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-prompt-text-fill: #8293B4;"
                + "-fx-border-color: #405477;"
                + "-fx-border-radius: 9;"
                + "-fx-background-radius: 9;"
                + "-fx-padding: 12;"
                + "-fx-font-size: 15px;";
    }

    private String estilo_boton() {
return "-fx-background-color: " + AZUL + ";"
        + "-fx-text-fill: white;"
        + "-fx-font-family: Arial;"
        + "-fx-font-size: 15px;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 10;"
        + "-fx-padding: 14px 18px;"
        + "-fx-cursor: hand;";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
