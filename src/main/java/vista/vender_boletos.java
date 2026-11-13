
package vista;

import dao.inventario_boletos_dao;
import dao.concierto_dao;
import dao.localidad_dao;

import modelo.inventario_boletos;
import modelo.concierto;
import modelo.localidad;

import servicio.sesion_usuario;
import servicio.venta_servicio;
import utilidades.ventana_utilidad;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilidades.encabezado_utilidad;
import javafx.scene.layout.HBox;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

import java.time.format.DateTimeFormatter;

public class vender_boletos {

    private final Stage ventana;
    private final sesion_usuario sesion;

    private final inventario_boletos_dao inventario_dao =
            new inventario_boletos_dao();

    private final concierto_dao concierto_dao =
            new concierto_dao();

    private final localidad_dao localidad_dao =
            new localidad_dao();

    private final venta_servicio venta_servicio =
            new venta_servicio();

    private final ComboBox<opcion_inventario> selector_inventario =
            new ComboBox<>();

    private final Spinner<Integer> selector_cantidad =
            new Spinner<>();

    private final Label texto_precio = new Label("Q0.00");
    private final Label texto_disponibles = new Label("0");
    private final Label texto_total = new Label("Q0.00");

    private final Button boton_confirmar =
            new Button("CONFIRMAR COMPRA");

    private static final String FONDO = "#080D18";
    private static final String PANEL = "#101827";
    private static final String AZUL = "#2855D9";
    private static final String NARANJA = "#F07832";
    private static final String TEXTO = "#F3F1EA";
    private static final String SECUNDARIO = "#9BAAC4";

    public vender_boletos(
            Stage ventana,
            sesion_usuario sesion) {

        this.ventana = ventana;
        this.sesion = sesion;
    }
    
private ImageView vista_cartel;
private MediaView vista_video;
private MediaPlayer reproductor;

private final Label texto_hora =
        new Label("HORA DE INICIO\n--:--");

private Label etiqueta_video;

private Button boton_reproducir;
private Button boton_detener;
private Button boton_silencio;

private Slider control_volumen;

private boolean sonido_silenciado = true;

