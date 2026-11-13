
package vista;

import dao.artista_dao;
import dao.concierto_dao;
import modelo.artista;
import modelo.concierto;
import servicio.sesion_usuario;
import utilidades.encabezado_utilidad;
import utilidades.ventana_utilidad;
import utilidades.gestor_archivos;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;



public class gestionar_conciertos {

    private final Stage ventana;
    private final sesion_usuario sesion;

    private final concierto_dao dao = new concierto_dao();
    private final artista_dao dao_artistas = new artista_dao();

    private final TableView<concierto> tabla = new TableView<>();
    private final ComboBox<artista> selector_artistas = new ComboBox<>();

    private TextField campo_titulo;
    private DatePicker campo_fecha;
    private TextField campo_hora;
    private TextField campo_recinto;
    private ComboBox<String> campo_estado;
    private TextField campo_cartel;
    private TextField campo_video;

    private Button boton_registrar;
    private Button boton_actualizar;
    private Button boton_eliminar;
    private Button boton_limpiar;

    private ImageView vista_cartel;
    private MediaView vista_video;
    private MediaPlayer reproductor;

    private Button boton_reproducir;
    private Button boton_detener;
    private Button boton_silencio;
    private Slider control_volumen;

    private Label etiqueta_video;

    private boolean sonido_silenciado = true;

    private static final String FONDO = "#080D18";
    private static final String PANEL = "#101827";
    private static final String AZUL = "#2855D9";
    private static final String NARANJA = "#F07832";
    private static final String TEXTO = "#F3F1EA";
    private static final String SECUNDARIO = "#9BAAC4";

    public gestionar_conciertos(
            Stage ventana,
            sesion_usuario sesion) {

        this.ventana = ventana;
        this.sesion = sesion;
    }

    public void mostrar() {

        if (!sesion.es_administrador()) {
            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Acceso denegado",
                    "Solo el administrador puede gestionar conciertos."
            );

            new menu_principal(ventana, sesion).mostrar();
            return;
        }

        // ENCABEZADO
        Label subtitulo = new Label(
                "Programa eventos y administra el calendario musical"
        );
        subtitulo.setTextFill(Color.web(SECUNDARIO));

        Button volver = new Button("← VOLVER AL MENÚ");
        estilizar_boton_secundario(volver);

        volver.setOnAction(evento -> {
            detener_reproductor();
            new menu_principal(ventana, sesion).mostrar();
        });

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox encabezado = new HBox(
                18,
                encabezado_utilidad.crear("GESTIÓN DE CONCIERTOS"),
                subtitulo,
                espacio,
                volver
        );

        encabezado.setAlignment(Pos.CENTER_LEFT);
encabezado.setPadding(new Insets(2, 25, 2, 25));
encabezado.setMinHeight(42);
encabezado.setPrefHeight(42);
encabezado.setMaxHeight(42);

encabezado.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-border-color: #263550;"
                + "-fx-border-width: 0 0 1 0;"
        );

        // FORMULARIO
        crear_campos();

        Label titulo_formulario = new Label("REGISTRAR CONCIERTO");
        titulo_formulario.setFont(Font.font("Arial", 16));
        titulo_formulario.setTextFill(Color.web(NARANJA));

        GridPane formulario = new GridPane();
formulario.setHgap(10);
formulario.setVgap(7);

        agregar_campo(
                formulario, "Título del evento:",
                campo_titulo, 0
        );

        agregar_campo(
                formulario, "Artista:",
                selector_artistas, 1
        );

        agregar_campo(
                formulario, "Fecha:",
                campo_fecha, 2
        );
        
        
        agregar_campo(
                formulario, "Hora:",
                campo_hora, 3
        );

        agregar_campo(
                formulario, "Recinto:",
                campo_recinto, 4
        );

        agregar_campo(
                formulario, "Estado:",
                campo_estado, 5
        );

        HBox seleccion_cartel = new HBox(
                8,
                campo_cartel,
                crear_boton_archivo("ELEGIR CARTEL", true)
        );

        HBox seleccion_video = new HBox(
                8,
                campo_video,
                crear_boton_archivo("ELEGIR VIDEO", false)
        );

        agregar_campo(
                formulario, "Cartel:",
                seleccion_cartel, 6
        );

        agregar_campo(
                formulario, "Video promocional:",
                seleccion_video, 7
        );

// BOTÓN REGISTRAR
boton_registrar = new Button("REGISTRAR CONCIERTO");
estilizar_boton_principal(boton_registrar);

boton_registrar.setOnAction(evento ->
        registrar_concierto()
);

// BOTÓN ACTUALIZAR
boton_actualizar = new Button("ACTUALIZAR CONCIERTO");
estilizar_boton_naranja(boton_actualizar);

boton_actualizar.setOnAction(evento ->
        actualizar_concierto()
);

// BOTÓN ELIMINAR
boton_eliminar = new Button("ELIMINAR CONCIERTO");
boton_eliminar.setMaxWidth(Double.MAX_VALUE);
boton_eliminar.setPrefHeight(40);

