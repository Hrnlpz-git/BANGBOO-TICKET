
package vista;

import dao.artista_dao;
import modelo.artista;
import servicio.sesion_usuario;
import utilidades.encabezado_utilidad;
import utilidades.ventana_utilidad;
import utilidades.gestor_archivos;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class gestionar_artistas {

    private final Stage ventana;
    private final sesion_usuario sesion;
    private final artista_dao dao = new artista_dao();

    private final TableView<artista> tabla = new TableView<>();

    private TextField campo_nombre;
    private TextField campo_genero;
    private TextField campo_pais;
    private TextField campo_imagen;
    private TextField campo_video;

    private Button boton_registrar;
    private Button boton_actualizar;
    private Button boton_eliminar;
    private Button boton_limpiar;

    private MediaPlayer reproductor_artista;

    private ImageView imagen_previa_artista;
    private MediaView vista_video_artista;

    private Label nombre_previo_artista;
    private Label datos_previo_artista;
    private Label estado_video_artista;

    private Button boton_reproducir_pausar;
    private Button boton_detener_video;
    private Button boton_silenciar;

    private Slider barra_volumen;

    private boolean sonido_silenciado = false;

    private static final String FONDO = "#080D18";
    private static final String PANEL = "#101827";
    private static final String AZUL = "#2855D9";
    private static final String NARANJA = "#F07832";
    private static final String TEXTO = "#F3F1EA";
    private static final String SECUNDARIO = "#9BAAC4";

    public gestionar_artistas(
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
                    "Solo el administrador puede gestionar artistas."
            );

            new menu_principal(ventana, sesion).mostrar();
            return;
        }

        // ENCABEZADO
        Label subtitulo = new Label(
                "Registra artistas y consulta el catálogo musical"
        );
        subtitulo.setTextFill(Color.web(SECUNDARIO));

        Button volver = new Button("← VOLVER AL MENÚ");
        estilizar_boton_secundario(volver);

        volver.setOnAction(evento -> {
            detener_reproductor_artista();
            new menu_principal(ventana, sesion).mostrar();
        });

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox encabezado = new HBox(
                18,
                encabezado_utilidad.crear("GESTIÓN DE ARTISTAS"),
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

        Label titulo_formulario = crear_titulo(
                "REGISTRAR ARTISTA"
        );

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(12);

        agregar_campo(
                formulario,
                "Nombre artístico:",
                campo_nombre,
                0
        );

        agregar_campo(
                formulario,
                "Género musical:",
                campo_genero,
                1
        );

        agregar_campo(
                formulario,
                "País de origen:",
                campo_pais,
                2
        );

        HBox seleccion_imagen = new HBox(
                8,
                campo_imagen,
                crear_boton_archivo("ELEGIR IMAGEN", true)
        );

        HBox seleccion_video = new HBox(
                8,
                campo_video,
                crear_boton_archivo("ELEGIR VIDEO", false)
        );

        agregar_campo(
                formulario,
                "Fotografía:",
                seleccion_imagen,
                3
        );

        agregar_campo(
                formulario,
                "Video promocional:",
                seleccion_video,
                4
        );

        // BOTÓN REGISTRAR
        boton_registrar = new Button("REGISTRAR ARTISTA");
        estilizar_boton_principal(boton_registrar);

        boton_registrar.setOnAction(evento ->
                registrar_artista()
        );

        // BOTÓN ACTUALIZAR
        boton_actualizar = new Button("ACTUALIZAR ARTISTA");
        estilizar_boton_naranja(boton_actualizar);

        boton_actualizar.setOnAction(evento ->
                actualizar_artista()
        );

        // BOTÓN ELIMINAR
        boton_eliminar = new Button("ELIMINAR ARTISTA");
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
                eliminar_artista()
        );

        // BOTÓN LIMPIAR
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

        // BOTONES PRINCIPALES
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

        // BOTONES SECUNDARIOS
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

        // VISTA PREVIA DEL ARTISTA
        crear_previsualizaciones();

