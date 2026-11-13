
package servicio;

import dao.usuario_dao;
import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

public class crear_administrador {

    public static void main(String[] args) {

        Console consola = System.console();
        Scanner entrada = null;
        char[] contrasena = null;
        char[] confirmacion = null;

        try {
            String nombre_usuario = "Hernan";
            String nombre_completo = "Hernan";

            if (consola != null) {
                contrasena = consola.readPassword(
                        "Nueva contrasena para Hernan: ");

                confirmacion = consola.readPassword(
                        "Confirma la contrasena: ");
            } else {
                entrada = new Scanner(System.in);

                System.out.print(
                        "Nueva contrasena para Hernan: ");
                contrasena = entrada.nextLine().toCharArray();

                System.out.print(
                        "Confirma la contrasena: ");
                confirmacion = entrada.nextLine().toCharArray();

                System.out.println(
                        "Aviso: la consola puede mostrar lo que escribes.");
            }

            if (contrasena.length < 6) {
                System.out.println(
                        "La contrasena debe tener al menos 6 caracteres.");
                return;
            }

            if (!Arrays.equals(contrasena, confirmacion)) {
                System.out.println(
                        "Las contrasenas no coinciden.");
                return;
            }

            usuario_dao dao = new usuario_dao();

            dao.registrar_usuario(
                    nombre_usuario,
                    contrasena,
                    nombre_completo,
                    "administrador");

            System.out.println(
                    "Administrador creado correctamente.");
            System.out.println(
                    "Nombre de usuario: Hernan");
            System.out.println(
                    "Nombre completo: Hernan");
            System.out.println(
                    "Rol: administrador");

        } catch (Exception e) {
            System.out.println(
                    "No se pudo crear el administrador.");
            System.out.println(
                    "Detalle: " + e.getMessage());

        } finally {
            if (contrasena != null) {
                Arrays.fill(contrasena, '\0');
            }

            if (confirmacion != null) {
                Arrays.fill(confirmacion, '\0');
            }
        }
    }
}