boton_eliminar.setStyle(
        "-fx-background-color: #A83232;"
        + "-fx-text-fill: white;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 6;"
        + "-fx-cursor: hand;"
);

boton_eliminar.setOnAction(evento ->
        eliminar_concierto()
);

// BOTÓN LIMPIAR FORMULARIO
boton_limpiar = new Button("LIMPIAR FORMULARIO");
boton_limpiar.setMaxWidth(Double.MAX_VALUE);
boton_limpiar.setPrefHeight(40);

boton_limpiar.setStyle(
        "-fx-background-color: #263550;"
        + "-fx-text-fill: white;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 6;"
        + "-fx-cursor: hand;"
);

boton_limpiar.setOnAction(evento ->
        limpiar_formulario()
);

// BOTONES DE ACCIÓN
HBox botones_accion = new HBox(
        10,
        boton_registrar,
        boton_actualizar
);

botones_accion.setAlignment(Pos.CENTER);
botones_accion.setFillHeight(true);

boton_registrar.setMaxWidth(Double.MAX_VALUE);
boton_actualizar.setMaxWidth(Double.MAX_VALUE);

HBox.setHgrow(boton_registrar, Priority.ALWAYS);
HBox.setHgrow(boton_actualizar, Priority.ALWAYS);

boton_registrar.setPrefWidth(0);
boton_actualizar.setPrefWidth(0);

// BOTONES ELIMINAR Y LIMPIAR
HBox botones_secundarios = new HBox(
        10,
        boton_eliminar,
        boton_limpiar
);

botones_secundarios.setAlignment(Pos.CENTER);

boton_eliminar.setMaxWidth(Double.MAX_VALUE);
boton_limpiar.setMaxWidth(Double.MAX_VALUE);

HBox.setHgrow(boton_eliminar, Priority.ALWAYS);
HBox.setHgrow(boton_limpiar, Priority.ALWAYS);

boton_eliminar.setPrefWidth(0);
boton_limpiar.setPrefWidth(0);

// PANEL DEL FORMULARIO
VBox panel_formulario = new VBox(
        9,
        titulo_formulario,
        formulario,
        botones_accion,
        botones_secundarios
);

panel_formulario.setPadding(new Insets(20));
panel_formulario.setMaxWidth(Double.MAX_VALUE);
panel_formulario.setStyle(estilo_panel());
panel_formulario.setPadding(new Insets(20));
panel_formulario.setMaxWidth(Double.MAX_VALUE);
panel_formulario.setStyle(estilo_panel());

        // VISTA PREVIA
        crear_previsualizaciones();

        VBox panel_datos = new VBox(
                10,
                crear_titulo("CARTEL DEL CONCIERTO"),
                vista_cartel
        );

panel_datos.setAlignment(Pos.TOP_CENTER);
panel_datos.setPrefWidth(270);
panel_datos.setMinWidth(250);
panel_datos.setMaxWidth(280);

VBox panel_reproductor = new VBox(
        13,
        crear_titulo("VIDEO PROMOCIONAL"),
        crear_contenedor(vista_video),
        crear_controles_video()
);

panel_reproductor.setAlignment(Pos.TOP_CENTER);
panel_reproductor.setPrefWidth(480);
panel_reproductor.setMinWidth(430);
panel_reproductor.setMaxWidth(520);



        HBox contenido_previsualizacion = new HBox(
                9,
                panel_datos,
                panel_reproductor
        );

        contenido_previsualizacion.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(panel_reproductor, Priority.ALWAYS);

        Label titulo_previsualizacion =
                crear_titulo("VISTA PREVIA DEL CONCIERTO");
        
        titulo_previsualizacion.setMaxWidth(Double.MAX_VALUE);
        titulo_previsualizacion.setAlignment(Pos.CENTER);

VBox panel_previsualizacion = new VBox(
        12,
        titulo_previsualizacion,
        contenido_previsualizacion
);

panel_previsualizacion.setPadding(new Insets(15, 20, 10, 20));
panel_previsualizacion.setMaxWidth(Double.MAX_VALUE);
panel_previsualizacion.setMaxHeight(Region.USE_PREF_SIZE);

panel_previsualizacion.setStyle(
        "-fx-background-color: " + PANEL + ";"
        + "-fx-background-radius: 10;"
        + "-fx-border-color: #344563;"
        + "-fx-border-width: 1;"
        + "-fx-border-radius: 10;"
);

        // DISTRIBUCIÓN SUPERIOR
        HBox zona_superior = new HBox(
                18,
                panel_formulario,
                panel_previsualizacion
        );

        zona_superior.setAlignment(Pos.TOP_LEFT);
