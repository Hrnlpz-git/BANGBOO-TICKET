
package vista;

import dao.localidad_dao;
import modelo.localidad;
import servicio.sesion_usuario;
import utilidades.ventana_utilidad;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import utilidades.encabezado_utilidad;

public class gestionar_localidades {

    private final Stage ventana;
    private final sesion_usuario sesion;
    private final localidad_dao dao_localidades;

    private Scene escena_anterior;
    private TableView<localidad> tabla;
    private TextField campo_nombre;

    private final String FONDO = "#101827";
    private final String PANEL = "#1C2940";
    private final String NARANJA = "#FF9D42";
    private final String TEXTO = "#F4F7FC";
    private final String SUAVE = "#AAB7CC";

    public gestionar_localidades(
            Stage ventana,
            sesion_usuario sesion) {

        this.ventana = ventana;
        this.sesion = sesion;
        this.dao_localidades = new localidad_dao();
    }

    public void mostrar() {

        escena_anterior = ventana.getScene();


Label subtitulo = new Label(
        "Administra las zonas disponibles para los eventos");

subtitulo.setTextFill(Color.web(SUAVE));
subtitulo.setFont(Font.font("Arial", 14));

HBox encabezado_logo = new HBox(
        12,
        encabezado_utilidad.crear("GESTIONAR LOCALIDADES"),
        subtitulo
);

encabezado_logo.setAlignment(Pos.CENTER_LEFT);


        Label etiqueta_nombre = new Label("Nombre de la localidad");
        etiqueta_nombre.setTextFill(Color.web(TEXTO));

        campo_nombre = new TextField();
        campo_nombre.setPromptText("Ejemplo: VIP, General, Preferencial");
        campo_nombre.setPrefHeight(40);
        campo_nombre.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-text-fill: " + TEXTO + ";"
                + "-fx-prompt-text-fill: " + SUAVE + ";"
                + "-fx-background-radius: 7;"
                + "-fx-border-color: #344563;"
                + "-fx-border-radius: 7;"
        );

        Button boton_registrar = new Button("REGISTRAR");
        boton_registrar.setStyle(estilo_boton());
        boton_registrar.setOnAction(
                evento -> registrar_localidad());

        Button boton_actualizar = new Button("ACTUALIZAR");
        boton_actualizar.setStyle(estilo_boton());
        boton_actualizar.setOnAction(
                evento -> cargar_localidades());
        
        Button boton_eliminar = new Button("ELIMINAR");
boton_eliminar.setStyle(
        "-fx-background-color: #C74747;"
        + "-fx-text-fill: white;"
        + "-fx-font-weight: bold;"
        + "-fx-font-size: 13px;"
        + "-fx-background-radius: 8px;"
        + "-fx-padding: 11px 18px;"
        + "-fx-cursor: hand;"
);

boton_eliminar.setOnAction(
        evento -> eliminar_localidad());

        VBox formulario = new VBox(
                10, etiqueta_nombre, campo_nombre);

HBox botones_formulario = new HBox(
        12,
        boton_registrar,
        boton_actualizar,
        boton_eliminar
);
        botones_formulario.setAlignment(Pos.CENTER_LEFT);

        VBox panel_formulario = new VBox(
                14, formulario, botones_formulario);
        panel_formulario.setPadding(new Insets(20));
        panel_formulario.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 12;"
        );

        Label titulo_tabla = new Label("LOCALIDADES REGISTRADAS");
        titulo_tabla.setTextFill(Color.web(TEXTO));
        titulo_tabla.setFont(Font.font("Arial", 16));
        titulo_tabla.setStyle("-fx-font-weight: bold;");

        tabla = new TableView<>();
        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        tabla.setPlaceholder(
                new Label("No hay localidades registradas."));

        TableColumn<localidad, Number> columna_id =
                new TableColumn<>("ID");

        columna_id.setCellValueFactory(
                datos -> new javafx.beans.property
                        .SimpleIntegerProperty(
                                datos.getValue().getId_localidad()));

        TableColumn<localidad, String> columna_nombre =
                new TableColumn<>("NOMBRE DE LA LOCALIDAD");

        columna_nombre.setCellValueFactory(
                datos -> new javafx.beans.property
                        .SimpleStringProperty(
                                datos.getValue().getNombre()));

        tabla.getColumns().addAll(
                columna_id, columna_nombre);


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

