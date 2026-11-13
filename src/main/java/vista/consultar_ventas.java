
package vista;

import dao.venta_dao;
import servicio.sesion_usuario;
import utilidades.ventana_utilidad;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import utilidades.encabezado_utilidad;

import java.time.LocalDate;
import javafx.scene.control.DatePicker;


public class consultar_ventas {

    private final Stage ventana;
    private final sesion_usuario sesion;
    private final venta_dao dao_ventas;

    private Scene escena_anterior;
    private TableView<String[]> tabla;
    private Label etiqueta_resumen;
    private DatePicker fecha_inicial;
    private DatePicker fecha_final;

    private final String FONDO = "#101827";
    private final String PANEL = "#1C2940";
    private final String NARANJA = "#FF9D42";
    private final String TEXTO = "#F4F7FC";
    private final String SUAVE = "#AAB7CC";

    public consultar_ventas(
            Stage ventana,
            sesion_usuario sesion) {

        this.ventana = ventana;
        this.sesion = sesion;
        this.dao_ventas = new venta_dao();
    }

    public void mostrar() {

        escena_anterior = ventana.getScene();

HBox encabezado_logo = encabezado_utilidad.crear(
        "BANGBOO-TICKET"
);

Label subtitulo = new Label(
        sesion.es_administrador()
        ? "Consulta de todas las ventas registradas"
        : "Consulta de tus ventas registradas"
);

subtitulo.setTextFill(Color.web(SUAVE));
subtitulo.setFont(Font.font("Arial", 14));

VBox encabezado_texto = new VBox(
        6,
        encabezado_logo,
        subtitulo
);

encabezado_texto.setAlignment(Pos.CENTER_LEFT);

        fecha_inicial = new DatePicker();
fecha_final = new DatePicker();

fecha_inicial.setPromptText("Fecha inicial");
fecha_final.setPromptText("Fecha final");

fecha_inicial.setStyle(
        "-fx-background-color: #1C2940;"
        + "-fx-text-fill: #F4F7FC;"
);

fecha_final.setStyle(
        "-fx-background-color: #1C2940;"
        + "-fx-text-fill: #F4F7FC;"
);

        tabla = new TableView<>();
        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        tabla.setPlaceholder(
                new Label("No hay ventas para mostrar."));

        agregar_columna("N.º VENTA", 0);
        agregar_columna("FECHA", 1);
        agregar_columna("VENDEDOR", 2);
        agregar_columna("BOLETOS", 3);
        agregar_columna("TOTAL", 4);


tabla.setStyle(
        "-fx-background-color: #1C2940;"
        + "-fx-control-inner-background: #1C2940;"
        + "-fx-text-background-color: #F4F7FC;"
        + "-fx-table-cell-border-color: #34445F;"
        + "-fx-border-color: #34445F;"
        + "-fx-font-size: 13px;"
);

Label etiqueta_fecha_inicial =
        new Label("FECHA INICIAL");

etiqueta_fecha_inicial.setTextFill(Color.web(TEXTO));
etiqueta_fecha_inicial.setFont(
        Font.font("Arial", 12));

Label etiqueta_fecha_final =
        new Label("FECHA FINAL");

etiqueta_fecha_final.setTextFill(Color.web(TEXTO));
etiqueta_fecha_final.setFont(
        Font.font("Arial", 12));

VBox grupo_fecha_inicial = new VBox(
        7,
        etiqueta_fecha_inicial,
        fecha_inicial
);

VBox grupo_fecha_final = new VBox(
        7,
        etiqueta_fecha_final,
        fecha_final
);

Button boton_consultar_fechas =
        new Button("CONSULTAR");

boton_consultar_fechas.setStyle(estilo_boton());

boton_consultar_fechas.setOnAction(
        evento -> consultar_por_fechas());

Button boton_mostrar_todas =
        new Button("MOSTRAR TODAS");

boton_mostrar_todas.setStyle(
        "-fx-background-color: #263550;"
        + "-fx-text-fill: #F4F7FC;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 8px;"
        + "-fx-padding: 11px 18px;"
        + "-fx-cursor: hand;"
);

boton_mostrar_todas.setOnAction(
        evento -> cargar_ventas());

HBox filtros = new HBox(
        15,
        grupo_fecha_inicial,
        grupo_fecha_final,
        boton_consultar_fechas,
        boton_mostrar_todas
);

filtros.setAlignment(Pos.CENTER_LEFT);
filtros.setPadding(new Insets(15));
filtros.setStyle(
        "-fx-background-color: " + PANEL + ";"
        + "-fx-background-radius: 10px;"
);


        etiqueta_resumen = new Label("Ventas registradas: 0");
        etiqueta_resumen.setTextFill(Color.web(TEXTO));
        etiqueta_resumen.setFont(Font.font("Arial", 15));


Button boton_actualizar = new Button("ACTUALIZAR");
boton_actualizar.setStyle(estilo_boton());
boton_actualizar.setOnAction(evento -> cargar_ventas());

Button boton_volver = new Button("← VOLVER AL MENÚ");

boton_volver.setPrefHeight(40);
boton_volver.setStyle(
        "-fx-background-color: #263550;"
        + "-fx-text-fill: #F3F1EA;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 7;"
        + "-fx-cursor: hand;"
);

boton_volver.setOnAction(evento -> {
    if (escena_anterior != null) {
        ventana.setScene(escena_anterior);
        ventana_utilidad.configurar(ventana);
        ventana.setMaximized(true);
        ventana.show();
    }
});

HBox botones = new HBox(
        12, boton_actualizar, boton_volver
);
botones.setAlignment(Pos.CENTER);

HBox pie = new HBox(
        etiqueta_resumen, botones
);
pie.setAlignment(Pos.CENTER);
pie.setSpacing(25);

HBox.setHgrow(
        etiqueta_resumen,
        javafx.scene.layout.Priority.ALWAYS
);

botones.setMaxWidth(Double.MAX_VALUE);
HBox.setHgrow(
        botones,
        javafx.scene.layout.Priority.ALWAYS
);


BorderPane barra_superior = new BorderPane();
barra_superior.setLeft(encabezado_texto);
barra_superior.setPadding(new Insets(10, 20, 10, 20));

VBox contenido = new VBox(
        18,
        barra_superior,
        filtros,
        tabla,
        pie
);

contenido.setPadding(new Insets(28));
contenido.setStyle("-fx-background-color: " + FONDO + ";");

VBox.setVgrow(
        tabla,
        javafx.scene.layout.Priority.ALWAYS
);



        BorderPane raiz = new BorderPane();
        raiz.setCenter(contenido);
        raiz.setStyle("-fx-background-color: " + FONDO + ";");

        Scene escena = new Scene(raiz);
        ventana.setScene(escena);
        ventana.setTitle("BANGBOO-TICKET - Historial de ventas");

        ventana_utilidad.configurar(ventana);

        cargar_ventas();
        ventana.show();
    }


private void agregar_columna(
        String encabezado,
        int posicion) {

    TableColumn<String[], String> columna =
            new TableColumn<>(encabezado);

    columna.setCellValueFactory(
            datos -> new ReadOnlyStringWrapper(
                    datos.getValue()[posicion]));

    columna.setMinWidth(100);

    // Estilo del encabezado.
    Label etiqueta_encabezado = new Label(encabezado);

    etiqueta_encabezado.setMaxWidth(Double.MAX_VALUE);
    etiqueta_encabezado.setAlignment(Pos.CENTER_LEFT);

    etiqueta_encabezado.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-text-fill: #F4F7FC;"
            + "-fx-font-size: 13px;"
            + "-fx-font-weight: bold;"
            + "-fx-padding: 12px 8px 12px 8px;");