zona_superior.setPrefHeight(500);
zona_superior.setMinHeight(480);
zona_superior.setMaxHeight(520);

        panel_formulario.setPrefWidth(440);
        panel_formulario.setMinWidth(360);
        HBox.setHgrow(panel_formulario, Priority.ALWAYS);

        panel_previsualizacion.setPrefWidth(560);
        panel_previsualizacion.setMinWidth(500);
        HBox.setHgrow(panel_previsualizacion, Priority.ALWAYS);
        
        
        
//        
        
        zona_superior.setAlignment(Pos.TOP_LEFT);
zona_superior.setFillHeight(false);

panel_previsualizacion.setMaxHeight(Region.USE_PREF_SIZE);
panel_previsualizacion.setMinHeight(Region.USE_PREF_SIZE);

        // TABLA
        Label titulo_tabla = crear_titulo(
                "CONCIERTOS REGISTRADOS"
        );

        Button actualizar_lista = new Button("ACTUALIZAR LISTA");
        estilizar_boton_principal(actualizar_lista);

        actualizar_lista.setOnAction(evento ->
                cargar_conciertos()
        );

        HBox barra_tabla = new HBox(
                15,
                titulo_tabla,
                actualizar_lista
        );

        barra_tabla.setAlignment(Pos.CENTER_LEFT);

        configurar_tabla();

        tabla.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargar_concierto_seleccionado(seleccionado);
                    }
                });

VBox panel_tabla = new VBox(
        8,
        barra_tabla,
        tabla
);

panel_tabla.setPadding(new Insets(15));
panel_tabla.setMinHeight(190);
panel_tabla.setStyle(estilo_panel());

VBox.setVgrow(tabla, Priority.ALWAYS);

        VBox.setVgrow(tabla, Priority.ALWAYS);

        // CENTRO
VBox centro = new VBox(
        14,
        zona_superior,
        panel_tabla
);

centro.setPadding(new Insets(10, 25, 10, 25));
VBox.setVgrow(panel_tabla, Priority.ALWAYS);

 

BorderPane contenido = new BorderPane();
contenido.setTop(encabezado);
contenido.setCenter(centro);
        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        Scene escena = new Scene(contenido, 1250, 800);

        ventana.setTitle("BANGBOO-TICKET | Gestión de conciertos");
        ventana.setScene(escena);

        ventana_utilidad.configurar(ventana);
        ventana.show();

        cargar_artistas();
        cargar_conciertos();
    }

    // CAMPOS DEL FORMULARIO
    private void crear_campos() {

        campo_titulo = new TextField();
        campo_titulo.setPromptText("Nombre del evento");

        selector_artistas.setPromptText("Selecciona un artista");
        selector_artistas.setMaxWidth(Double.MAX_VALUE);

        selector_artistas.setCellFactory(lista ->
                new ListCell<artista>() {
                    @Override
                    protected void updateItem(artista item, boolean vacio) {
                        super.updateItem(item, vacio);

                        setText(vacio || item == null
                                ? null
                                : item.getNombre_artistico());
                    }
                });

        selector_artistas.setButtonCell(
                new ListCell<artista>() {
                    @Override
                    protected void updateItem(artista item, boolean vacio) {
                        super.updateItem(item, vacio);

                        setText(vacio || item == null
                                ? "Selecciona un artista"
                                : item.getNombre_artistico());

                        setTextFill(Color.web(TEXTO));
                    }
                });

        campo_fecha = new DatePicker();
        campo_fecha.setValue(LocalDate.now().plusDays(30));
        campo_fecha.setMaxWidth(Double.MAX_VALUE);
        
        campo_hora = new TextField();
        campo_hora.setPromptText("Ej. 19:30");
        campo_hora.setMaxWidth(Double.MAX_VALUE);

        campo_recinto = new TextField();
        campo_recinto.setPromptText("Ej. Estadio Nacional");

        campo_estado = new ComboBox<>();
        campo_estado.getItems().addAll(
                "programado",
                "activo",
                "cancelado"
        );
        campo_estado.setValue("programado");
        campo_estado.setMaxWidth(Double.MAX_VALUE);
        
        campo_estado.setCellFactory(lista ->
        new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean vacio) {
                super.updateItem(item, vacio);

                setText(vacio || item == null ? null : item);
                setTextFill(Color.web(TEXTO));

                setStyle(
                        "-fx-background-color: #080D18;"
                        + "-fx-text-fill: white;"
                );
            }
        }
);

