
package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import servicio.sesion_usuario;
import utilidades.ventana_utilidad;

import javafx.scene.control.ScrollPane;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class menu_principal {

    private final sesion_usuario sesion;
    private final Stage ventana;

    private static final String FONDO = "#080D18";
    private static final String PANEL = "#101827";
    private static final String AZUL = "#2855D9";
    private static final String NARANJA = "#F07832";
    private static final String TEXTO = "#F3F1EA";
    private static final String SECUNDARIO = "#9BAAC4";
    private static final String BORDE = "#263550";

    public menu_principal(Stage ventana, sesion_usuario sesion) {
        this.ventana = ventana;
        this.sesion = sesion;
    }

    public void mostrar() {

        boolean administrador = sesion.es_administrador();

        String nombre_usuario = sesion
                .obtener_usuario_actual()
                .getNombre_usuario();

        // MARCA DEL SISTEMA
        Label marca = new Label("BANGBOO-TICKET");
        marca.setFont(Font.font("Arial", 30));
        marca.setTextFill(Color.web(TEXTO));

        Label descripcion = new Label(
                "SISTEMA DE VENTA DE BOLETOS"
        );
        descripcion.setFont(Font.font("Arial", 15));
        descripcion.setTextFill(Color.web(SECUNDARIO));

        VBox textos_marca = new VBox(6, marca, descripcion);
        
        Image imagen_logo = new Image(
        getClass().getResourceAsStream(
                "/imagenes/bangboo.png"
        )
);

ImageView logo = new ImageView(imagen_logo);
logo.setFitWidth(140);
logo.setFitHeight(140);
logo.setPreserveRatio(true);


textos_marca.setAlignment(Pos.CENTER_LEFT);

        // BOTÓN CERRAR SESIÓN
        Button cerrar_sesion = new Button("CERRAR SESIÓN");
        cerrar_sesion.setPrefSize(175, 42);
        cerrar_sesion.setStyle(
                "-fx-background-color: #B94B2D;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 12px;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );

        cerrar_sesion.setOnMouseEntered(evento ->
                cerrar_sesion.setStyle(
                        "-fx-background-color: #D85C39;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 12px;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
                )
        );

        cerrar_sesion.setOnMouseExited(evento ->
                cerrar_sesion.setStyle(
                        "-fx-background-color: #B94B2D;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 12px;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
                )
        );

        cerrar_sesion.setOnAction(evento -> {
            sesion.cerrar_sesion();
            new inicio_aplicacion().mostrar_inicio(ventana);
        });

HBox identidad = new HBox(14, logo, textos_marca);
identidad.setAlignment(Pos.CENTER_LEFT);

HBox encabezado = new HBox(
        20, identidad, cerrar_sesion
);
        encabezado.setAlignment(Pos.CENTER_LEFT);
encabezado.setPadding(new Insets(3, 35, 3, 35));
                encabezado.setStyle(
                "-fx-background-color: rgba(16,24,39,0.94);"
                + "-fx-border-color: " + BORDE + ";"
                + "-fx-border-width: 0 0 1 0;"
        );

        javafx.scene.layout.Region espacio =
                new javafx.scene.layout.Region();
        HBox.setHgrow(espacio,
                javafx.scene.layout.Priority.ALWAYS);

        encabezado.getChildren().add(1, espacio);

        // BIENVENIDA
        Label etiqueta_bienvenida = new Label("BIENVENIDO A");
        etiqueta_bienvenida.setTextFill(Color.web(NARANJA));
        etiqueta_bienvenida.setFont(Font.font("Arial", 12));

        Label saludo = new Label(
                "¡Hola, " + nombre_usuario + "!"
        );
        saludo.setTextFill(Color.web(TEXTO));
        saludo.setFont(Font.font("Arial", 30));

        Label instrucciones = new Label(
                administrador
                ? "Administra los eventos y controla las operaciones."
                : "Gestiona la venta y consulta de boletos."
        );
        instrucciones.setTextFill(Color.web(SECUNDARIO));
        instrucciones.setFont(Font.font("Arial", 14));
        instrucciones.setWrapText(true);

        Label rol = new Label(
                administrador
                ? "●  ADMINISTRADOR"
                : "●  VENDEDOR"
        );
        rol.setTextFill(Color.web(NARANJA));
        rol.setFont(Font.font("Arial", 12));
        rol.setStyle(
                "-fx-background-color: #29251F;"
                + "-fx-background-radius: 20;"
                + "-fx-padding: 8 14 8 14;"
        );

        VBox bienvenida = new VBox(
                12,
                etiqueta_bienvenida,
                saludo,
                instrucciones,
                rol
        );
        bienvenida.setAlignment(Pos.CENTER_LEFT);
        bienvenida.setPadding(new Insets(30, 35, 25, 35));


        // PANEL DE OPCIONES
        Label titulo_opciones = new Label("MENÚ PRINCIPAL");
        titulo_opciones.setTextFill(Color.web(TEXTO));
        titulo_opciones.setFont(Font.font("Arial", 20));
        titulo_opciones.setStyle("-fx-font-weight: bold;");

        Label subtitulo_opciones = new Label(
                "Selecciona un módulo para continuar"
        );
        subtitulo_opciones.setTextFill(Color.web(SECUNDARIO));
        subtitulo_opciones.setFont(Font.font("Arial", 13));

        VBox encabezado_opciones = new VBox(
                7, titulo_opciones, subtitulo_opciones
        );
        encabezado_opciones.setAlignment(Pos.CENTER_LEFT);


GridPane opciones = new GridPane();
opciones.setAlignment(Pos.CENTER);
opciones.setHgap(12);
opciones.setVgap(10);
opciones.setMaxWidth(510);
opciones.setPadding(new Insets(16));
opciones.setStyle(
        "-fx-background-color: rgba(16,24,39,0.88);"
        + "-fx-background-radius: 16;"
        + "-fx-border-color: " + BORDE + ";"
        + "-fx-border-radius: 16;"
);


        if (administrador) {

            agregar_boton(opciones, "Gestionar usuarios");
            agregar_boton(opciones, "Gestionar artistas");
            agregar_boton(opciones, "Gestionar conciertos");
            agregar_boton(opciones, "Gestionar localidades");
            agregar_boton(opciones, "Gestionar inventario");
            agregar_boton(opciones, "Consultar ventas");
            agregar_boton(opciones, "Reportes");
            agregar_boton(opciones, "Validar boletos");

        } else if (sesion.es_vendedor()) {

            agregar_boton(opciones, "Vender boletos");
            agregar_boton(opciones, "Consultar ventas");
        }

VBox centro = new VBox(
        10, bienvenida, encabezado_opciones, opciones
);
centro.setAlignment(Pos.TOP_CENTER);
centro.setPadding(new Insets(0, 20, 6, 20));

bienvenida.setPadding(new Insets(2, 35, 2, 35));

        // PIE DE PÁGINA
        Label pie_texto = new Label(
                "BANGBOO-TICKET  •  MÁS MÚSICA, MÁS MOMENTOS"
        );
        pie_texto.setMinHeight(28);
        pie_texto.setMaxHeight(28);
        pie_texto.setTextFill(Color.web(SECUNDARIO));
        pie_texto.setFont(Font.font("Arial", 10));
        pie_texto.setAlignment(Pos.CENTER);

        BorderPane.setAlignment(pie_texto, Pos.CENTER);
BorderPane.setMargin(pie_texto, new Insets(2, 15, 6, 15));
// VENTANA PRINCIPAL
BorderPane contenido = new BorderPane();

contenido.setTop(encabezado);

ScrollPane desplazamiento = new ScrollPane(centro);
desplazamiento.setFitToWidth(true);
desplazamiento.setFitToHeight(false);

desplazamiento.setHbarPolicy(
        ScrollPane.ScrollBarPolicy.NEVER
);

desplazamiento.setStyle(
        "-fx-background: transparent;"
        + "-fx-background-color: transparent;"
);

// CONTENEDOR DEL FONDO
StackPane fondo_menu = new StackPane();

// IMAGEN DE FONDO
Image imagen_fondo = new Image(
        getClass().getResource(
                "/imagenes/fondo_login.png"
        ).toExternalForm()
);

ImageView fondo = new ImageView(imagen_fondo);

fondo.setPreserveRatio(false);
fondo.setSmooth(true);

fondo.fitWidthProperty().bind(
        fondo_menu.widthProperty()
);

fondo.fitHeightProperty().bind(
        fondo_menu.heightProperty()
);

// CAPA OSCURA PARA MEJORAR LA VISIBILIDAD
Rectangle capa_oscura = new Rectangle();

capa_oscura.widthProperty().bind(
        fondo_menu.widthProperty()
);

capa_oscura.heightProperty().bind(
        fondo_menu.heightProperty()
);

capa_oscura.setFill(
        Color.rgb(8, 13, 24, 0.68)
);

// CONTENIDO TRANSPARENTE
contenido.setCenter(desplazamiento);

contenido.setStyle(
        "-fx-background-color: transparent;"
);

// CAPAS DEL MENÚ
fondo_menu.getChildren().addAll(
        fondo,
        capa_oscura,
        contenido
);

// ESCENA
Scene escena = new Scene(fondo_menu, 950, 720);

ventana.setTitle(
        "BANGBOO-TICKET | "
        + (administrador ? "Administrador" : "Vendedor")
);

ventana.setScene(escena);

ventana_utilidad.configurar(ventana);
ventana.show();
    }

    private void agregar_separador(VBox contenedor) {

        Label separador = new Label(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );
        separador.setTextFill(Color.web(NARANJA));
        separador.setPadding(new Insets(2, 0, 8, 0));

        contenedor.getChildren().add(separador);
    }

    private void agregar_boton(GridPane contenedor, String texto) {

        Button boton = new Button(texto);
boton.setPrefWidth(235);
boton.setMinWidth(235);
boton.setMaxWidth(235);
boton.setPrefHeight(52);
boton.setMinHeight(52);
boton.setMaxHeight(52);
        boton.setAlignment(Pos.CENTER_LEFT);
        boton.setPadding(new Insets(10, 18, 10, 18));

        aplicar_estilo_boton(boton, false);

        boton.setOnMouseEntered(evento ->
                aplicar_estilo_boton(boton, true)
        );

        boton.setOnMouseExited(evento ->
                aplicar_estilo_boton(boton, false)
        );

        boton.setOnAction(evento -> {

            switch (texto) {

                case "Gestionar usuarios":
                    if (sesion.es_administrador()) {
                        new gestionar_usuarios(
                                ventana, sesion
                        ).mostrar();
                    }
                    break;
                    
                case "Validar boletos":
                    new escanear_boleto(
                        ventana, sesion
                    ).mostrar();
                    break;

                case "Gestionar artistas":
                    if (sesion.es_administrador()) {
                        new gestionar_artistas(
                                ventana, sesion
                        ).mostrar();
                    }
                    break;

                case "Gestionar conciertos":
                    if (sesion.es_administrador()) {
                        new gestionar_conciertos(
                                ventana, sesion
                        ).mostrar();
                    }
                    break;

                case "Gestionar localidades":
                    if (sesion.es_administrador()) {
                        new gestionar_localidades(
                                ventana, sesion
                        ).mostrar();
                    }
                    break;

                case "Gestionar inventario":
                    if (sesion.es_administrador()) {
                        new gestionar_inventario(
                                ventana, sesion
                        ).mostrar();
                    }
                    break;

                case "Vender boletos":
                    if (sesion.es_vendedor()) {
                        new vender_boletos(
                                ventana, sesion
                        ).mostrar();
                    }
                    break;

                case "Consultar ventas":
                    new consultar_ventas(
                            ventana, sesion
                    ).mostrar();
                    break;

                case "Reportes":
                    if (sesion.es_administrador()) {
                        new ver_reportes(
                                ventana, sesion
                        ).mostrar();
                    }
                    break;

                default:
                    break;
            }
        });

int indice = contenedor.getChildren().size();
GridPane.setHgrow(boton, javafx.scene.layout.Priority.ALWAYS);
contenedor.add(boton, indice % 2, indice / 2);
    }

    private void aplicar_estilo_boton(
            Button boton,
            boolean resaltado) {

        String fondo = resaltado ? AZUL : "#151F31";
        String borde = resaltado ? NARANJA : "#344563";
        String color_texto = resaltado ? "#FFFFFF" : TEXTO;

        boton.setStyle(
                "-fx-background-color: " + fondo + ";"
                + "-fx-text-fill: " + color_texto + ";"
                + "-fx-font-family: Arial;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-border-color: " + borde + ";"
                + "-fx-border-width: 1;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
    }
}