for (TableColumn<localidad, ?> columna : tabla.getColumns()) {

    Label encabezado = new Label(columna.getText());

    encabezado.setMaxWidth(Double.MAX_VALUE);
    encabezado.setAlignment(Pos.CENTER_LEFT);

    encabezado.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-text-fill: #F4F7FC;"
            + "-fx-font-size: 13px;"
            + "-fx-font-weight: bold;"
            + "-fx-padding: 12px 8px 12px 8px;"
    );

    columna.setText("");
    columna.setGraphic(encabezado);
}


        Label pie_texto = new Label(
                "BANGBOO-TICKET • ADMINISTRACIÓN DE LOCALIDADES");
        pie_texto.setTextFill(Color.web(SUAVE));

 Button boton_volver = new Button("← VOLVER AL MENÚ");
boton_volver.setStyle(
        "-fx-background-color: #263550;"
        + "-fx-text-fill: #F3F1EA;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 7;"
        + "-fx-cursor: hand;"
);

boton_volver.setPrefHeight(40);

boton_volver.setOnAction(evento -> {

    if (escena_anterior != null) {
        ventana.setScene(escena_anterior);
        ventana_utilidad.configurar(ventana);
        ventana.setMaximized(true);
        ventana.show();
    }
});

        HBox pie = new HBox(
                pie_texto, boton_volver);
        pie.setAlignment(Pos.CENTER);
        pie.setSpacing(25);
        HBox.setHgrow(pie_texto, Priority.ALWAYS);

VBox contenido = new VBox(
        20, encabezado_logo, panel_formulario,
        titulo_tabla, tabla, pie);

        contenido.setPadding(new Insets(28));
        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";");

        VBox.setVgrow(tabla, Priority.ALWAYS);

        BorderPane raiz = new BorderPane();
        raiz.setCenter(contenido);
        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";");

        Scene escena = new Scene(raiz);

        ventana.setTitle(
                "BANGBOO-TICKET - Gestionar localidades");
        ventana.setScene(escena);

        ventana_utilidad.configurar(ventana);

        cargar_localidades();
        ventana.show();
    }

    private void registrar_localidad() {

        String nombre = campo_nombre.getText();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrar_alerta(
                    Alert.AlertType.WARNING,
                    "Campo obligatorio",
                    "Escribe el nombre de la localidad.");
            return;
        }

        try {
            localidad nueva = new localidad();
            nueva.setNombre(nombre.trim());

            dao_localidades.registrar_localidad(nueva);

            campo_nombre.clear();
            cargar_localidades();

            mostrar_alerta(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "La localidad se registró correctamente.");

        } catch (SQLException | IOException
                | IllegalArgumentException error) {

            mostrar_alerta(
                    Alert.AlertType.ERROR,
                    "No se pudo registrar",
                    error.getMessage());
        }
    }
    
    private void eliminar_localidad() {

    localidad seleccionada =
            tabla.getSelectionModel().getSelectedItem();

    if (seleccionada == null) {

        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Selecciona una localidad",
                "Debes seleccionar una localidad de la tabla "
                + "para poder eliminarla.");

        return;
    }

    Alert confirmacion = new Alert(
            Alert.AlertType.CONFIRMATION);

    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText(
            "¿Deseas eliminar esta localidad?");

    confirmacion.setContentText(
            "Localidad: " + seleccionada.getNombre()
            + "\nID: " + seleccionada.getId_localidad()
            + "\n\nEsta acción no se puede deshacer.");

    java.util.Optional<javafx.scene.control.ButtonType> respuesta =
            confirmacion.showAndWait();

    if (respuesta.isPresent()
            && respuesta.get()
            == javafx.scene.control.ButtonType.OK) {

        try {

            dao_localidades.eliminar_localidad(
                    seleccionada.getId_localidad());

            cargar_localidades();

            mostrar_alerta(
                    Alert.AlertType.INFORMATION,
                    "Eliminación exitosa",
                    "La localidad se eliminó correctamente.");

        } catch (SQLException | IOException error) {

            mostrar_alerta(
                    Alert.AlertType.ERROR,
                    "No se pudo eliminar",
                    error.getMessage());
        }
    }
}

    private void cargar_localidades() {

        try {
            List<localidad> localidades =
                    dao_localidades.listar_localidades();

            ObservableList<localidad> datos =
                    FXCollections.observableArrayList(
                            localidades);

            tabla.setItems(datos);

        } catch (SQLException | IOException error) {

            mostrar_alerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar localidades",
                    error.getMessage());
        }
    }

    private String estilo_boton() {

        return "-fx-background-color: " + NARANJA + ";"
                + "-fx-text-fill: #101827;"
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 13px;"
                + "-fx-background-radius: 8px;"
                + "-fx-padding: 11px 18px;"
                + "-fx-cursor: hand;";
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
}