campo_estado.setButtonCell(
        new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean vacio) {
                super.updateItem(item, vacio);

                setText(vacio || item == null
                        ? "Selecciona un estado"
                        : item);

                setTextFill(Color.web(TEXTO));

                setStyle(
                        "-fx-background-color: #080D18;"
                        + "-fx-text-fill: white;"
                );
            }
        }
);

        campo_cartel = new TextField();
        campo_cartel.setPromptText("Selecciona un cartel...");
        campo_cartel.setEditable(false);

        campo_video = new TextField();
        campo_video.setPromptText("Selecciona un video...");
        campo_video.setEditable(false);

        estilizar_campo(campo_titulo);
        estilizar_campo(campo_recinto);
        estilizar_campo(campo_cartel);
        estilizar_campo(campo_video);
        estilizar_campo(campo_hora);

        selector_artistas.setStyle(estilo_combo());
        campo_estado.setStyle(estilo_combo());
    }

    // SELECTORES DE ARCHIVOS
    private Button crear_boton_archivo(
            String texto,
            boolean es_imagen) {

        Button boton = crear_boton(
                texto,
                es_imagen ? NARANJA : AZUL
        );

        boton.setOnAction(evento -> {

            FileChooser selector = new FileChooser();

            if (es_imagen) {
                selector.setTitle("Seleccionar cartel del concierto");
                selector.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(
                                "Imágenes",
                                "*.png", "*.jpg", "*.jpeg", "*.webp"
                        )
                );
            } else {
                selector.setTitle("Seleccionar video promocional");
                selector.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(
                                "Videos",
                                "*.mp4", "*.m4v"
                        )
                );
            }

            File archivo = selector.showOpenDialog(ventana);

            if (archivo == null) {
                return;
            }

            try {
                String ruta_copiada = gestor_archivos.copiar_archivo(
                        archivo,
                        es_imagen ? "carteles" : "videos"
                );

                if (es_imagen) {
                    campo_cartel.setText(ruta_copiada);
                    mostrar_cartel(ruta_copiada);
                } else {
                    campo_video.setText(ruta_copiada);
                    reproducir_video(ruta_copiada);
                }

            } catch (IOException ex) {
                mostrar_mensaje(
                        Alert.AlertType.ERROR,
                        "Error al copiar archivo",
                        obtener_mensaje_error(
                                ex,
                                "No se pudo guardar el archivo."
                        )
                );
            }
        });

        return boton;
    }

// VISTAS PREVIAS
private void crear_previsualizaciones() {

    // VISTA PREVIA DEL CARTEL
    vista_cartel = new ImageView();
    vista_cartel.setFitWidth(190);
    vista_cartel.setFitHeight(245);
    vista_cartel.setPreserveRatio(true);
    vista_cartel.setSmooth(true);

    // VISTA PREVIA DEL VIDEO
    vista_video = new MediaView();
vista_video.setFitWidth(420);
vista_video.setFitHeight(320);
    vista_video.setPreserveRatio(true);
    vista_video.setSmooth(false);

    // BOTONES DEL VIDEO
    boton_reproducir = crear_boton("▶ REPRODUCIR", AZUL);
    boton_detener = crear_boton("■ DETENER", "#263550");
    boton_silencio = crear_boton("🔇 ACTIVAR AUDIO", "#263550");

    boton_reproducir.setDisable(true);
    boton_detener.setDisable(true);
    boton_silencio.setDisable(true);

    // CONTROL DE VOLUMEN
    control_volumen = new Slider(0, 100, 65);
    control_volumen.setMinWidth(80);
    control_volumen.setPrefWidth(80);
    control_volumen.setMaxWidth(80);
    control_volumen.setShowTickLabels(true);
    control_volumen.setMajorTickUnit(25);
    control_volumen.setDisable(true);

    control_volumen.valueProperty().addListener(
            (observable, anterior, nuevo) -> {
                if (reproductor != null) {
                    reproductor.setVolume(
                            nuevo.doubleValue() / 100.0
                    );
                }
            }
    );

    // MENSAJE DEL VIDEO
    etiqueta_video = new Label(
            "Selecciona un concierto para ver su video."
    );

    etiqueta_video.setTextFill(Color.web(SECUNDARIO));
    etiqueta_video.setWrapText(true);

    // ACCIÓN REPRODUCIR / PAUSAR
    boton_reproducir.setOnAction(evento -> {

        if (reproductor == null) {
            return;
        }

        if (reproductor.getStatus() == MediaPlayer.Status.PLAYING) {
            reproductor.pause();
            boton_reproducir.setText("▶ REANUDAR");
            etiqueta_video.setText("Video en pausa.");
        } else {
            reproductor.play();
            boton_reproducir.setText("⏸ PAUSAR");
        }
    });

    // ACCIÓN DETENER
    boton_detener.setOnAction(evento -> {

        if (reproductor != null) {
            reproductor.stop();
            boton_reproducir.setText("▶ REPRODUCIR");
            etiqueta_video.setText("Video detenido.");
        }
    });

    // ACCIÓN SILENCIO
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

private HBox crear_controles_video() {

    boton_reproducir.setPrefHeight(38);
    boton_detener.setPrefHeight(38);
    boton_silencio.setPrefHeight(38);

    boton_reproducir.setPrefWidth(115);
    boton_detener.setPrefWidth(95);
    boton_silencio.setPrefWidth(125);

    // BARRA DE VOLUMEN
    control_volumen.setMinWidth(65);
    control_volumen.setPrefWidth(80);
    control_volumen.setMaxWidth(80);

    control_volumen.setShowTickLabels(true);
    control_volumen.setShowTickMarks(false);
    control_volumen.setMajorTickUnit(25);

    control_volumen.setStyle(
            "-fx-control-inner-background: " + NARANJA + ";"
    );

    HBox controles = new HBox(
            7,
            boton_reproducir,
            boton_detener,
            boton_silencio,
            control_volumen
    );

    controles.setAlignment(Pos.CENTER);
    controles.setPadding(new Insets(10));
    controles.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-background-radius: 8;"
            + "-fx-border-color: #344563;"
            + "-fx-border-radius: 8;"
    );

    return controles;
}

