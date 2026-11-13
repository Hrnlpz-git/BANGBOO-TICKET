
package utilidades;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class encabezado_utilidad {

    public static HBox crear(String titulo) {

        Image imagen = new Image(
                encabezado_utilidad.class.getResourceAsStream(
                        "/imagenes/bangboo.png"
                )
        );

        ImageView logo = new ImageView(imagen);
        logo.setFitWidth(55);
        logo.setFitHeight(55);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);

        Label nombre = new Label(titulo);
        nombre.setFont(Font.font("Arial", 21));
        nombre.setTextFill(Color.web("#F4A340"));

        HBox encabezado = new HBox(10, logo, nombre);

        encabezado.setAlignment(Pos.CENTER_LEFT);

        // Sin fondo, borde ni caja adicional.
        encabezado.setStyle("-fx-background-color: transparent;");

        return encabezado;
    }
}