    public void mostrar() {


HBox encabezado_logo = encabezado_utilidad.crear(
        "BANGBOO-TICKET"
);

encabezado_logo.setMinHeight(116);
encabezado_logo.setPrefHeight(116);

Button boton_volver = new Button("← VOLVER AL MENÚ");
estilizar_boton_secundario(boton_volver);

boton_volver.setOnAction(evento -> {

    detener_reproductor();

    new menu_principal(ventana, sesion).mostrar();
});

BorderPane encabezado = new BorderPane();
encabezado.setLeft(encabezado_logo);
encabezado.setRight(boton_volver);

encabezado.setMinHeight(136);
encabezado.setPrefHeight(136);

encabezado.setPadding(new Insets(10, 35, 10, 35));

encabezado.setStyle(
        "-fx-background-color: " + PANEL + ";"
        + "-fx-border-color: #263550;"
        + "-fx-border-width: 0 0 1 0;"
);

        crear_previsualizaciones();
        
        Label titulo = new Label("Registrar nueva venta");
        titulo.setTextFill(Color.web(TEXTO));
        titulo.setFont(Font.font("Arial", 23));

        Label descripcion = new Label(
                "Selecciona el concierto y la cantidad de boletos."
        );
        descripcion.setTextFill(Color.web(SECUNDARIO));
        descripcion.setWrapText(true);

        Label etiqueta_inventario =
                crear_etiqueta("CONCIERTO Y LOCALIDAD");

        selector_inventario.setMaxWidth(Double.MAX_VALUE);
        selector_inventario.setPrefHeight(45);
        selector_inventario.setPromptText(
                "Selecciona una opción disponible"
        );

        selector_inventario.setStyle(
        "-fx-background-color: #1C2940;"
        + "-fx-border-color: #344563;"
        + "-fx-border-radius: 7;"
        + "-fx-background-radius: 7;"
        + "-fx-font-size: 14px;"
);

selector_inventario.setButtonCell(
        new javafx.scene.control.ListCell<opcion_inventario>() {

    @Override
    protected void updateItem(
            opcion_inventario item,
            boolean vacio) {

        super.updateItem(item, vacio);

        setText(vacio || item == null
                ? "Selecciona una opción disponible"
                : item.toString());

        setTextFill(Color.web(TEXTO));

        setStyle(
                "-fx-background-color: #1C2940;"
                + "-fx-text-fill: #F3F1EA;"
        );
    }
});

selector_inventario.setCellFactory(lista ->
        new javafx.scene.control.ListCell<opcion_inventario>() {

    @Override
    protected void updateItem(
            opcion_inventario item,
            boolean vacio) {

        super.updateItem(item, vacio);

        setText(vacio || item == null
                ? null
                : item.toString());

        setTextFill(Color.web(TEXTO));

        setStyle(
                "-fx-background-color: #1C2940;"
                + "-fx-text-fill: #F3F1EA;"
                + "-fx-padding: 10px;"
        );
    }
});

selector_inventario.setCellFactory(lista ->
        new javafx.scene.control.ListCell<opcion_inventario>() {

    @Override
    protected void updateItem(
            opcion_inventario item,
            boolean vacio) {

        super.updateItem(item, vacio);

        setText(vacio || item == null
                ? null
                : item.toString());

        setTextFill(Color.web(TEXTO));

        setStyle(
                "-fx-background-color: #1C2940;"
                + "-fx-text-fill: #F4F7FC;"
                + "-fx-padding: 10px;"
        );
    }
});

        Label etiqueta_precio = crear_etiqueta("PRECIO POR BOLETO");

        texto_precio.setTextFill(Color.web(NARANJA));
        texto_precio.setFont(Font.font("Arial", 20));

        Label etiqueta_disponibles =
                crear_etiqueta("BOLETOS DISPONIBLES");

        texto_disponibles.setTextFill(Color.web(TEXTO));
        texto_disponibles.setFont(Font.font("Arial", 16));

        Label etiqueta_cantidad = crear_etiqueta("CANTIDAD");

        selector_cantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, 1, 1
                )
        );

        selector_cantidad.setPrefWidth(150);
        selector_cantidad.setPrefHeight(42);
        selector_cantidad.setEditable(false);
        selector_cantidad.setStyle(
        "-fx-background-color: #1C2940;"
        + "-fx-border-color: #34445F;"
        + "-fx-border-radius: 7;"
        + "-fx-background-radius: 7;"
        + "-fx-font-size: 14px;"
);
        selector_cantidad.setStyle(
        "-fx-background-color: #1C2940;"
        + "-fx-border-color: #344563;"
        + "-fx-border-radius: 7;"
        + "-fx-background-radius: 7;"
        + "-fx-font-size: 14px;"
        + "-fx-text-fill: #F3F1EA;"
);

selector_cantidad.getEditor().setStyle(
        "-fx-background-color: #1C2940;"
        + "-fx-text-fill: #F4F7FC;"
        + "-fx-font-weight: bold;"
);

        Label etiqueta_total = crear_etiqueta("TOTAL A PAGAR");

        texto_total.setTextFill(Color.web(NARANJA));
        texto_total.setFont(Font.font("Arial", 32));

        boton_confirmar.setMaxWidth(Double.MAX_VALUE);
        boton_confirmar.setPrefHeight(50);
        boton_confirmar.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );

        boton_confirmar.setDisable(true);

        boton_confirmar.setOnAction(evento ->
                confirmar_compra()
        );

        selector_inventario.setOnAction(evento ->
                actualizar_seleccion()
        );

        selector_cantidad.valueProperty().addListener(
                (observable, anterior, actual) ->
                        actualizar_total()
        );

        VBox formulario = new VBox(
                12,
                titulo,
                descripcion,
                etiqueta_inventario,
                selector_inventario,
                etiqueta_precio,
                texto_precio,
                etiqueta_disponibles,
                texto_disponibles,
                etiqueta_cantidad,
                selector_cantidad,
                etiqueta_total,
                texto_total,
                boton_confirmar
        );

        formulario.setPadding(new Insets(30));
        formulario.setMaxWidth(650);
        formulario.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #263550;"
                + "-fx-border-radius: 14;"
        );

        // PANEL DEL CARTEL
