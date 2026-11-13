
package vista;

import reportes.reporte_ventas_dao;
import servicio.sesion_usuario;
import utilidades.ventana_utilidad;
import utilidades.encabezado_utilidad;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ver_reportes {

    private final Stage ventana;
    private final sesion_usuario sesion;

    private final reporte_ventas_dao dao =
            new reporte_ventas_dao();

    private static final String FONDO = "#080D18";
    private static final String PANEL = "#101827";
    private static final String NARANJA = "#FF9D42";
    private static final String TEXTO = "#F4F7FC";
    private static final String SUAVE = "#AAB7CC";

    private BarChart<String, Number> grafica;

    public ver_reportes(Stage ventana, sesion_usuario sesion) {
        this.ventana = ventana;
        this.sesion = sesion;
    }

    public void mostrar() {

        if (!sesion.es_administrador()) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Acceso denegado",
                    "Solo el administrador puede consultar estos reportes."
            );

            new menu_principal(ventana, sesion).mostrar();
            return;
        }

        Label subtitulo = new Label(
                "Resumen general de ventas e ingresos"
        );

        subtitulo.setTextFill(Color.web(SUAVE));

        Button volver = new Button("← VOLVER AL MENÚ");

        volver.setStyle(
                "-fx-background-color: #263550;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 7;"
                + "-fx-cursor: hand;"
        );

        volver.setOnAction(evento ->
                new menu_principal(ventana, sesion).mostrar()
        );

        HBox encabezado_logo = new HBox(
                12,
                encabezado_utilidad.crear("REPORTES Y ESTADÍSTICAS"),
                subtitulo
        );

        encabezado_logo.setAlignment(Pos.CENTER_LEFT);

        BorderPane encabezado = new BorderPane();

        encabezado.setLeft(encabezado_logo);
        encabezado.setRight(volver);
        encabezado.setAlignment(volver, Pos.CENTER_RIGHT);
        encabezado.setPadding(new Insets(10, 30, 10, 30));

        encabezado.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-border-color: #263550;"
                + "-fx-border-width: 0 0 1 0;"
        );

        // TARJETAS DE RESUMEN

        Label titulo_tarjetas = new Label(
                "RESUMEN DE ACTIVIDAD"
        );

        titulo_tarjetas.setFont(Font.font("Arial", 18));
        titulo_tarjetas.setTextFill(Color.web(NARANJA));

        Label ingresos = crear_valor("Q0.00");
        Label ventas = crear_valor("0");
        Label boletos = crear_valor("0");

        VBox tarjeta_ingresos = crear_tarjeta(
                "INGRESOS TOTALES", ingresos
        );

        VBox tarjeta_ventas = crear_tarjeta(
                "VENTAS REGISTRADAS", ventas
        );

        VBox tarjeta_boletos = crear_tarjeta(
                "BOLETOS VENDIDOS", boletos
        );

        GridPane tarjetas = new GridPane();

        tarjetas.setHgap(18);
        tarjetas.setVgap(18);
        tarjetas.setAlignment(Pos.CENTER);
        tarjetas.setMaxWidth(Double.MAX_VALUE);

        tarjetas.add(tarjeta_ingresos, 0, 0);
        tarjetas.add(tarjeta_ventas, 1, 0);
        tarjetas.add(tarjeta_boletos, 2, 0);

        for (int columna = 0; columna < 3; columna++) {

            javafx.scene.layout.ColumnConstraints restriccion =
                    new javafx.scene.layout.ColumnConstraints();

            restriccion.setPercentWidth(33.333);

            restriccion.setHgrow(Priority.ALWAYS);

            tarjetas.getColumnConstraints().add(restriccion);
        }

        // GRÁFICA DE BARRAS

        Label titulo_grafica = new Label(
                "BOLETOS VENDIDOS POR CONCIERTO"
        );

        titulo_grafica.setTextFill(Color.web(NARANJA));
        titulo_grafica.setFont(Font.font("Arial", 18));

        Label descripcion_grafica = new Label(
                "Comparación de entradas vendidas en cada evento"
        );

        descripcion_grafica.setTextFill(Color.web(SUAVE));
        descripcion_grafica.setFont(Font.font("Arial", 12));

        CategoryAxis eje_x = new CategoryAxis();

        eje_x.setLabel("CONCIERTOS");
        eje_x.lookup(".axis-label");
        eje_x.setTickLabelFill(Color.web(TEXTO));
        eje_x.setTickLabelFont(Font.font("Arial", 12));

        NumberAxis eje_y = new NumberAxis();

        eje_y.setLabel("CANTIDAD DE BOLETOS");
        eje_y.setLowerBound(0);
        eje_y.setForceZeroInRange(true);
        eje_y.setTickLabelFill(Color.web(TEXTO));
        eje_y.setTickLabelFont(Font.font("Arial", 12));
        eje_y.setMinorTickVisible(false);

        grafica = new BarChart<>(eje_x, eje_y);

        grafica.setTitle("");
        grafica.setLegendVisible(false);
        grafica.setAnimated(false);
        grafica.setCategoryGap(35);
        grafica.setBarGap(8);

        grafica.setMinHeight(360);
        grafica.setPrefHeight(430);
        grafica.setMaxWidth(Double.MAX_VALUE);

        grafica.setStyle(
        "-fx-background-color: #080D18;"
        + "-fx-plot-background-color: #080D18;"
        + "-fx-text-fill: #FF9D42;"
        + "-fx-padding: 15;"
);
        
        eje_x.setStyle(
        "-fx-tick-label-fill: #FF9D42;"
        + "-fx-tick-label-font-size: 12px;"
);

