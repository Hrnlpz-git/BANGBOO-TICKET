
package utilidades;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class ventana_utilidad {

    private static final String CLAVE_ESTADO =
            "bangboo_pantalla_completa";

    public static void configurar(Stage ventana) {

        ventana.setResizable(true);

        ventana.setFullScreenExitHint(
                ""
        );

        // Recordar el estado de pantalla completa.
        if (!ventana.getProperties().containsKey(CLAVE_ESTADO)) {
            ventana.getProperties().put(CLAVE_ESTADO, true);
        }

        // Configurar las teclas en la escena actual.
        Scene escena = ventana.getScene();

        if (escena != null) {
            escena.setOnKeyPressed(evento -> {

                if (evento.getCode() == KeyCode.F11) {
                    boolean nuevo_estado = !ventana.isFullScreen();

                    ventana.getProperties().put(
                            CLAVE_ESTADO, nuevo_estado
                    );

                    ventana.setFullScreen(nuevo_estado);
                    evento.consume();

                } else if (evento.getCode() == KeyCode.ESCAPE
                        && ventana.isFullScreen()) {

                    ventana.getProperties().put(
                            CLAVE_ESTADO, false
                    );

                    ventana.setFullScreen(false);
                    evento.consume();
                }
            });
        }

        // Aplicar el estado al cambiar de pantalla.
        Platform.runLater(() -> {
            boolean pantalla_completa = (Boolean)
                    ventana.getProperties().get(CLAVE_ESTADO);

            ventana.setFullScreen(pantalla_completa);
        });
    }
}