texto_hora.setTextFill(Color.web(NARANJA));
texto_hora.setFont(Font.font("Arial", 15));
texto_hora.setAlignment(Pos.CENTER);
texto_hora.setStyle(
        "-fx-font-weight: bold;"
        + "-fx-padding: 10 5 5 5;"
);

VBox panel_cartel = new VBox(
        12,
        crear_titulo_multimedia("CARTEL DEL CONCIERTO"),
        vista_cartel,
        texto_hora
);

panel_cartel.setAlignment(Pos.TOP_CENTER);
panel_cartel.setPrefWidth(230);
panel_cartel.setMinWidth(200);

// PANEL DEL VIDEO
VBox panel_video = new VBox(
        12,
        crear_titulo_multimedia("VIDEO PROMOCIONAL"),
        crear_contenedor_video(),
        crear_controles_video()
);

panel_video.setAlignment(Pos.TOP_CENTER);
panel_video.setPrefWidth(430);
panel_video.setMinWidth(350);

// PANEL MULTIMEDIA
HBox panel_multimedia = new HBox(
        18,
        panel_cartel,
        panel_video
);

panel_multimedia.setAlignment(Pos.TOP_CENTER);
panel_multimedia.setPadding(new Insets(20));
panel_multimedia.setStyle(
        "-fx-background-color: " + PANEL + ";"
        + "-fx-background-radius: 12;"
        + "-fx-border-color: #344563;"
        + "-fx-border-radius: 12;"
);

// DISTRIBUCIÓN DE LA PANTALLA
HBox contenido_central = new HBox(
        20,
        formulario,
        panel_multimedia
);

contenido_central.setAlignment(Pos.TOP_CENTER);
contenido_central.setPadding(new Insets(25));

HBox.setHgrow(panel_multimedia, javafx.scene.layout.Priority.ALWAYS);

VBox centro = new VBox(contenido_central);
centro.setAlignment(Pos.TOP_CENTER);
centro.setPadding(new Insets(20));
centro.setStyle(
        "-fx-background-color: " + FONDO + ";"
);

        BorderPane contenido = new BorderPane();
        contenido.setTop(encabezado);
        contenido.setCenter(centro);
        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        Scene escena = new Scene(contenido, 1350, 850);

        ventana.setTitle("BANGBOO-TICKET | Venta de boletos");
        ventana.setScene(escena);
        ventana_utilidad.configurar(ventana);
        ventana.show();

        cargar_inventario();
    }
    
    private void crear_previsualizaciones() {

    vista_cartel = new ImageView();
    vista_cartel.setFitWidth(210);
    vista_cartel.setFitHeight(290);
    vista_cartel.setPreserveRatio(true);
    vista_cartel.setSmooth(true);

    vista_video = new MediaView();
    vista_video.setFitWidth(400);
    vista_video.setFitHeight(270);
    vista_video.setPreserveRatio(true);
    vista_video.setSmooth(false);

    boton_reproducir = crear_boton_multimedia(
            "▶ REPRODUCIR", AZUL);

    boton_detener = crear_boton_multimedia(
            "■ DETENER", "#263550");

    boton_silencio = crear_boton_multimedia(
            "🔊 ACTIVAR AUDIO", "#263550");

    boton_reproducir.setDisable(true);
    boton_detener.setDisable(true);
    boton_silencio.setDisable(true);

    control_volumen = new Slider(0, 100, 65);
    control_volumen.setPrefWidth(90);
    control_volumen.setDisable(true);

    control_volumen.setStyle(
            "-fx-control-inner-background: " + NARANJA + ";"
    );

    control_volumen.valueProperty().addListener(
            (observable, anterior, nuevo) -> {

                if (reproductor != null) {
                    reproductor.setVolume(
                            nuevo.doubleValue() / 100.0
                    );
                }
            }
    );

    etiqueta_video = new Label(
            "Selecciona un concierto para ver su video."
    );

    etiqueta_video.setTextFill(Color.web(SECUNDARIO));
    etiqueta_video.setWrapText(true);

    boton_reproducir.setOnAction(evento -> {

        if (reproductor == null) {
            return;
        }

        if (reproductor.getStatus()
                == MediaPlayer.Status.PLAYING) {

            reproductor.pause();
            boton_reproducir.setText("▶ REANUDAR");
            etiqueta_video.setText("Video en pausa.");

        } else {

            reproductor.play();
            boton_reproducir.setText("⏸ PAUSAR");
        }
    });

    boton_detener.setOnAction(evento -> {

        if (reproductor != null) {

            reproductor.stop();
            boton_reproducir.setText("▶ REPRODUCIR");
            etiqueta_video.setText("Video detenido.");
        }
    });

    boton_silencio.setOnAction(evento -> {

        if (reproductor == null) {
            return;
        }

        sonido_silenciado = !sonido_silenciado;

        reproductor.setMute(sonido_silenciado);

        boton_silencio.setText(
                sonido_silenciado
                ? "🔊 ACTIVAR AUDIO"
                : "🔇 SILENCIAR"
        );
    });
}