eje_y.setStyle(
        "-fx-tick-label-fill: #FF9D42;"
        + "-fx-tick-label-font-size: 12px;"
);

        VBox panel_grafica = new VBox(
                12,
                titulo_grafica,
                descripcion_grafica,
                grafica
        );

        panel_grafica.setPadding(new Insets(22));
        panel_grafica.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #344563;"
                + "-fx-border-radius: 12;"
        );

        VBox.setVgrow(grafica, Priority.ALWAYS);
        
        grafica.applyCss();

javafx.scene.Node titulo_x =
        grafica.lookup(".chart-plot-background");

if (titulo_x != null) {
    titulo_x.setStyle(
            "-fx-background-color: #080D18;"
    );
}

        // BOTÓN ACTUALIZAR

        Button actualizar = new Button(
                "ACTUALIZAR REPORTES"
        );

        actualizar.setPrefHeight(42);

        actualizar.setStyle(
                "-fx-background-color: " + NARANJA + ";"
                + "-fx-text-fill: #080D18;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 7;"
                + "-fx-cursor: hand;"
        );

        actualizar.setOnAction(evento ->
                cargar_reportes(ingresos, ventas, boletos)
        );

        // CONTENIDO

        VBox contenido = new VBox(
                22,
                titulo_tarjetas,
                tarjetas,
                panel_grafica,
                actualizar
        );

        contenido.setAlignment(Pos.TOP_CENTER);
        contenido.setPadding(new Insets(30));
        contenido.setFillWidth(true);

        VBox.setVgrow(panel_grafica, Priority.ALWAYS);

        BorderPane raiz = new BorderPane();

        raiz.setTop(encabezado);
        raiz.setCenter(contenido);

        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        Scene escena = new Scene(raiz, 1100, 750);

        ventana.setTitle("BANGBOO-TICKET | Reportes");
        ventana.setScene(escena);

        ventana_utilidad.configurar(ventana);

        ventana.setMaximized(true);
        ventana.show();

        cargar_reportes(ingresos, ventas, boletos);
    }

    private VBox crear_tarjeta(String titulo, Label valor) {

        Label encabezado = new Label(titulo);

        encabezado.setTextFill(Color.web(SUAVE));
        encabezado.setFont(Font.font("Arial", 13));
        encabezado.setWrapText(true);

        VBox tarjeta = new VBox(18, encabezado, valor);

        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(25));
        tarjeta.setMinHeight(130);
        tarjeta.setMaxWidth(Double.MAX_VALUE);

        tarjeta.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #344563;"
                + "-fx-border-radius: 12;"
        );

        GridPane.setHgrow(tarjeta, Priority.ALWAYS);

        return tarjeta;
    }

    private Label crear_valor(String texto) {

        Label valor = new Label(texto);

        valor.setTextFill(Color.web(NARANJA));
        valor.setFont(Font.font("Arial", 27));
        valor.setWrapText(true);

        return valor;
    }

    private void cargar_reportes(
            Label ingresos,
            Label ventas,
            Label boletos) {

        try {

            BigDecimal total = dao.obtener_ingresos_totales();

            if (total == null) {
                total = BigDecimal.ZERO;
            }

            NumberFormat formato = NumberFormat.getNumberInstance(
                    Locale.forLanguageTag("es-GT")
            );

            formato.setMinimumFractionDigits(2);
            formato.setMaximumFractionDigits(2);

            ingresos.setText("Q" + formato.format(total));

            ventas.setText(
                    String.valueOf(dao.obtener_cantidad_ventas())
            );

            boletos.setText(
                    String.valueOf(dao.obtener_boletos_vendidos())
            );

            cargar_grafica();

        } catch (SQLException | IOException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al cargar reportes",
                    ex.getMessage() == null
                            ? "No se pudieron consultar las estadísticas."
                            : ex.getMessage()
            );
        }
    }

    private void cargar_grafica()
            throws SQLException, IOException {

        grafica.getData().clear();

        List<Object[]> datos =
                dao.obtener_boletos_por_concierto();

        XYChart.Series<String, Number> serie =
                new XYChart.Series<>();

        serie.setName("Boletos vendidos");

        for (Object[] fila : datos) {

            String nombre_concierto = (String) fila[0];

            int cantidad_vendida = (int) fila[1];

            XYChart.Data<String, Number> dato =
                    new XYChart.Data<>(
                            nombre_concierto,
                            cantidad_vendida
                    );

            serie.getData().add(dato);
        }

        grafica.getData().add(serie);

        for (XYChart.Data<String, Number> dato
                : serie.getData()) {

            dato.nodeProperty().addListener(
                    (observable, anterior, nodo) -> {

                        if (nodo != null) {

                            nodo.setStyle(
                                    "-fx-bar-fill: " + NARANJA + ";"
                            );

                            Label valor = new Label(
                                    String.valueOf(
                                            dato.getYValue()
                                    )
                            );

                            valor.setTextFill(Color.web(TEXTO));
                            valor.setFont(Font.font("Arial", 12));

                            javafx.scene.control.Tooltip tooltip =
                                    new javafx.scene.control.Tooltip(
                                            dato.getXValue()
                                            + ": "
                                            + dato.getYValue()
                                            + " boletos vendidos"
                                    );

                            javafx.scene.control.Tooltip.install(
                                    nodo, tooltip
                            );
                        }
                    }
            );
        }
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
