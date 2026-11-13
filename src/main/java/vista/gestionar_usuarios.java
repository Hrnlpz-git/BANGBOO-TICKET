
package vista;

import dao.usuario_dao;
import modelo.usuario;
import servicio.sesion_usuario;
import utilidades.ventana_utilidad;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import utilidades.encabezado_utilidad;

public class gestionar_usuarios {

    private final Stage ventana;
    private final sesion_usuario sesion;
    private final usuario_dao dao = new usuario_dao();

    private final TableView<usuario> tabla = new TableView<>();
    private usuario usuario_seleccionado;

    private final TextField campo_usuario = new TextField();
    private final TextField campo_nombre = new TextField();
    private final PasswordField campo_contrasena = new PasswordField();
    private final ComboBox<String> campo_rol = new ComboBox<>();

    private final String FONDO = "#080D18";
    private final String PANEL = "#101827";
    private final String AZUL = "#2855D9";
    private final String NARANJA = "#F07832";
    private final String TEXTO = "#F3F1EA";
    private final String SECUNDARIO = "#9BAAC4";

    public gestionar_usuarios(Stage ventana, sesion_usuario sesion) {
        this.ventana = ventana;
        this.sesion = sesion;
    }

    public void mostrar() {

        if (!sesion.es_administrador()) {
            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Acceso denegado",
                    "Solo el administrador puede gestionar usuarios."
            );
            new menu_principal(ventana, sesion).mostrar();
            return;
        }


Label subtitulo = new Label(
        "Registra, edita usuarios y cambia sus contraseñas"
);
subtitulo.setTextFill(Color.web(SECUNDARIO));

HBox encabezado_logo = new HBox(
        12,
        encabezado_utilidad.crear("GESTIÓN DE USUARIOS"),
        subtitulo
);

encabezado_logo.setAlignment(Pos.CENTER_LEFT);
encabezado_logo.setPadding(new Insets(10, 30, 10, 30));
encabezado_logo.setStyle(
        "-fx-background-color: " + PANEL + ";"
        + "-fx-border-color: #263550;"
        + "-fx-border-width: 0 0 1 0;"
);


        Label titulo_formulario = new Label("DATOS DEL USUARIO");
        titulo_formulario.setFont(Font.font("Arial", 16));
        titulo_formulario.setTextFill(Color.web(NARANJA));

        campo_usuario.setPromptText("Nombre de usuario");
        campo_nombre.setPromptText("Nombre completo");
        campo_contrasena.setPromptText(
                "Contraseña (mínimo 6 caracteres)"
        );

        campo_rol.getItems().setAll("vendedor", "administrador");
        campo_rol.setValue("vendedor");
        campo_rol.setMaxWidth(Double.MAX_VALUE);

        estilizar_campo(campo_usuario);
        estilizar_campo(campo_nombre);
        estilizar_campo(campo_contrasena);
        estilizar_combo(campo_rol);

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(12);

        formulario.add(new Label("Usuario:"), 0, 0);
        formulario.add(campo_usuario, 1, 0);

        formulario.add(new Label("Nombre completo:"), 0, 1);
        formulario.add(campo_nombre, 1, 1);

        formulario.add(new Label("Contraseña:"), 0, 2);
        formulario.add(campo_contrasena, 1, 2);

        formulario.add(new Label("Rol:"), 0, 3);
        formulario.add(campo_rol, 1, 3);

        for (javafx.scene.Node elemento : formulario.getChildren()) {
            if (elemento instanceof Label etiqueta) {
                etiqueta.setTextFill(Color.web(TEXTO));
            }
        }

        Button registrar = crear_boton(
                "REGISTRAR USUARIO", AZUL
        );

        registrar.setOnAction(evento -> registrar_usuario());

        Button guardar = crear_boton(
                "GUARDAR CAMBIOS", "#23865B"
        );

        guardar.setOnAction(evento -> actualizar_usuario());

        Button cambiar_contrasena = crear_boton(
                "CAMBIAR CONTRASEÑA", "#8A5BD6"
        );

