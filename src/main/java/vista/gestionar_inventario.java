
package vista;

import dao.inventario_boletos_dao;
import dao.concierto_dao;
import dao.localidad_dao;

import modelo.inventario_boletos;
import modelo.concierto;
import modelo.localidad;

import servicio.sesion_usuario;
import utilidades.ventana_utilidad;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Stage;

import javafx.util.StringConverter;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utilidades.encabezado_utilidad;

import javafx.scene.control.ButtonType;

public class gestionar_inventario {

    private final Stage ventana;
    private final sesion_usuario sesion;

    private final inventario_boletos_dao dao_inventario;
    private final concierto_dao dao_conciertos;
    private final localidad_dao dao_localidades;

    private Scene escena_anterior;

    private TableView<inventario_boletos> tabla;

    private ComboBox<concierto> campo_concierto;
    private ComboBox<localidad> campo_localidad;

    private TextField campo_precio;
    private TextField campo_cantidad;

    private final ObservableList<inventario_boletos> inventarios =
            FXCollections.observableArrayList();

    private final Map<Integer, String> nombres_conciertos =
            new HashMap<>();

    private final Map<Integer, String> nombres_localidades =
            new HashMap<>();

    private final String FONDO = "#101827";
    private final String PANEL = "#1C2940";
    private final String NARANJA = "#FF9D42";
    private final String TEXTO = "#F4F7FC";
    private final String SUAVE = "#AAB7CC";

    public gestionar_inventario(
            Stage ventana,
            sesion_usuario sesion) {

        this.ventana = ventana;
        this.sesion = sesion;

        this.dao_inventario = new inventario_boletos_dao();
        this.dao_conciertos = new concierto_dao();
        this.dao_localidades = new localidad_dao();
    }

    public void mostrar() {

        if (!sesion.es_administrador()) {
            mostrar_alerta(
                    Alert.AlertType.ERROR,
                    "Acceso denegado",
                    "Solo el administrador puede gestionar "
                    + "el inventario.");

            return;
        }

        escena_anterior = ventana.getScene();

        BorderPane raiz = new BorderPane();
        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";");

        raiz.setPadding(new Insets(25));


Label subtitulo = new Label(
        "Administra los precios y las existencias "
        + "de cada concierto.");

subtitulo.setStyle(
        "-fx-text-fill: " + SUAVE + ";"
        + "-fx-font-size: 14px;");

HBox encabezado_logo = new HBox(
        12,
        encabezado_utilidad.crear("GESTIONAR INVENTARIO"),
        subtitulo
);

encabezado_logo.setAlignment(Pos.CENTER_LEFT);
encabezado_logo.setPadding(new Insets(0, 0, 22, 0));

raiz.setTop(encabezado_logo);


        tabla = new TableView<>();
        tabla.setItems(inventarios);
        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        tabla.setPlaceholder(
                new Label("No hay inventarios registrados."));