private StackPane crear_contenedor(Node elemento) {

    StackPane contenedor = new StackPane(elemento);

    contenedor.setMinHeight(320);
    contenedor.setPrefHeight(320);
    contenedor.setMaxHeight(320);

    contenedor.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-background-radius: 8;"
            + "-fx-border-color: #344563;"
            + "-fx-border-radius: 8;"
    );

    return contenedor;
}

    // REGISTRAR CONCIERTO
    private void registrar_concierto() {

        if (!validar_formulario()) {
            return;
        }

        concierto nuevo = obtener_concierto_del_formulario();

        try {
            dao.registrar_concierto(nuevo);

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "El concierto se registró correctamente."
            );

            limpiar_formulario();
            cargar_conciertos();

        } catch (SQLException | IOException
                | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al registrar",
                    obtener_mensaje_error(ex, "No se pudo registrar.")
            );
        }
    }

    // ACTUALIZAR CONCIERTO
    private void actualizar_concierto() {

        concierto seleccionado =
                tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un concierto",
                    "Selecciona un concierto de la tabla antes de actualizarlo."
            );
            return;
        }

        if (!validar_formulario()) {
            return;
        }

        int id_concierto = seleccionado.getId_concierto();

        concierto actualizado = obtener_concierto_del_formulario();
        actualizado.setId_concierto(id_concierto);

        try {
            dao.actualizar_concierto(actualizado);

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Actualización exitosa",
                    "Los datos del concierto se actualizaron correctamente."
            );

            limpiar_formulario();
            cargar_conciertos();

        } catch (SQLException | IOException
                | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al actualizar",
                    obtener_mensaje_error(ex, "No se pudo actualizar.")
            );
        }
    }
    
    // ELIMINAR CONCIERTO
private void eliminar_concierto() {

    concierto seleccionado =
            tabla.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        mostrar_mensaje(
                Alert.AlertType.WARNING,
                "Selecciona un concierto",
                "Selecciona un concierto de la tabla antes de eliminarlo."
        );
        return;
    }

    Alert confirmacion = new Alert(
            Alert.AlertType.CONFIRMATION
    );

    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText("¿Eliminar concierto?");
    confirmacion.setContentText(
            "¿Estás seguro de que deseas eliminar el concierto "
            + seleccionado.getTitulo_evento()
            + "?\n\nEsta acción no se puede deshacer."
    );

    confirmacion.getButtonTypes().setAll(
            ButtonType.YES,
            ButtonType.NO
    );

    ButtonType respuesta = confirmacion.showAndWait()
            .orElse(ButtonType.NO);

    if (respuesta != ButtonType.YES) {
        return;
    }

    try {
        dao.eliminar_concierto(
                seleccionado.getId_concierto()
        );

        detener_reproductor();

        limpiar_formulario();
        cargar_conciertos();

        mostrar_mensaje(
                Alert.AlertType.INFORMATION,
                "Eliminación exitosa",
                "El concierto se eliminó correctamente."
        );

    } catch (SQLException | IOException | IllegalArgumentException ex) {

        mostrar_mensaje(
                Alert.AlertType.ERROR,
                "No se pudo eliminar",
                obtener_mensaje_error(
                        ex,
                        "Ocurrió un error al eliminar el concierto."
                )
        );
    }
}