// PANEL DE FOTOGRAFÍA E INFORMACIÓN
VBox panel_datos_artista = new VBox(
        10,
        crear_titulo("DATOS DEL ARTISTA"),
        imagen_previa_artista,
        nombre_previo_artista,
        datos_previo_artista
);

panel_datos_artista.setAlignment(Pos.TOP_CENTER);
panel_datos_artista.setPrefWidth(220);
panel_datos_artista.setMinWidth(200);
panel_datos_artista.setMaxWidth(240);

        panel_datos_artista.setAlignment(Pos.TOP_CENTER);
        panel_datos_artista.setPrefWidth(220);
        panel_datos_artista.setMinWidth(200);
        panel_datos_artista.setMaxWidth(240);

        // PANEL DEL VIDEO
        VBox panel_reproductor_artista = new VBox(
                13,
                crear_titulo("VIDEO PROMOCIONAL"),
                crear_contenedor(vista_video_artista),
                crear_controles_video()
        );

        panel_reproductor_artista.setAlignment(Pos.TOP_CENTER);
        panel_reproductor_artista.setPrefWidth(430);
        panel_reproductor_artista.setMinWidth(360);
        panel_reproductor_artista.setMaxWidth(470);

        // CONTENIDO DE LA PREVISUALIZACIÓN
        HBox contenido_previsualizacion = new HBox(
                12,
                panel_datos_artista,
                panel_reproductor_artista
        );

        contenido_previsualizacion.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(panel_reproductor_artista, Priority.ALWAYS);

        Label titulo_previsualizacion = crear_titulo(
                "VISTA PREVIA DEL ARTISTA"
        );

        titulo_previsualizacion.setMaxWidth(Double.MAX_VALUE);
        titulo_previsualizacion.setAlignment(Pos.CENTER);

        VBox panel_previsualizacion = new VBox(
                12,
                titulo_previsualizacion,
                contenido_previsualizacion
        );

        panel_previsualizacion.setAlignment(Pos.TOP_LEFT);
        panel_previsualizacion.setPadding(
                new Insets(15, 20, 10, 20)
        );
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
        zona_superior.setFillHeight(false);

        panel_formulario.setPrefWidth(440);
        panel_formulario.setMinWidth(360);
        HBox.setHgrow(panel_formulario, Priority.ALWAYS);

        panel_previsualizacion.setPrefWidth(700);
        panel_previsualizacion.setMinWidth(560);
        panel_previsualizacion.setMaxHeight(Region.USE_PREF_SIZE);
        panel_previsualizacion.setMinHeight(Region.USE_PREF_SIZE);

        HBox.setHgrow(panel_previsualizacion, Priority.ALWAYS);

        // TABLA DE ARTISTAS
        Label titulo_tabla = crear_titulo(
                "ARTISTAS REGISTRADOS"
        );

        Button actualizar_lista = new Button("ACTUALIZAR LISTA");
        estilizar_boton_principal(actualizar_lista);

        actualizar_lista.setOnAction(evento ->
                cargar_artistas()
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
                        cargar_artista_seleccionado(seleccionado);
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

        // CENTRO
        VBox centro = new VBox(
                14,
                zona_superior,
                panel_tabla
        );

        centro.setPadding(new Insets(10, 25, 10, 25));
        VBox.setVgrow(panel_tabla, Priority.ALWAYS);

        // CONTENEDOR PRINCIPAL
        BorderPane contenido = new BorderPane();

        contenido.setTop(encabezado);
        contenido.setCenter(centro);

        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        Scene escena = new Scene(contenido, 1250, 800);

        ventana.setTitle("BANGBOO-TICKET | Gestión de artistas");
        ventana.setScene(escena);

        ventana_utilidad.configurar(ventana);
        ventana.show();

        cargar_artistas();
    }

    // CREAR CAMPOS
    private void crear_campos() {

        campo_nombre = new TextField();
        campo_nombre.setPromptText("Nombre artístico");

        campo_genero = new TextField();
        campo_genero.setPromptText("Género musical");

        campo_pais = new TextField();
        campo_pais.setPromptText("País de origen");

        campo_imagen = new TextField();
        campo_imagen.setPromptText("Selecciona una imagen...");
        campo_imagen.setEditable(false);

        campo_video = new TextField();
        campo_video.setPromptText("Selecciona un video promocional...");
        campo_video.setEditable(false);

        estilizar_campo(campo_nombre);
        estilizar_campo(campo_genero);
        estilizar_campo(campo_pais);
        estilizar_campo(campo_imagen);
        estilizar_campo(campo_video);
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
                selector.setTitle("Seleccionar imagen del artista");

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
                                "*.mp4", "*.m4v", "*.mkv"
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
                        es_imagen ? "artistas" : "videos"
                );

                if (es_imagen) {
                    campo_imagen.setText(ruta_copiada);
                    mostrar_imagen(ruta_copiada);

                } else {
                    campo_video.setText(ruta_copiada);
                    reproducir_video_artista(ruta_copiada);
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

    // VISTAS PREVIAS Y CONTROLES
    private void crear_previsualizaciones() {

        // FOTOGRAFÍA
        imagen_previa_artista = new ImageView();

        imagen_previa_artista.setFitWidth(190);
        imagen_previa_artista.setFitHeight(245);
        imagen_previa_artista.setPreserveRatio(true);
        imagen_previa_artista.setSmooth(true);

        // NOMBRE DEL ARTISTA
        nombre_previo_artista = new Label(
                "Selecciona un artista"
        );

        nombre_previo_artista.setFont(Font.font("Arial", 20));
        nombre_previo_artista.setTextFill(Color.web(TEXTO));
        nombre_previo_artista.setWrapText(true);
        nombre_previo_artista.setAlignment(Pos.CENTER);

        // INFORMACIÓN
        datos_previo_artista = new Label(
                "Aquí aparecerá su información musical."
        );

        datos_previo_artista.setFont(Font.font("Arial", 13));
        datos_previo_artista.setTextFill(Color.web(SECUNDARIO));
        datos_previo_artista.setWrapText(true);
        datos_previo_artista.setAlignment(Pos.CENTER);

        // VIDEO
        vista_video_artista = new MediaView();

        vista_video_artista.setFitWidth(420);
        vista_video_artista.setFitHeight(320);
        vista_video_artista.setPreserveRatio(true);
        vista_video_artista.setSmooth(false);

        // MENSAJE INTERNO DEL VIDEO
        estado_video_artista = new Label(
                "Selecciona un artista para ver su video."
        );

        estado_video_artista.setTextFill(Color.web(SECUNDARIO));
        estado_video_artista.setWrapText(true);

        // BOTONES
        boton_reproducir_pausar = crear_boton(
                "▶ REPRODUCIR",
                AZUL
        );

        boton_detener_video = crear_boton(
                "■ DETENER",
                "#263550"
        );

        boton_silenciar = crear_boton(
                "🔇 ACTIVAR AUDIO",
                "#263550"
        );

        boton_reproducir_pausar.setDisable(true);
        boton_detener_video.setDisable(true);
        boton_silenciar.setDisable(true);

        // VOLUMEN
        barra_volumen = new Slider(0, 100, 65);

        barra_volumen.setMinWidth(65);
        barra_volumen.setPrefWidth(80);
        barra_volumen.setMaxWidth(80);
        barra_volumen.setShowTickLabels(true);
        barra_volumen.setShowTickMarks(false);
        barra_volumen.setMajorTickUnit(25);
        barra_volumen.setDisable(true);

        barra_volumen.setStyle(
                "-fx-control-inner-background: " + NARANJA + ";"
        );

        barra_volumen.valueProperty().addListener(
                (observable, anterior, nuevo) -> {

                    if (reproductor_artista != null) {
                        reproductor_artista.setVolume(
                                nuevo.doubleValue() / 100.0
                        );
                    }
                }
        );

        // REPRODUCIR Y PAUSAR
        boton_reproducir_pausar.setOnAction(evento -> {

            if (reproductor_artista == null) {
                return;
            }

            if (reproductor_artista.getStatus()
                    == MediaPlayer.Status.PLAYING) {

                reproductor_artista.pause();

                boton_reproducir_pausar.setText("▶ REANUDAR");
                estado_video_artista.setText("Video en pausa.");

            } else {
                reproductor_artista.play();

                boton_reproducir_pausar.setText("⏸ PAUSAR");
                estado_video_artista.setText(
                        "Reproduciendo video promocional."
                );
            }
        });

        // DETENER
        boton_detener_video.setOnAction(evento -> {

            if (reproductor_artista != null) {
                reproductor_artista.stop();

                boton_reproducir_pausar.setText("▶ REPRODUCIR");
                estado_video_artista.setText("Video detenido.");
            }
        });

        // SILENCIAR Y ACTIVAR AUDIO
        boton_silenciar.setOnAction(evento -> {

            if (reproductor_artista == null) {
                return;
            }

            sonido_silenciado = !sonido_silenciado;

            reproductor_artista.setMute(sonido_silenciado);

            boton_silenciar.setText(
                    sonido_silenciado
                            ? "🔊 ACTIVAR AUDIO"
                            : "🔇 SILENCIAR"
            );
        });
    }

    // CONTROLES DEL VIDEO
    private HBox crear_controles_video() {

        boton_reproducir_pausar.setPrefHeight(38);
        boton_detener_video.setPrefHeight(38);
        boton_silenciar.setPrefHeight(38);

        boton_reproducir_pausar.setPrefWidth(115);
        boton_detener_video.setPrefWidth(95);
        boton_silenciar.setPrefWidth(125);

        barra_volumen.setMinWidth(65);
        barra_volumen.setPrefWidth(80);
        barra_volumen.setMaxWidth(80);

        HBox controles = new HBox(
                7,
                boton_reproducir_pausar,
                boton_detener_video,
                boton_silenciar,
                barra_volumen
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

    // CONTENEDOR DEL VIDEO
    private VBox crear_contenedor(Node elemento) {

        VBox contenedor = new VBox(elemento);

        contenedor.setAlignment(Pos.CENTER);
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

    // REGISTRAR ARTISTA
    private void registrar_artista() {

        String nombre = campo_nombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "Campo obligatorio",
                    "Escribe el nombre artístico."
            );
            return;
        }

        artista nuevo = obtener_artista_del_formulario();

        try {
            dao.registrar_artista(nuevo);

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "El artista se registró correctamente."
            );

            limpiar_formulario();
            cargar_artistas();

        } catch (SQLException | IOException | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al registrar",
                    obtener_mensaje_error(
                            ex,
                            "No se pudo registrar el artista."
                    )
            );
        }
    }

    // ACTUALIZAR ARTISTA
    private void actualizar_artista() {

        artista seleccionado =
                tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un artista",
                    "Selecciona un artista de la tabla antes de actualizarlo."
            );
            return;
        }

        String nombre = campo_nombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "Campo obligatorio",
                    "El nombre artístico es obligatorio."
            );
            return;
        }

        int id_artista = seleccionado.getId_artista();

        artista actualizado = obtener_artista_del_formulario();
        actualizado.setId_artista(id_artista);

        try {
            dao.actualizar_artista(actualizado);

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Actualización exitosa",
                    "Los datos del artista se actualizaron correctamente."
            );

            cargar_artistas();
            seleccionar_artista_en_tabla(id_artista);

        } catch (SQLException | IOException | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al actualizar",
                    obtener_mensaje_error(
                            ex,
                            "No se pudo actualizar el artista."
                    )
            );
        }
    }

    // ELIMINAR ARTISTA
    private void eliminar_artista() {

        artista seleccionado =
                tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un artista",
                    "Selecciona un artista de la tabla antes de eliminarlo."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar artista?");

        confirmacion.setContentText(
                "¿Estás seguro de que deseas eliminar a "
                + seleccionado.getNombre_artistico()
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
            dao.eliminar_artista(
                    seleccionado.getId_artista()
            );

            detener_reproductor_artista();

            cargar_artistas();
            limpiar_formulario();

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Eliminación exitosa",
                    "El artista se eliminó correctamente."
            );

        } catch (SQLException | IOException | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo eliminar",
                    obtener_mensaje_error(
                            ex,
                            "Ocurrió un error al eliminar el artista."
                    )
            );
        }
    }

    // OBTENER ARTISTA DEL FORMULARIO
    private artista obtener_artista_del_formulario() {

        artista nuevo = new artista();

        nuevo.setNombre_artistico(
                campo_nombre.getText().trim()
        );

        nuevo.setGenero_musical(
                texto_o_nulo(campo_genero.getText())
        );

        nuevo.setPais_origen(
                texto_o_nulo(campo_pais.getText())
        );

        nuevo.setRuta_imagen(
                texto_o_nulo(campo_imagen.getText())
        );

        nuevo.setRuta_video(
                texto_o_nulo(campo_video.getText())
        );

        return nuevo;
    }

    // CARGAR ARTISTA SELECCIONADO
    private void cargar_artista_seleccionado(artista seleccionado) {

        campo_nombre.setText(
                valor_seguro(seleccionado.getNombre_artistico())
        );

        campo_genero.setText(
                valor_seguro(seleccionado.getGenero_musical())
        );

        campo_pais.setText(
                valor_seguro(seleccionado.getPais_origen())
        );

        campo_imagen.setText(
                valor_seguro(seleccionado.getRuta_imagen())
        );

        campo_video.setText(
                valor_seguro(seleccionado.getRuta_video())
        );

        mostrar_previsualizacion(seleccionado);
    }

    // SELECCIONAR ARTISTA EN TABLA
    private void seleccionar_artista_en_tabla(int id_artista) {

        for (artista actual : tabla.getItems()) {

            if (actual.getId_artista() == id_artista) {
                tabla.getSelectionModel().select(actual);
                tabla.scrollTo(actual);
                return;
            }
        }
    }

    // LIMPIAR FORMULARIO
    private void limpiar_formulario() {

        detener_reproductor_artista();

        tabla.getSelectionModel().clearSelection();

        campo_nombre.clear();
        campo_genero.clear();
        campo_pais.clear();
        campo_imagen.clear();
        campo_video.clear();

        imagen_previa_artista.setImage(null);

        nombre_previo_artista.setText(
                "Selecciona un artista"
        );

        datos_previo_artista.setText(
                "Aquí aparecerá su información musical."
        );

        estado_video_artista.setText(
                "Selecciona un artista para ver su video."
        );

        barra_volumen.setValue(65);

        sonido_silenciado = false;
    }

    // MOSTRAR PREVISUALIZACIÓN
    private void mostrar_previsualizacion(artista seleccionado) {

        nombre_previo_artista.setText(
                valor_seguro(seleccionado.getNombre_artistico())
        );

        String genero = seleccionado.getGenero_musical();
        String pais = seleccionado.getPais_origen();

        datos_previo_artista.setText(
                "Género: "
                + (genero == null || genero.isBlank()
                        ? "No especificado" : genero)
                + "\nPaís: "
                + (pais == null || pais.isBlank()
                        ? "No especificado" : pais)
        );

        mostrar_imagen(seleccionado.getRuta_imagen());

        detener_reproductor_artista();

        String ruta_video = seleccionado.getRuta_video();

        if (ruta_video == null || ruta_video.isBlank()) {

            estado_video_artista.setText(
                    "Este artista no tiene un video promocional disponible."
            );

            return;
        }

        reproducir_video_artista(ruta_video);
    }

    // MOSTRAR IMAGEN
    private void mostrar_imagen(String ruta) {

        imagen_previa_artista.setImage(null);

        if (ruta == null || ruta.isBlank()) {
            return;
        }

        File archivo = new File(ruta);

        if (!archivo.isFile()) {
            return;
        }

        try {
            imagen_previa_artista.setImage(
                    new Image(archivo.toURI().toString())
            );

        } catch (Exception ex) {
            imagen_previa_artista.setImage(null);
        }
    }

    // REPRODUCIR VIDEO
    private void reproducir_video_artista(String ruta) {

        detener_reproductor_artista();

        if (ruta == null || ruta.isBlank()) {

            estado_video_artista.setText(
                    "No hay un video seleccionado."
            );

            return;
        }

        File archivo = new File(ruta);

        if (!archivo.isFile()) {

            estado_video_artista.setText(
                    "No se encontró el archivo de video."
            );

            return;
        }

        try {
            Media medio = new Media(
                    archivo.toURI().toString()
            );

            MediaPlayer reproductor_nuevo = new MediaPlayer(medio);

            reproductor_artista = reproductor_nuevo;

            reproductor_nuevo.setCycleCount(
                    MediaPlayer.INDEFINITE
            );

            reproductor_nuevo.setVolume(
                    barra_volumen.getValue() / 100.0
            );

            reproductor_nuevo.setMute(true);

            sonido_silenciado = true;

            boton_silenciar.setText("🔊 ACTIVAR AUDIO");

            vista_video_artista.setMediaPlayer(
                    reproductor_nuevo
            );

            estado_video_artista.setText(
                    "Cargando video promocional..."
            );

reproductor_nuevo.setOnReady(() -> {

    if (reproductor_artista != reproductor_nuevo) {
        return;
    }

    boton_reproducir_pausar.setDisable(false);
    boton_detener_video.setDisable(false);
    boton_silenciar.setDisable(false);
    barra_volumen.setDisable(false);

    // EL VIDEO SE MANTIENE PAUSADO AL CARGAR
    reproductor_nuevo.pause();

    boton_reproducir_pausar.setText("▶ REPRODUCIR");

    estado_video_artista.setText(
            "Video en pausa. Presiona reproducir para comenzar."
    );
});

            reproductor_nuevo.setOnError(() -> {

                if (reproductor_artista == reproductor_nuevo) {

                    estado_video_artista.setText(
                            "No se pudo reproducir el video. Comprueba el formato."
                    );
                }
            });

        } catch (Exception ex) {

            detener_reproductor_artista();

            estado_video_artista.setText(
                    "Error al cargar el video promocional."
            );
        }
    }

    // DETENER Y LIBERAR REPRODUCTOR
    private void detener_reproductor_artista() {

        if (vista_video_artista != null) {
            vista_video_artista.setMediaPlayer(null);
        }

        if (reproductor_artista != null) {

            MediaPlayer reproductor_anterior = reproductor_artista;
            reproductor_artista = null;

            reproductor_anterior.setOnReady(null);
            reproductor_anterior.setOnError(null);
            reproductor_anterior.setOnPlaying(null);
            reproductor_anterior.setOnPaused(null);
            reproductor_anterior.setOnStopped(null);
            reproductor_anterior.setOnEndOfMedia(null);

            reproductor_anterior.stop();
            reproductor_anterior.dispose();
        }

        if (boton_reproducir_pausar != null) {
            boton_reproducir_pausar.setDisable(true);
            boton_reproducir_pausar.setText("▶ REPRODUCIR");
        }

        if (boton_detener_video != null) {
            boton_detener_video.setDisable(true);
        }

        if (boton_silenciar != null) {
            boton_silenciar.setDisable(true);
            boton_silenciar.setText("🔇 ACTIVAR AUDIO");
        }

        if (barra_volumen != null) {
            barra_volumen.setDisable(true);
        }

        sonido_silenciado = false;
    }

    // CARGAR ARTISTAS
    private void cargar_artistas() {

        try {
            List<artista> artistas = dao.listar_artistas();

            ObservableList<artista> datos =
                    FXCollections.observableArrayList(artistas);

            tabla.setItems(datos);

        } catch (SQLException | IOException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al cargar artistas",
                    obtener_mensaje_error(
                            ex,
                            "No se pudo consultar el catálogo."
                    )
            );
        }
    }

    // CONFIGURAR TABLA
    private void configurar_tabla() {

        TableColumn<artista, String> columna_id =
                new TableColumn<>("ID");

        columna_id.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        String.valueOf(
                                dato.getValue().getId_artista()
                        )
                )
        );

        columna_id.setPrefWidth(60);

        // FOTOGRAFÍA
        TableColumn<artista, String> columna_fotografia =
                new TableColumn<>("FOTOGRAFÍA");

        columna_fotografia.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(
                                dato.getValue().getRuta_imagen()
                        )
                )
        );

        columna_fotografia.setCellFactory(columna ->
                new TableCell<artista, String>() {

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

                        if (vacia
                                || ruta == null
                                || ruta.isBlank()
                                || !new File(ruta).isFile()) {

                            imagen.setImage(null);
                            setGraphic(null);
                            return;
                        }

                        try {
                            imagen.setImage(
                                    new Image(
                                            new File(ruta).toURI().toString(),
                                            55,
                                            55,
                                            true,
                                            true
                                    )
                            );

                            setGraphic(imagen);

                        } catch (Exception ex) {
                            imagen.setImage(null);
                            setGraphic(null);
                        }
                    }
                }
        );

        columna_fotografia.setPrefWidth(100);

        // NOMBRE
        TableColumn<artista, String> columna_nombre =
                new TableColumn<>("Nombre artístico");

        columna_nombre.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(
                                dato.getValue().getNombre_artistico()
                        )
                )
        );

        // GÉNERO
        TableColumn<artista, String> columna_genero =
                new TableColumn<>("Género musical");

        columna_genero.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(
                                dato.getValue().getGenero_musical()
                        )
                )
        );

        // PAÍS
        TableColumn<artista, String> columna_pais =
                new TableColumn<>("País de origen");

        columna_pais.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        valor_seguro(
                                dato.getValue().getPais_origen()
                        )
                )
        );

        tabla.getColumns().setAll(
                columna_id,
                columna_fotografia,
                columna_nombre,
                columna_genero,
                columna_pais
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label("No hay artistas registrados.")
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

        // ENCABEZADOS DE COLUMNAS
        for (TableColumn<artista, ?> columna : tabla.getColumns()) {

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

    // AGREGAR CAMPO AL FORMULARIO
    private void agregar_campo(
            GridPane formulario,
            String texto,
            Node campo,
            int fila) {

        Label etiqueta = new Label(texto);
        etiqueta.setTextFill(Color.web(TEXTO));

        formulario.add(etiqueta, 0, fila);
        formulario.add(campo, 1, fila);

        if (campo instanceof TextField campo_texto) {

            campo_texto.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(campo_texto, Priority.ALWAYS);

        } else if (campo instanceof HBox contenedor) {

            contenedor.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(contenedor, Priority.ALWAYS);

            for (Node elemento : contenedor.getChildren()) {

                if (elemento instanceof TextField campo_interno) {

                    HBox.setHgrow(elemento, Priority.ALWAYS);

                    campo_interno.setMaxWidth(
                            Double.MAX_VALUE
                    );
                }
            }
        }
    }

    // TÍTULOS
    private Label crear_titulo(String texto) {

        Label titulo = new Label(texto);

        titulo.setFont(Font.font("Arial", 16));
        titulo.setTextFill(Color.web(NARANJA));

        return titulo;
    }

    // BOTÓN CON COLOR
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

    // ESTILOS DE BOTONES
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

    // ESTILO DE PANELES
    private String estilo_panel() {

        return "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: #344563;"
                + "-fx-border-width: 1.5;"
                + "-fx-border-radius: 10;";
    }

    // ESTILO DE CAMPOS
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

    // TEXTO VACÍO O NULO
    private String texto_o_nulo(String texto) {

        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }

    private String valor_seguro(String texto) {
        return texto == null ? "" : texto;
    }

    // MENSAJES DE ERROR
    private String obtener_mensaje_error(
            Exception ex,
            String mensaje_predeterminado) {

        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return mensaje_predeterminado;
        }

        return ex.getMessage();
    }

    // ALERTAS
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