        tabla.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-control-inner-background: " + PANEL + ";"
                + "-fx-text-background-color: " + TEXTO + ";"
                + "-fx-border-color: #34445F;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;");

        crear_columnas();

        Label titulo_formulario = new Label(
        "REGISTRAR O ACTUALIZAR INVENTARIO");

        titulo_formulario.setStyle(
                "-fx-text-fill: " + NARANJA + ";"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;");

        campo_concierto = new ComboBox<>();
        campo_concierto.setPromptText("Selecciona un concierto");
        campo_concierto.setMaxWidth(Double.MAX_VALUE);

        campo_localidad = new ComboBox<>();
        campo_localidad.setPromptText("Selecciona una localidad");
        campo_localidad.setMaxWidth(Double.MAX_VALUE);

        campo_precio = new TextField();
        campo_precio.setPromptText("Ejemplo: 250.00");

        campo_cantidad = new TextField();
        campo_cantidad.setPromptText("Ejemplo: 100");

        estilizar_combo(campo_concierto);
        estilizar_combo(campo_localidad);
        estilizar_campo(campo_precio);
        estilizar_campo(campo_cantidad);

        GridPane formulario = new GridPane();
        formulario.setHgap(15);
        formulario.setVgap(12);

        formulario.add(
                crear_etiqueta("Concierto"), 0, 0);
        formulario.add(campo_concierto, 0, 1);

        formulario.add(
                crear_etiqueta("Localidad"), 1, 0);
        formulario.add(campo_localidad, 1, 1);

        formulario.add(
                crear_etiqueta("Precio (Q)"), 0, 2);
        formulario.add(campo_precio, 0, 3);

        formulario.add(
                crear_etiqueta("Cantidad total"), 1, 2);
        formulario.add(campo_cantidad, 1, 3);

        formulario.setPrefWidth(Double.MAX_VALUE);

        GridPane.setHgrow(
                campo_concierto,
                javafx.scene.layout.Priority.ALWAYS);

        GridPane.setHgrow(
                campo_localidad,
                javafx.scene.layout.Priority.ALWAYS);

        GridPane.setHgrow(
                campo_precio,
                javafx.scene.layout.Priority.ALWAYS);

        GridPane.setHgrow(
                campo_cantidad,
                javafx.scene.layout.Priority.ALWAYS);

        Button boton_registrar =
                crear_boton("REGISTRAR INVENTARIO");

        Button boton_actualizar_lista =
        crear_boton("ACTUALIZAR LISTA");

        Button boton_guardar_cambios =
        crear_boton("ACTUALIZAR INVENTARIO");
        
        Button boton_eliminar =
        crear_boton("ELIMINAR INVENTARIO");
        
        Button boton_limpiar =
        crear_boton("LIMPIAR");

boton_limpiar.setStyle(
        "-fx-background-color: #263550;"
        + "-fx-text-fill: #F4F7FC;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 8;"
        + "-fx-padding: 11 18 11 18;"
        + "-fx-cursor: hand;"
);

boton_limpiar.setOnAction(
        evento -> limpiar_formulario()
);

boton_eliminar.setStyle(
        "-fx-background-color: #B83F50;"
        + "-fx-text-fill: white;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 8;"
        + "-fx-padding: 11 18 11 18;"
        + "-fx-cursor: hand;"
);

        boton_registrar.setOnAction(
                evento -> registrar_inventario());

        boton_actualizar_lista.setOnAction(
        evento -> cargar_datos());

boton_guardar_cambios.setOnAction(
        evento -> actualizar_inventario());
        
        boton_eliminar.setOnAction(
        evento -> eliminar_inventario());

HBox botones_formulario = new HBox(
        15,
        boton_registrar,
        boton_actualizar_lista,
        boton_guardar_cambios,
        boton_eliminar,
        boton_limpiar
);

        botones_formulario.setAlignment(Pos.CENTER_LEFT);

        VBox panel_formulario = new VBox(
                18,
                titulo_formulario,
                formulario,
                botones_formulario);

        panel_formulario.setPadding(new Insets(22));
        panel_formulario.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 12;");

        VBox contenido = new VBox(
                22,
                crear_etiqueta("INVENTARIOS REGISTRADOS"),
                tabla,
                panel_formulario);

        contenido.setFillWidth(true);
        VBox.setVgrow(tabla, javafx.scene.layout.Priority.ALWAYS);

        raiz.setCenter(contenido);

Button boton_volver = crear_boton("← VOLVER AL MENÚ");

boton_volver.setPrefHeight(40);
boton_volver.setStyle(
        "-fx-background-color: #263550;"
        + "-fx-text-fill: #F3F1EA;"
        + "-fx-font-weight: bold;"
        + "-fx-background-radius: 7;"
        + "-fx-cursor: hand;"
);

boton_volver.setOnAction(evento -> volver());


        HBox pie = new HBox(boton_volver);
        pie.setAlignment(Pos.CENTER);
        pie.setPadding(new Insets(20, 0, 0, 0));

        raiz.setBottom(pie);

        Scene escena = new Scene(raiz);

        ventana.setScene(escena);
        ventana_utilidad.configurar(ventana);
        ventana.setMaximized(true);
        ventana.show();

        cargar_datos();
        
        tabla.getSelectionModel().selectedItemProperty()
        .addListener((observable, anterior, seleccionado) -> {

            if (seleccionado != null) {
                seleccionar_inventario(seleccionado);
            }
        });
    }