        cambiar_contrasena.setOnAction(
                evento -> cambiar_contrasena()
        );

        Button limpiar = crear_boton(
                "LIMPIAR", "#263550"
        );
        Button eliminar = crear_boton("ELIMINAR USUARIO", "#B83232");
        eliminar.setOnAction(evento -> eliminar_usuario());

        limpiar.setOnAction(evento -> limpiar_formulario());

VBox panel_formulario = new VBox(12,
    titulo_formulario, formulario, registrar, guardar,
    cambiar_contrasena, limpiar, eliminar);

        panel_formulario.setPadding(new Insets(20));
        panel_formulario.setPrefWidth(380);
        panel_formulario.setMinWidth(340);
        panel_formulario.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 10;"
        );

        configurar_tabla();

        tabla.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    usuario_seleccionado = seleccionado;

                    if (seleccionado != null) {
                        campo_usuario.setText(
                                seleccionado.getNombre_usuario()
                        );
                        campo_nombre.setText(
                                seleccionado.getNombre_completo()
                        );
                        campo_rol.setValue(seleccionado.getRol());

                        // Nunca mostramos la contraseña guardada.
                        campo_contrasena.clear();
                    }
                });

        Label titulo_tabla = new Label("USUARIOS REGISTRADOS");
        titulo_tabla.setFont(Font.font("Arial", 16));
        titulo_tabla.setTextFill(Color.web(NARANJA));

        Button actualizar = crear_boton(
                "ACTUALIZAR LISTA", AZUL
        );
        actualizar.setOnAction(evento -> cargar_usuarios());

        HBox barra_tabla = new HBox(
                12, titulo_tabla, actualizar
        );
        barra_tabla.setAlignment(Pos.CENTER_LEFT);

        VBox panel_tabla = new VBox(15, barra_tabla, tabla);
        panel_tabla.setPadding(new Insets(20));
        panel_tabla.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-background-radius: 10;"
        );

        HBox centro = new HBox(
                18, panel_formulario, panel_tabla
        );
        centro.setPadding(new Insets(20, 30, 20, 30));
        centro.setAlignment(Pos.TOP_CENTER);

    
        tabla.setPrefWidth(600);
        tabla.setMaxWidth(Double.MAX_VALUE);
        panel_tabla.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(panel_tabla,
                javafx.scene.layout.Priority.ALWAYS);

        Button volver = crear_boton(
                "← VOLVER AL MENÚ", "#263550"
        );

        volver.setOnAction(evento ->
                new menu_principal(ventana, sesion).mostrar()
        );

        HBox pie = new HBox(volver);
        pie.setAlignment(Pos.CENTER_LEFT);
        pie.setPadding(new Insets(15, 30, 20, 30));
        pie.setStyle(
                "-fx-background-color: " + PANEL + ";"
                + "-fx-border-color: #263550;"
                + "-fx-border-width: 1 0 0 0;"
        );

        BorderPane contenido = new BorderPane();
        contenido.setTop(encabezado_logo);
        contenido.setCenter(centro);
        contenido.setBottom(pie);
        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        Scene escena = new Scene(contenido, 1150, 780);
        ventana.setTitle("BANGBOO-TICKET | Gestión de usuarios");
        ventana.setScene(escena);
        ventana_utilidad.configurar(ventana);
        ventana.show();

        cargar_usuarios();
    }

    private void registrar_usuario() {

        char[] contrasena =
                campo_contrasena.getText().toCharArray();

        try {
            dao.registrar_usuario(
                    campo_usuario.getText(),
                    contrasena,
                    campo_nombre.getText(),
                    campo_rol.getValue()
            );

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Registro exitoso",
                    "El usuario se registró correctamente."
            );

            cargar_usuarios();
            limpiar_formulario();

        } catch (SQLException | IOException
                | GeneralSecurityException
                | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo registrar",
                    mensaje_error(ex)
            );

        } finally {
            Arrays.fill(contrasena, '\0');
        }
    }

    private void actualizar_usuario() {

        if (usuario_seleccionado == null) {
            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un usuario",
                    "Selecciona una fila de la tabla antes de guardar cambios."
            );
            return;
        }

        // Evita quitar accidentalmente el rol de administrador
        // de la cuenta que tiene abierta esta sesión.
        int id_actual = sesion.obtener_usuario_actual()
                .getId_usuario();

        if (usuario_seleccionado.getId_usuario() == id_actual
                && !"administrador".equals(campo_rol.getValue())) {

            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "No se puede cambiar el rol",
                    "No puedes quitarte el rol de administrador "
                    + "desde tu propia sesión."
            );
            return;
        }
        
        if (usuario_seleccionado.getId_usuario() == 3
        && !"administrador".equals(campo_rol.getValue())) {

    mostrar_mensaje(
            Alert.AlertType.WARNING,
            "Cuenta protegida",
            "La cuenta de Hernan debe conservar el rol de administrador."
    );

    return;
}

        try {
            dao.actualizar_usuario(
                    usuario_seleccionado.getId_usuario(),
                    campo_usuario.getText(),
                    campo_nombre.getText(),
                    campo_rol.getValue()
            );

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Cambios guardados",
                    "Los datos del usuario se actualizaron correctamente."
            );

            cargar_usuarios();
            limpiar_formulario();

        } catch (SQLException | IOException
                | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "No se pudieron guardar los cambios",
                    mensaje_error(ex)
            );
        }
    }

    private void cambiar_contrasena() {

        if (usuario_seleccionado == null) {
            mostrar_mensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona un usuario",
                    "Selecciona un usuario de la tabla primero."
            );
            return;
        }

        char[] nueva_contrasena =
                campo_contrasena.getText().toCharArray();

        try {
            dao.cambiar_contrasena(
                    usuario_seleccionado.getId_usuario(),
                    nueva_contrasena
            );

            mostrar_mensaje(
                    Alert.AlertType.INFORMATION,
                    "Contraseña actualizada",
                    "La nueva contraseña se guardó correctamente."
            );

            campo_contrasena.clear();

        } catch (SQLException | IOException
                | GeneralSecurityException
                | IllegalArgumentException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo cambiar la contraseña",
                    mensaje_error(ex)
            );

        } finally {
            Arrays.fill(nueva_contrasena, '\0');
        }
    }