private Label crear_titulo_multimedia(String texto) {

    Label titulo = new Label(texto);

    titulo.setTextFill(Color.web(NARANJA));
    titulo.setFont(Font.font("Arial", 15));
    titulo.setAlignment(Pos.CENTER);

    return titulo;
}

private StackPane crear_contenedor_video() {

    StackPane contenedor = new StackPane(vista_video);

    contenedor.setMinHeight(270);
    contenedor.setPrefHeight(270);
    contenedor.setMaxHeight(270);

    contenedor.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-background-radius: 8;"
            + "-fx-border-color: #344563;"
            + "-fx-border-radius: 8;"
    );

    return contenedor;
}

private HBox crear_controles_video() {

    boton_reproducir.setPrefHeight(35);
    boton_detener.setPrefHeight(35);
    boton_silencio.setPrefHeight(35);

    HBox controles = new HBox(
            7,
            boton_reproducir,
            boton_detener,
            boton_silencio,
            control_volumen
    );

    controles.setAlignment(Pos.CENTER);
    controles.setPadding(new Insets(8));
    controles.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-background-radius: 8;"
    );

    return controles;
}

private Button crear_boton_multimedia(
        String texto,
        String color) {

    Button boton = new Button(texto);

    boton.setStyle(
            "-fx-background-color: " + color + ";"
            + "-fx-text-fill: white;"
            + "-fx-font-size: 11px;"
            + "-fx-font-weight: bold;"
            + "-fx-background-radius: 6;"
            + "-fx-cursor: hand;"
    );

    return boton;
}

private void mostrar_cartel(String ruta) {

    vista_cartel.setImage(null);

    if (ruta == null || ruta.isBlank()) {
        return;
    }

    File archivo = new File(ruta);

    if (!archivo.isFile()) {
        return;
    }

    try {

        vista_cartel.setImage(
                new Image(archivo.toURI().toString())
        );

    } catch (Exception ex) {

        vista_cartel.setImage(null);
    }
}

private void reproducir_video(String ruta) {

    detener_reproductor();

    if (ruta == null || ruta.isBlank()) {

        etiqueta_video.setText(
                "Este concierto no tiene video promocional."
        );

        return;
    }

    File archivo = new File(ruta);

    if (!archivo.isFile()) {

        etiqueta_video.setText(
                "No se encontró el video promocional."
        );

        return;
    }

    try {

        Media media = new Media(
                archivo.toURI().toString()
        );

        MediaPlayer nuevo_reproductor =
                new MediaPlayer(media);

        reproductor = nuevo_reproductor;

        vista_video.setMediaPlayer(nuevo_reproductor);

        nuevo_reproductor.setVolume(
                control_volumen.getValue() / 100.0
        );

        nuevo_reproductor.setMute(true);
        sonido_silenciado = true;

        boton_silencio.setText("🔊 ACTIVAR AUDIO");

        etiqueta_video.setText(
                "Cargando video promocional..."
        );

        nuevo_reproductor.setOnReady(() -> {

            if (reproductor != nuevo_reproductor) {
                return;
            }

            nuevo_reproductor.pause();

            boton_reproducir.setDisable(false);
            boton_detener.setDisable(false);
            boton_silencio.setDisable(false);
            control_volumen.setDisable(false);

            boton_reproducir.setText("▶ REPRODUCIR");

            etiqueta_video.setText(
                    "Video listo. Presiona REPRODUCIR."
            );
        });

        nuevo_reproductor.setOnError(() -> {

            if (reproductor == nuevo_reproductor) {

                etiqueta_video.setText(
                        "No se pudo reproducir el video."
                );
            }
        });

        nuevo_reproductor.setOnEndOfMedia(() ->
                boton_reproducir.setText("▶ REPRODUCIR")
        );

    } catch (Exception ex) {

        detener_reproductor();

        etiqueta_video.setText(
                "No se pudo abrir el video promocional."
        );
    }
}