private void crear_columnas() {

    TableColumn<inventario_boletos, String> columna_id =
            new TableColumn<>("ID");

    columna_id.setCellValueFactory(fila ->
            new ReadOnlyStringWrapper(
                    String.valueOf(
                            fila.getValue().getId_inventario())));

    TableColumn<inventario_boletos, String> columna_concierto =
            new TableColumn<>("Concierto");

    columna_concierto.setCellValueFactory(fila ->
            new ReadOnlyStringWrapper(
                    nombres_conciertos.getOrDefault(
                            fila.getValue().getId_concierto(),
                            "ID " + fila.getValue().getId_concierto())));

    TableColumn<inventario_boletos, String> columna_localidad =
            new TableColumn<>("Localidad");

    columna_localidad.setCellValueFactory(fila ->
            new ReadOnlyStringWrapper(
                    nombres_localidades.getOrDefault(
                            fila.getValue().getId_localidad(),
                            "ID " + fila.getValue().getId_localidad())));

    TableColumn<inventario_boletos, BigDecimal> columna_precio =
            new TableColumn<>("Precio");

    columna_precio.setCellValueFactory(fila ->
            new ReadOnlyObjectWrapper<>(
                    fila.getValue().getPrecio()));

    columna_precio.setCellFactory(columna ->
            new javafx.scene.control.TableCell<
                    inventario_boletos, BigDecimal>() {

        @Override
        protected void updateItem(BigDecimal precio, boolean vacio) {
            super.updateItem(precio, vacio);

            if (vacio || precio == null) {
                setText(null);
            } else {
                setText("Q" + precio.toPlainString());
            }
        }
    });

    TableColumn<inventario_boletos, Number> columna_total =
            new TableColumn<>("Cantidad total");

    columna_total.setCellValueFactory(fila ->
            new ReadOnlyObjectWrapper<>(
                    fila.getValue().getCantidad_total()));

    TableColumn<inventario_boletos, Number> columna_disponible =
            new TableColumn<>("Disponibles");

    columna_disponible.setCellValueFactory(fila ->
            new ReadOnlyObjectWrapper<>(
                    fila.getValue().getCantidad_disponible()));

    tabla.getColumns().setAll(
            columna_id,
            columna_concierto,
            columna_localidad,
            columna_precio,
            columna_total,
            columna_disponible);

    // Fondo oscuro y texto claro para la tabla.

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


    // Estilo individual de los encabezados.
    for (TableColumn<inventario_boletos, ?> columna
            : tabla.getColumns()) {

        Label encabezado = new Label(columna.getText());

        encabezado.setMaxWidth(Double.MAX_VALUE);
        encabezado.setAlignment(Pos.CENTER_LEFT);

        encabezado.setStyle(
                "-fx-background-color: #080D18;"
                + "-fx-text-fill: #F4F7FC;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-padding: 12px 8px 12px 8px;");

        columna.setText("");
        columna.setGraphic(encabezado);
    }
}


    private void cargar_datos() {

        try {

            List<concierto> conciertos =
                    dao_conciertos.listar_conciertos();

            List<localidad> localidades =
                    dao_localidades.listar_localidades();

            List<inventario_boletos> lista_inventarios =
                    dao_inventario.listar_inventario();

            nombres_conciertos.clear();

            for (concierto actual : conciertos) {
                nombres_conciertos.put(
                        actual.getId_concierto(),
                        actual.getTitulo_evento());
            }

            nombres_localidades.clear();

            for (localidad actual : localidades) {
                nombres_localidades.put(
                        actual.getId_localidad(),
                        actual.getNombre());
            }

            campo_concierto.setItems(
                    FXCollections.observableArrayList(conciertos));

            campo_localidad.setItems(
                    FXCollections.observableArrayList(localidades));

            campo_concierto.setConverter(
                    new StringConverter<concierto>() {

                @Override
                public String toString(concierto actual) {
                    return actual == null
                            ? ""
                            : actual.getTitulo_evento();
                }

                @Override
                public concierto fromString(String texto) {
                    return null;
                }
            });

            campo_localidad.setConverter(
                    new StringConverter<localidad>() {

                @Override
                public String toString(localidad actual) {
                    return actual == null
                            ? ""
                            : actual.getNombre();
                }

                @Override
                public localidad fromString(String texto) {
                    return null;
                }
            });

            inventarios.setAll(lista_inventarios);
            
            tabla.getSelectionModel().selectedItemProperty()
        .addListener((observable, anterior, seleccionado) -> {

            if (seleccionado != null) {

                seleccionar_inventario(seleccionado);
            }
        });

            tabla.refresh();

        } catch (SQLException | IOException error) {

            mostrar_alerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar",
                    "No se pudieron cargar los datos del inventario.\n"
                    + error.getMessage());
        }
    }

    private void registrar_inventario() {

        concierto concierto_seleccionado =
                campo_concierto.getValue();

        localidad localidad_seleccionada =
                campo_localidad.getValue();

        if (concierto_seleccionado == null
                || localidad_seleccionada == null
                || campo_precio.getText().trim().isEmpty()
                || campo_cantidad.getText().trim().isEmpty()) {

            mostrar_alerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Selecciona un concierto, una localidad "
                    + "y completa el precio y la cantidad.");

            return;
        }

        try {

            BigDecimal precio = new BigDecimal(
                    campo_precio.getText().trim());

            int cantidad = Integer.parseInt(
                    campo_cantidad.getText().trim());

            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                mostrar_alerta(
                        Alert.AlertType.WARNING,
                        "Precio inválido",
                        "El precio no puede ser negativo.");
                return;
            }

            if (cantidad <= 0) {
                mostrar_alerta(
                        Alert.AlertType.WARNING,
                        "Cantidad inválida",
                        "La cantidad total debe ser mayor que cero.");
                return;
            }

            inventario_boletos nuevo =
                    new inventario_boletos();

            nuevo.setId_concierto(
                    concierto_seleccionado.getId_concierto());

            nuevo.setId_localidad(
                    localidad_seleccionada.getId_localidad());

            nuevo.setPrecio(precio);
            nuevo.setCantidad_total(cantidad);
            nuevo.setCantidad_disponible(cantidad);

            dao_inventario.registrar_inventario(nuevo);

            campo_precio.clear();
            campo_cantidad.clear();

            cargar_datos();

            mostrar_alerta(
                    Alert.AlertType.INFORMATION,
                    "Inventario registrado",
                    "El inventario se registró correctamente.");

        } catch (NumberFormatException error) {

            mostrar_alerta(
                    Alert.AlertType.WARNING,
                    "Datos inválidos",
                    "Escribe un precio válido, por ejemplo 250.00, "
                    + "y una cantidad entera, por ejemplo 100.");

        } catch (SQLException | IOException error) {

            mostrar_alerta(
                    Alert.AlertType.ERROR,
                    "Error al registrar",
                    "No se pudo registrar el inventario.\n"
                    + error.getMessage());

        } catch (IllegalArgumentException error) {

            mostrar_alerta(
                    Alert.AlertType.WARNING,
                    "Datos inválidos",
                    error.getMessage());
        }
    }
    
    private void seleccionar_inventario(
        inventario_boletos seleccionado) {

    for (concierto actual : campo_concierto.getItems()) {

        if (actual.getId_concierto()
                == seleccionado.getId_concierto()) {

            campo_concierto.setValue(actual);
            break;
        }
    }

    for (localidad actual : campo_localidad.getItems()) {

        if (actual.getId_localidad()
                == seleccionado.getId_localidad()) {

            campo_localidad.setValue(actual);
            break;
        }
    }

    campo_precio.setText(
            seleccionado.getPrecio().toPlainString());

    campo_cantidad.setText(
            String.valueOf(seleccionado.getCantidad_total()));
}
    
    private void eliminar_inventario() {

    inventario_boletos seleccionado =
            tabla.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {

        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Selecciona un inventario",
                "Debes seleccionar un inventario "
                + "de la tabla para eliminarlo.");

        return;
    }

    Alert confirmacion = new Alert(
            Alert.AlertType.CONFIRMATION);

    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText("¿Eliminar este inventario?");

    confirmacion.setContentText(
            "ID del inventario: "
            + seleccionado.getId_inventario()
            + "\n\nEsta acción eliminará el registro "
            + "seleccionado si no tiene boletos o ventas "
            + "relacionados."
    );

    confirmacion.getButtonTypes().setAll(
            ButtonType.YES,
            ButtonType.NO
    );

    if (confirmacion.showAndWait().orElse(
            ButtonType.NO) != ButtonType.YES) {

        return;
    }

    try {

        dao_inventario.eliminar_inventario(
                seleccionado.getId_inventario());

        cargar_datos();

        mostrar_alerta(
                Alert.AlertType.INFORMATION,
                "Inventario eliminado",
                "El inventario se eliminó correctamente.");

    } catch (SQLException | IOException error) {

        mostrar_alerta(
                Alert.AlertType.ERROR,
                "No se pudo eliminar",
                error.getMessage());

    } catch (IllegalArgumentException error) {

        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Datos inválidos",
                error.getMessage());
    }
}

    private Label crear_etiqueta(String texto) {

        Label etiqueta = new Label(texto);

        etiqueta.setStyle(
                "-fx-text-fill: " + TEXTO + ";"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;");

        return etiqueta;
    }

private void estilizar_campo(TextField campo) {

    campo.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-text-fill: #F4F7FC;"
            + "-fx-prompt-text-fill: #AAB7CC;"
            + "-fx-highlight-fill: #FF9D42;"
            + "-fx-highlight-text-fill: #080D18;"
            + "-fx-border-color: #FF9D42;"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 7;"
            + "-fx-background-radius: 7;"
            + "-fx-padding: 11;"
            + "-fx-font-size: 14px;");
}