private void eliminar_usuario() {

    if (usuario_seleccionado == null) {
        mostrar_mensaje(
                Alert.AlertType.WARNING,
                "Selecciona un usuario",
                "Selecciona un usuario de la tabla antes de eliminarlo."
        );
        return;
    }

    int id_actual = sesion.obtener_usuario_actual().getId_usuario();

    if (usuario_seleccionado.getId_usuario() == id_actual) {
        mostrar_mensaje(
                Alert.AlertType.WARNING,
                "Operación no permitida",
                "No puedes eliminar tu propia cuenta mientras tienes la sesión activa."
        );
        return;
    }

    javafx.scene.control.Alert confirmacion =
            new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION
            );

    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText("¿Eliminar este usuario?");
    confirmacion.setContentText(
            "Usuario: " + usuario_seleccionado.getNombre_usuario()
            + "\nNombre: " + usuario_seleccionado.getNombre_completo()
            + "\n\nEsta acción no se puede deshacer."
    );

    java.util.Optional<javafx.scene.control.ButtonType> respuesta =
            confirmacion.showAndWait();

    if (respuesta.isEmpty()
            || respuesta.get()
            != javafx.scene.control.ButtonType.OK) {
        return;
    }

    try {
        dao.eliminar_usuario(
                usuario_seleccionado.getId_usuario()
        );

        mostrar_mensaje(
                Alert.AlertType.INFORMATION,
                "Usuario eliminado",
                "El usuario se eliminó correctamente."
        );

        cargar_usuarios();
        limpiar_formulario();

    } catch (SQLException | IOException ex) {
        mostrar_mensaje(
                Alert.AlertType.ERROR,
                "No se pudo eliminar el usuario",
                "La operación fue rechazada o ocurrió un error.\n\n"
                + mensaje_error(ex)
                + "\n\nSi el usuario tiene ventas u otros registros "
                + "relacionados, no elimines esos registros para "
                + "forzar la operación."
        );
    }
}
    
    
    private void configurar_tabla() {

        TableColumn<usuario, String> columna_id =
                new TableColumn<>("ID");

        columna_id.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(
                                dato.getValue().getId_usuario()
                        )
                )
        );

        TableColumn<usuario, String> columna_usuario =
                new TableColumn<>("Usuario");

        columna_usuario.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getNombre_usuario()
                )
        );

        TableColumn<usuario, String> columna_nombre =
                new TableColumn<>("Nombre completo");

        columna_nombre.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getNombre_completo()
                )
        );

        TableColumn<usuario, String> columna_rol =
                new TableColumn<>("Rol");

        columna_rol.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getRol()
                )
        );

        tabla.getColumns().setAll(
                columna_id,
                columna_usuario,
                columna_nombre,
                columna_rol
        );

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label("No hay usuarios para mostrar.")
        );