private boolean validar_formulario() {

    String nombre = campo_titulo.getText().trim();
    String recinto = campo_recinto.getText().trim();
    String hora = campo_hora.getText().trim();

    artista seleccionado = selector_artistas.getValue();
    LocalDate fecha = campo_fecha.getValue();

    if (nombre.isEmpty()
            || recinto.isEmpty()
            || hora.isEmpty()
            || seleccionado == null
            || fecha == null
            || campo_estado.getValue() == null) {

        mostrar_mensaje(
                Alert.AlertType.WARNING,
                "Datos incompletos",
                "Completa el título, artista, fecha, hora, recinto y estado."
        );
        return false;
    }

    if (fecha.isBefore(LocalDate.now())) {
        mostrar_mensaje(
                Alert.AlertType.WARNING,
                "Fecha no válida",
                "Selecciona una fecha de hoy o posterior."
        );
        return false;
    }

    try {
        LocalTime.parse(
                hora,
                DateTimeFormatter.ofPattern("HH:mm")
        );

    } catch (DateTimeParseException ex) {

        mostrar_mensaje(
                Alert.AlertType.WARNING,
                "Hora no válida",
                "Ingresa una hora válida en formato de 24 horas. Ejemplo: 19:30."
        );

        return false;
    }

    return true;
}

    private concierto obtener_concierto_del_formulario() {

        artista seleccionado = selector_artistas.getValue();

        concierto nuevo = new concierto();

        nuevo.setId_artista(seleccionado.getId_artista());
        nuevo.setTitulo_evento(campo_titulo.getText().trim());
        nuevo.setFecha_concierto(Date.valueOf(campo_fecha.getValue()));
        nuevo.setHora_concierto(
        Time.valueOf(
                LocalTime.parse(
                        campo_hora.getText().trim(),
                        DateTimeFormatter.ofPattern("HH:mm")
                )
        )
);
        
        nuevo.setRecinto(campo_recinto.getText().trim());
        nuevo.setEstado(campo_estado.getValue());

        nuevo.setRuta_cartel(
                campo_cartel.getText().isBlank()
                        ? null : campo_cartel.getText().trim()
        );

        nuevo.setRuta_video(
                campo_video.getText().isBlank()
                        ? null : campo_video.getText().trim()
        );

        return nuevo;
    }

    // CARGAR CONCIERTO SELECCIONADO
    private void cargar_concierto_seleccionado(
            concierto seleccionado) {

        detener_reproductor();

        campo_titulo.setText(
                valor_seguro(seleccionado.getTitulo_evento())
        );

        campo_recinto.setText(
                valor_seguro(seleccionado.getRecinto())
        );

        campo_estado.setValue(
                seleccionado.getEstado() == null
                        ? "programado"
                        : seleccionado.getEstado()
        );

        if (seleccionado.getFecha_concierto() != null) {
            campo_fecha.setValue(
                    seleccionado.getFecha_concierto().toLocalDate()
            );
        } else {
            campo_fecha.setValue(null);
        }
        
        if (seleccionado.getHora_concierto() != null) {

    campo_hora.setText(
            seleccionado.getHora_concierto()
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
    );

} else {
    campo_hora.clear();
}

        campo_cartel.setText(
                valor_seguro(seleccionado.getRuta_cartel())
        );

        campo_video.setText(
                valor_seguro(seleccionado.getRuta_video())
        );

        seleccionar_artista(seleccionado.getId_artista());

        mostrar_cartel(seleccionado.getRuta_cartel());

        if (seleccionado.getRuta_video() != null
                && !seleccionado.getRuta_video().isBlank()) {

            reproducir_video(seleccionado.getRuta_video());

        } else {
            etiqueta_video.setText(
                    "Este concierto no tiene video promocional."
            );
        }
    }

    private void seleccionar_artista(int id_artista) {

        for (artista actual : selector_artistas.getItems()) {
            if (actual.getId_artista() == id_artista) {
                selector_artistas.setValue(actual);
                return;
            }
        }

        selector_artistas.getSelectionModel().clearSelection();
    }

    // LIMPIAR FORMULARIO
    private void limpiar_formulario() {

        detener_reproductor();

        tabla.getSelectionModel().clearSelection();

        campo_titulo.clear();
        campo_recinto.clear();
        campo_hora.clear();
        campo_cartel.clear();
        campo_video.clear();

        campo_fecha.setValue(LocalDate.now().plusDays(30));
        campo_estado.setValue("programado");

        selector_artistas.getSelectionModel().clearSelection();

        vista_cartel.setImage(null);

        etiqueta_video.setText(
                "Selecciona un concierto para ver su video."
        );

        boton_reproducir.setText("▶ REPRODUCIR");
        boton_silencio.setText("🔇 ACTIVAR AUDIO");
    }

    // CARTEL
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

    // VIDEO
    private void reproducir_video(String ruta) {

        detener_reproductor();

        if (ruta == null || ruta.isBlank()) {
            etiqueta_video.setText("No hay un video seleccionado.");
            return;
        }

        File archivo = new File(ruta);

        if (!archivo.isFile()) {
            etiqueta_video.setText(
                    "No se encontró el archivo de video."
            );
            return;
        }

        try {
            Media media = new Media(
                    archivo.toURI().toString()
            );

            MediaPlayer reproductor_nuevo = new MediaPlayer(media);
            reproductor = reproductor_nuevo;

            vista_video.setMediaPlayer(reproductor_nuevo);

            reproductor_nuevo.setVolume(
                    control_volumen.getValue() / 100.0
            );

            reproductor_nuevo.setMute(true);
            sonido_silenciado = true;

            boton_silencio.setText("🔊 ACTIVAR AUDIO");
            etiqueta_video.setText("Cargando video promocional...");

            reproductor_nuevo.setOnReady(() -> {

                if (reproductor != reproductor_nuevo) {
                    return;
                }

                boton_reproducir.setDisable(false);
                boton_detener.setDisable(false);
                boton_silencio.setDisable(false);
                control_volumen.setDisable(false);

// El video queda pausado hasta que el usuario lo reproduzca.
reproductor_nuevo.pause();

boton_reproducir.setText("▶ REPRODUCIR");

etiqueta_video.setText(
        "Video listo. Presiona REPRODUCIR para verlo."
);
            });

            reproductor_nuevo.setOnError(() -> {

                if (reproductor == reproductor_nuevo) {
                    etiqueta_video.setText(
                            "No se pudo reproducir este video. Comprueba el formato."
                    );
                }
            });

            reproductor_nuevo.setOnEndOfMedia(() ->
                    boton_reproducir.setText("▶ REPRODUCIR")
            );

            reproductor_nuevo.setOnStopped(() ->
                    boton_reproducir.setText("▶ REPRODUCIR")
            );

        } catch (Exception ex) {

            detener_reproductor();

            etiqueta_video.setText(
                    "No se pudo abrir el video seleccionado."
            );
        }
    }

    private void detener_reproductor() {

        if (vista_video != null) {
            vista_video.setMediaPlayer(null);
        }

        if (reproductor != null) {

            MediaPlayer anterior = reproductor;
            reproductor = null;

            anterior.setOnReady(null);
            anterior.setOnError(null);
            anterior.setOnEndOfMedia(null);
            anterior.setOnStopped(null);

            anterior.stop();
            anterior.dispose();
        }

        if (boton_reproducir != null) {
            boton_reproducir.setDisable(true);
            boton_reproducir.setText("▶ REPRODUCIR");
        }

        if (boton_detener != null) {
            boton_detener.setDisable(true);
        }

        if (boton_silencio != null) {
            boton_silencio.setDisable(true);
            boton_silencio.setText("🔇 ACTIVAR AUDIO");
        }

        if (control_volumen != null) {
            control_volumen.setDisable(true);
        }

        sonido_silenciado = true;
    }

    // TABLA DE CONCIERTOS
    private void configurar_tabla() {

        TableColumn<concierto, String> columna_id =
                new TableColumn<>("ID");

        columna_id.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        String.valueOf(dato.getValue().getId_concierto())
                )
        );
        columna_id.setPrefWidth(55);

        TableColumn<concierto, String> columna_cartel =
                new TableColumn<>("CARTEL");

        columna_cartel.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(dato.getValue().getRuta_cartel())
                )
        );

        columna_cartel.setCellFactory(columna ->
                new TableCell<concierto, String>() {

                    private final ImageView imagen = new ImageView();

                    {
                        imagen.setFitWidth(55);
                        imagen.setFitHeight(55);
                        imagen.setPreserveRatio(true);
                        setAlignment(Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(String ruta, boolean vacia) {
                        super.updateItem(ruta, vacia);

                        if (vacia || ruta == null || ruta.isBlank()
                                || !new File(ruta).isFile()) {
                            imagen.setImage(null);
                            setGraphic(null);
                            return;
                        }

                        try {
                            imagen.setImage(new Image(
                                    new File(ruta).toURI().toString(),
                                    55, 55, true, true
                            ));
                            setGraphic(imagen);
                        } catch (Exception ex) {
                            imagen.setImage(null);
                            setGraphic(null);
                        }
                    }
                }
        );
        columna_cartel.setPrefWidth(90);

        TableColumn<concierto, String> columna_titulo =
                new TableColumn<>("Evento");

        columna_titulo.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(dato.getValue().getTitulo_evento())
                )
        );

        TableColumn<concierto, String> columna_artista =
                new TableColumn<>("Artista");

        columna_artista.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        obtener_nombre_artista(
                                dato.getValue().getId_artista()
                        )
                )
        );

        TableColumn<concierto, String> columna_fecha =
                new TableColumn<>("Fecha");

        columna_fecha.setCellValueFactory(dato -> {

            Date fecha = dato.getValue().getFecha_concierto();

            String texto = fecha == null
                    ? ""
                    : fecha.toLocalDate().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    );

            return new SimpleStringProperty(texto);
        });
        
        TableColumn<concierto, String> columna_hora =
        new TableColumn<>("Hora");