private <T> void estilizar_combo(ComboBox<T> combo) {

    combo.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-background-radius: 7;"
            + "-fx-border-color: #FF9D42;"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 7;"
            + "-fx-font-size: 14px;"
            + "-fx-mark-color: #FF9D42;");

    combo.setPrefHeight(42);

    // Texto visible cuando se selecciona un elemento.
    combo.setButtonCell(new javafx.scene.control.ListCell<T>() {

        @Override
        protected void updateItem(T elemento, boolean vacio) {
            super.updateItem(elemento, vacio);

            if (vacio || elemento == null) {
                setText(combo.getPromptText());
            } else if (combo.getConverter() != null) {
                setText(combo.getConverter().toString(elemento));
            } else {
                setText(elemento.toString());
            }

            setTextFill(Color.web("#F4F7FC"));

            setStyle(
                    "-fx-background-color: #080D18;"
                    + "-fx-text-fill: #F4F7FC;"
                    + "-fx-padding: 5px;");
        }
    });

    // Texto de los elementos dentro de la lista desplegada.
    combo.setCellFactory(lista ->
            new javafx.scene.control.ListCell<T>() {

        @Override
        protected void updateItem(T elemento, boolean vacio) {
            super.updateItem(elemento, vacio);

            if (vacio || elemento == null) {
                setText(null);
            } else if (combo.getConverter() != null) {
                setText(combo.getConverter().toString(elemento));
            } else {
                setText(elemento.toString());
            }

            setTextFill(Color.web("#F4F7FC"));

            setStyle(
                    "-fx-background-color: #080D18;"
                    + "-fx-text-fill: #F4F7FC;"
                    + "-fx-padding: 9px;");
        }
    });
}


    private Button crear_boton(String texto) {

        Button boton = new Button(texto);

        boton.setTextFill(Color.web(TEXTO));

        boton.setStyle(
                "-fx-background-color: " + NARANJA + ";"
                + "-fx-text-fill: " + FONDO + ";"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 11 18 11 18;"
                + "-fx-cursor: hand;");

        boton.setOnMouseEntered(evento ->
                boton.setOpacity(0.85));

        boton.setOnMouseExited(evento ->
                boton.setOpacity(1.0));

        return boton;
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

    private void volver() {

        if (escena_anterior != null) {

            ventana.setScene(escena_anterior);
            ventana_utilidad.configurar(ventana);
            ventana.setMaximized(true);
            ventana.show();
        }
    }
    
    private void actualizar_inventario() {

    inventario_boletos seleccionado =
            tabla.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {

        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Selecciona un inventario",
                "Selecciona un registro de la tabla "
                + "antes de actualizarlo.");

        return;
    }

    concierto concierto_seleccionado =
            campo_concierto.getValue();

    localidad localidad_seleccionada =
            campo_localidad.getValue();

    if (concierto_seleccionado == null
            || localidad_seleccionada == null
            || campo_precio.getText().trim().isEmpty()
            || campo_cantidad.getText().trim().isEmpty()) {

        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Datos incompletos",
                "Completa todos los campos antes de actualizar.");

        return;
    }

    try {

        BigDecimal precio = new BigDecimal(
                campo_precio.getText().trim());

        int cantidad = Integer.parseInt(
                campo_cantidad.getText().trim());

        if (precio.compareTo(BigDecimal.ZERO) < 0) {

            mostrar_alerta(
                    Alert.AlertType.WARNING,
                    "Precio inválido",
                    "El precio no puede ser negativo.");

            return;
        }

        if (cantidad <= 0) {

            mostrar_alerta(
                    Alert.AlertType.WARNING,
                    "Cantidad inválida",
                    "La cantidad debe ser mayor que cero.");

            return;
        }

        inventario_boletos actualizado =
                new inventario_boletos();

        actualizado.setId_inventario(
                seleccionado.getId_inventario());

        actualizado.setId_concierto(
                concierto_seleccionado.getId_concierto());

        actualizado.setId_localidad(
                localidad_seleccionada.getId_localidad());

        actualizado.setPrecio(precio);

        actualizado.setCantidad_total(cantidad);

        dao_inventario.actualizar_inventario(actualizado);

        campo_concierto.setValue(null);
        campo_localidad.setValue(null);
        campo_precio.clear();
        campo_cantidad.clear();

        cargar_datos();

        tabla.getSelectionModel().clearSelection();

        mostrar_alerta(
                Alert.AlertType.INFORMATION,
                "Inventario actualizado",
                "Los cambios se guardaron correctamente.");

    } catch (NumberFormatException error) {

        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Datos inválidos",
                "Escribe un precio válido y una cantidad entera.");

    } catch (SQLException | IOException error) {

        mostrar_alerta(
                Alert.AlertType.ERROR,
                "Error al actualizar",
                error.getMessage());

    } catch (IllegalArgumentException error) {

        mostrar_alerta(
                Alert.AlertType.WARNING,
                "Datos inválidos",
                error.getMessage());
    }
}
    
    private void limpiar_formulario() {

    campo_concierto.setValue(null);
    campo_localidad.setValue(null);

    campo_precio.clear();
    campo_cantidad.clear();

    tabla.getSelectionModel().clearSelection();

    campo_concierto.getEditor().clear();
    campo_localidad.getEditor().clear();
}
}