private void detener_reproductor() {

    if (reproductor != null) {

        reproductor.stop();
        reproductor.dispose();

        reproductor = null;
    }

    if (vista_video != null) {
        vista_video.setMediaPlayer(null);
    }

    if (boton_reproducir != null) {
        boton_reproducir.setText("▶ REPRODUCIR");
        boton_reproducir.setDisable(true);
    }

    if (boton_detener != null) {
        boton_detener.setDisable(true);
    }

    if (boton_silencio != null) {
        boton_silencio.setText("🔇 SILENCIO");
        boton_silencio.setDisable(true);
    }

    if (control_volumen != null) {
        control_volumen.setDisable(true);
    }

    sonido_silenciado = true;
}

    private void cargar_inventario() {

        try {
            List<concierto> conciertos =
                    concierto_dao.listar_conciertos();

            List<localidad> localidades =
                    localidad_dao.listar_localidades();

            List<inventario_boletos> inventarios =
                    inventario_dao.listar_inventario();

            Map<Integer, concierto> mapa_conciertos =
                    new HashMap<>();

            for (concierto actual : conciertos) {
                mapa_conciertos.put(
                        actual.getId_concierto(), actual
                );
            }

            Map<Integer, localidad> mapa_localidades =
                    new HashMap<>();

            for (localidad actual : localidades) {
                mapa_localidades.put(
                        actual.getId_localidad(), actual
                );
            }

            selector_inventario.getItems().clear();

            for (inventario_boletos inventario : inventarios) {

                if (inventario.getCantidad_disponible() <= 0) {
                    continue;
                }

                concierto evento = mapa_conciertos.get(
                        inventario.getId_concierto()
                );

                localidad zona = mapa_localidades.get(
                        inventario.getId_localidad()
                );

                if (evento == null || zona == null) {
                    continue;
                }

                String estado = evento.getEstado();

                if (!"programado".equals(estado)
                        && !"activo".equals(estado)) {
                    continue;
                }

                String hora_concierto =
        evento.getHora_concierto() == null
        ? "Por confirmar"
        : evento.getHora_concierto()
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

selector_inventario.getItems().add(
        new opcion_inventario(
                inventario,
                evento.getTitulo_evento(),
                zona.getNombre(),
                evento.getRuta_cartel(),
                evento.getRuta_video(),
                hora_concierto
        )
);
            }

            if (!selector_inventario.getItems().isEmpty()) {

    selector_inventario.getSelectionModel()
            .selectFirst();

    actualizar_seleccion();

} else {

    actualizar_seleccion();
                mostrar_alerta(
                        Alert.AlertType.INFORMATION,
                        "Sin inventario disponible",
                        "No hay boletos disponibles para vender."
                );
            }

        } catch (SQLException | IOException ex) {

            mostrar_alerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar inventario",
                    ex.getMessage()
            );
        }
    }

    private void actualizar_seleccion() {

        opcion_inventario seleccion =
                selector_inventario.getValue();

        if (seleccion == null) {
            
    texto_hora.setText("HORA DE INICIO\n--:--");
    texto_precio.setText("Q0.00");
    texto_disponibles.setText("0");
    texto_total.setText("Q0.00");

    vista_cartel.setImage(null);

    detener_reproductor();

    etiqueta_video.setText(
            "Selecciona un concierto para ver su video."
    );

    boton_confirmar.setDisable(true);

    return;
}

        inventario_boletos inventario =
                seleccion.inventario;
        
        texto_hora.setText(
        "HORA DE INICIO\n" + seleccion.hora_concierto
);
        
        mostrar_cartel(seleccion.ruta_cartel);
reproducir_video(seleccion.ruta_video);

        texto_precio.setText(
                formato_dinero(inventario.getPrecio())
        );

        texto_disponibles.setText(
                String.valueOf(
                        inventario.getCantidad_disponible()
                )
        );

        int disponibles = inventario.getCantidad_disponible();

        selector_cantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        Math.max(1, disponibles),
                        1
                )
        );

        selector_cantidad.setDisable(disponibles <= 0);
        boton_confirmar.setDisable(disponibles <= 0);

        actualizar_total();
    }