columna_hora.setCellValueFactory(dato -> {

    Time hora = dato.getValue().getHora_concierto();

    String texto = hora == null
            ? ""
            : hora.toLocalTime().format(
                    DateTimeFormatter.ofPattern("HH:mm")
            );

    return new SimpleStringProperty(texto);
});

        TableColumn<concierto, String> columna_recinto =
                new TableColumn<>("Recinto");

        columna_recinto.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(dato.getValue().getRecinto())
                )
        );

        TableColumn<concierto, String> columna_estado =
                new TableColumn<>("Estado");

        columna_estado.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(dato.getValue().getEstado())
                )
        );

        tabla.getColumns().setAll(
        columna_id,
        columna_cartel,
        columna_titulo,
        columna_artista,
        columna_fecha,
        columna_hora,
        columna_recinto,
        columna_estado
);

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label("No hay conciertos registrados.")
        );

tabla.setPrefHeight(260);
tabla.setMinHeight(150);
tabla.setMaxHeight(Double.MAX_VALUE);

        tabla.setStyle(
                "-fx-background-color: #1C2940;"
                + "-fx-control-inner-background: #1C2940;"
                + "-fx-text-background-color: #F4F7FC;"
                + "-fx-table-cell-border-color: #34445F;"
                + "-fx-border-color: #34445F;"
                + "-fx-font-size: 13px;"
                + "-fx-selection-bar: #354966;"
                + "-fx-selection-bar-non-focused: #354966;"
        );

        for (TableColumn<concierto, ?> columna : tabla.getColumns()) {

            Label titulo = new Label(columna.getText());
            titulo.setMaxWidth(Double.MAX_VALUE);
            titulo.setAlignment(Pos.CENTER_LEFT);

            titulo.setStyle(
                    "-fx-background-color: #080D18;"
                    + "-fx-text-fill: #F4F7FC;"
                    + "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-padding: 12px 8px;"
            );

            columna.setText("");
            columna.setGraphic(titulo);
        }
    }

    // CARGAR DATOS
    private void cargar_artistas() {

        try {
            List<artista> artistas = dao_artistas.listar_artistas();

            selector_artistas.setItems(
                    FXCollections.observableArrayList(artistas)
            );

        } catch (SQLException | IOException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al cargar artistas",
                    obtener_mensaje_error(ex, "No se pudo cargar el catálogo.")
            );
        }
    }

    private void cargar_conciertos() {

        try {
            List<concierto> conciertos = dao.listar_conciertos();

            tabla.setItems(
                    FXCollections.observableArrayList(conciertos)
            );

        } catch (SQLException | IOException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al cargar conciertos",
                    obtener_mensaje_error(ex, "No se pudieron cargar los conciertos.")
            );
        }
    }

    private String obtener_nombre_artista(int id_artista) {

        for (artista actual : selector_artistas.getItems()) {
            if (actual.getId_artista() == id_artista) {
                return actual.getNombre_artistico();
            }
        }

        return "Artista ID " + id_artista;
    }

    // COMPONENTES Y ESTILOS
    private void agregar_campo(
            GridPane formulario,
            String texto,
            Node campo,
            int fila) {

        Label etiqueta = new Label(texto);
        etiqueta.setTextFill(Color.web(TEXTO));

        formulario.add(etiqueta, 0, fila);
        formulario.add(campo, 1, fila);

        if (campo instanceof javafx.scene.control.Control control) {
            control.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(control, Priority.ALWAYS);

        } else if (campo instanceof HBox caja) {

            caja.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(caja, Priority.ALWAYS);

            for (Node elemento : caja.getChildren()) {
                if (elemento instanceof TextField campo_texto) {
                    HBox.setHgrow(campo_texto, Priority.ALWAYS);
                    campo_texto.setMaxWidth(Double.MAX_VALUE);
                }
            }
        }
    }

    private Label crear_titulo(String texto) {

        Label titulo = new Label(texto);
        titulo.setFont(Font.font("Arial", 16));
        titulo.setTextFill(Color.web(NARANJA));

        return titulo;
    }

    private Button crear_boton(String texto, String color) {

        Button boton = new Button(texto);

        boton.setStyle(
                "-fx-background-color: " + color + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );

        return boton;
    }

    private void estilizar_boton_principal(Button boton) {

        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setPrefHeight(40);
        boton.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
    }

    private void estilizar_boton_naranja(Button boton) {

        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setPrefHeight(40);
        boton.setStyle(
                "-fx-background-color: " + NARANJA + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
    }

    private void estilizar_boton_secundario(Button boton) {

        boton.setPrefHeight(38);
        boton.setStyle(
                "-fx-background-color: #263550;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );
    }

private String estilo_panel() {

    return "-fx-background-color: " + PANEL + ";"
            + "-fx-background-radius: 10;"
            + "-fx-border-color: #344563;"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 10;";
}

    private String estilo_combo() {

        return "-fx-background-color: #080D18;"
                + "-fx-text-fill: white;";
    }

    private void estilizar_campo(TextField campo) {

        campo.setPrefHeight(36);
        campo.setStyle(
                "-fx-background-color: #080D18;"
                + "-fx-text-fill: white;"
                + "-fx-prompt-text-fill: " + SECUNDARIO + ";"
                + "-fx-border-color: #344563;"
                + "-fx-border-radius: 5;"
                + "-fx-background-radius: 5;"
        );
    }

    private String valor_seguro(String texto) {
        return texto == null ? "" : texto;
    }

    private String obtener_mensaje_error(
            Exception ex,
            String mensaje_predeterminado) {

        return ex.getMessage() == null || ex.getMessage().isBlank()
                ? mensaje_predeterminado
                : ex.getMessage();
    }

    private void mostrar_mensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