    columna.setText("");
    columna.setGraphic(etiqueta_encabezado);

    tabla.getColumns().add(columna);
}

    private void consultar_por_fechas() {

    LocalDate inicio = fecha_inicial.getValue();
    LocalDate fin = fecha_final.getValue();

    if (inicio == null || fin == null) {

        Alert alerta = new Alert(
                Alert.AlertType.WARNING);

        alerta.setTitle("Fechas incompletas");
        alerta.setHeaderText(
                "Debes seleccionar ambas fechas");

        alerta.setContentText(
                "Selecciona una fecha inicial "
                + "y una fecha final.");

        alerta.showAndWait();
        return;
    }

    if (inicio.isAfter(fin)) {

        Alert alerta = new Alert(
                Alert.AlertType.WARNING);

        alerta.setTitle("Rango incorrecto");
        alerta.setHeaderText(
                "Las fechas no son válidas");

        alerta.setContentText(
                "La fecha inicial no puede ser "
                + "posterior a la fecha final.");

        alerta.showAndWait();
        return;
    }

    try {

        boolean consultar_todas =
                sesion.es_administrador();

        int id_vendedor =
                sesion.obtener_usuario_actual()
                        .getId_usuario();

        List<String[]> resultados = dao_ventas.consultar_ventas(
                        id_vendedor,
                        consultar_todas,
                        inicio,
                        fin);

        ObservableList<String[]> filas =
                FXCollections.observableArrayList(
                        resultados);

        tabla.setItems(filas);

        etiqueta_resumen.setText(
                "Ventas encontradas: " + filas.size());

    } 
    catch (SQLException | IOException
            | RuntimeException error) {

        Alert alerta = new Alert(
                Alert.AlertType.ERROR);

        alerta.setTitle("Error al consultar ventas");
        alerta.setHeaderText(
                "No se pudo realizar la consulta");

        alerta.setContentText(
                error.getMessage() == null
                ? "Ocurrió un error inesperado."
                : error.getMessage());

        alerta.showAndWait();
    }
}

    private void cargar_ventas() {

        try {
            boolean consultar_todas =
                    sesion.es_administrador();

            int id_vendedor =
                    sesion.obtener_usuario_actual()
                            .getId_usuario();

            List<String[]> resultados =
                    dao_ventas.consultar_ventas(
                            id_vendedor,
                            consultar_todas);

            ObservableList<String[]> filas =
                    FXCollections.observableArrayList(
                            resultados);

            tabla.setItems(filas);

            etiqueta_resumen.setText(
                    "Ventas registradas: " + filas.size());

        } catch (SQLException | IOException
                | RuntimeException error) {

            Alert alerta = new Alert(
                    Alert.AlertType.ERROR);

            alerta.setTitle("Error al consultar ventas");
            alerta.setHeaderText(
                    "No se pudo cargar el historial");

            alerta.setContentText(
                    error.getMessage() == null
                    ? "Ocurrió un error inesperado."
                    : error.getMessage());

            alerta.showAndWait();
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
}