private void actualizar_total() {

    opcion_inventario seleccion =
            selector_inventario.getValue();

    if (seleccion == null) {
        texto_total.setText("Q0.00");
        return;
    }

    BigDecimal total = seleccion.inventario.getPrecio()
            .multiply(
                    BigDecimal.valueOf(
                            selector_cantidad.getValue()
                    )
            );

    texto_total.setText(formato_dinero(total));
}



private void confirmar_compra() {

    opcion_inventario seleccion =
            selector_inventario.getValue();

    if (seleccion == null) {
        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Selecciona una localidad",
                "Debes seleccionar un concierto y una localidad."
        );
        return;
    }

    int cantidad = selector_cantidad.getValue();

    BigDecimal precio =
            seleccion.inventario.getPrecio();

    BigDecimal total_compra =
            precio.multiply(BigDecimal.valueOf(cantidad));

    boton_confirmar.setDisable(true);

    try {
        long id_venta = venta_servicio.registrar_compra(
                sesion,
                seleccion.inventario.getId_inventario(),
                cantidad
        );
        
        detener_reproductor();

        new boletos_generados(
                ventana,
                sesion,
                id_venta,
                seleccion.nombre_concierto,
                seleccion.nombre_localidad,
                precio,
                cantidad,
                total_compra
        ).mostrar();

    } catch (SQLException | IOException
            | RuntimeException ex) {

        mostrar_alerta(
                Alert.AlertType.ERROR,
                "No se pudo completar la venta",
                ex.getMessage() == null
                        ? "Ocurrió un error al registrar la compra."
                        : ex.getMessage()
        );

    } finally {
        boton_confirmar.setDisable(
                selector_inventario.getValue() == null
                || selector_inventario.getValue()
                        .inventario.getCantidad_disponible() <= 0
        );
    }
}


    private Label crear_etiqueta(String texto) {

        Label etiqueta = new Label(texto);
        etiqueta.setTextFill(Color.web(SECUNDARIO));
        etiqueta.setFont(Font.font("Arial", 11));

        return etiqueta;
    }

    private String formato_dinero(BigDecimal cantidad) {
        return "Q" + cantidad.setScale(
                2, java.math.RoundingMode.HALF_UP
        ).toPlainString();
    }

    private void estilizar_boton_secundario(Button boton) {

        boton.setPrefHeight(40);
        boton.setStyle(
                "-fx-background-color: #263550;"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 7;"
                + "-fx-cursor: hand;"
        );
    }

    private void mostrar_alerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

   private static class opcion_inventario {
       
    private final String hora_concierto;

    private final inventario_boletos inventario;

    private final String nombre_concierto;
    private final String nombre_localidad;

    private final String ruta_cartel;
    private final String ruta_video;

    private opcion_inventario(
        inventario_boletos inventario,
        String nombre_concierto,
        String nombre_localidad,
        String ruta_cartel,
        String ruta_video,
        String hora_concierto) {

    this.inventario = inventario;

    this.nombre_concierto = nombre_concierto;
    this.nombre_localidad = nombre_localidad;

    this.ruta_cartel = ruta_cartel;
    this.ruta_video = ruta_video;
    this.hora_concierto = hora_concierto;
}

    @Override
    public String toString() {

        return nombre_concierto
                + " | " + nombre_localidad
                + " | Q"
                + inventario.getPrecio().toPlainString()
                + " | Disponibles: "
                + inventario.getCantidad_disponible();
    }
}
}