tabla.setPrefHeight(450);

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


for (TableColumn<usuario, ?> columna : tabla.getColumns()) {

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


    }

    private void cargar_usuarios() {

        try {
            List<usuario> usuarios = dao.listar_usuarios();

            ObservableList<usuario> datos =
                    FXCollections.observableArrayList(usuarios);

            tabla.setItems(datos);

        } catch (SQLException | IOException ex) {

            mostrar_mensaje(
                    Alert.AlertType.ERROR,
                    "Error al cargar usuarios",
                    mensaje_error(ex)
            );
        }
    }

    private void limpiar_formulario() {
        usuario_seleccionado = null;
        tabla.getSelectionModel().clearSelection();
        campo_usuario.clear();
        campo_nombre.clear();
        campo_contrasena.clear();
        campo_rol.setValue("vendedor");
    }

    private Button crear_boton(String texto, String color) {

        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setPrefHeight(40);
        boton.setStyle(
                "-fx-background-color: " + color + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 6;"
                + "-fx-cursor: hand;"
        );

        return boton;
    }

    private void estilizar_campo(TextField campo) {
        campo.setPrefHeight(36);
        campo.setStyle(
                "-fx-background-color: #080D18;"
                + "-fx-text-fill: white;"
                + "-fx-prompt-text-fill: #9BAAC4;"
                + "-fx-border-color: #344563;"
                + "-fx-border-radius: 5;"
                + "-fx-background-radius: 5;"
        );
    }

    
private void estilizar_combo(ComboBox<String> campo) {

    campo.setPrefHeight(36);

    campo.setStyle(
            "-fx-background-color: #080D18;"
            + "-fx-border-color: #344563;"
            + "-fx-border-radius: 5;"
            + "-fx-background-radius: 5;"
            + "-fx-text-fill: white;"
    );

    campo.setButtonCell(new javafx.scene.control.ListCell<String>() {
        @Override
        protected void updateItem(String elemento, boolean vacio) {
            super.updateItem(elemento, vacio);

            if (vacio || elemento == null) {
                setText(null);
            } else {
                setText(elemento);
            }

            setTextFill(Color.WHITE);
            setStyle(
                    "-fx-background-color: #080D18;"
                    + "-fx-text-fill: white;"
            );
        }
    });

    campo.setCellFactory(lista -> new javafx.scene.control.ListCell<String>() {
        @Override
        protected void updateItem(String elemento, boolean vacio) {
            super.updateItem(elemento, vacio);

            if (vacio || elemento == null) {
                setText(null);
            } else {
                setText(elemento);
            }

            setTextFill(Color.WHITE);
            setStyle(
                    "-fx-background-color: #101827;"
                    + "-fx-text-fill: white;"
            );
        }
    });
}


    private String mensaje_error(Exception ex) {
        return ex.getMessage() == null
                ? "Ocurrió un error inesperado."
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
