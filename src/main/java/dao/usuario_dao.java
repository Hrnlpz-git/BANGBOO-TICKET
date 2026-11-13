
package dao;

import conexion.conexionbd;
import modelo.usuario;
import seguridad.gestor_contrasenas;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


public class usuario_dao {

    public void registrar_usuario(
            String nombre_usuario,
            char[] contrasena,
            String nombre_completo,
            String rol)
            throws SQLException, IOException,
            GeneralSecurityException {

        if (nombre_usuario == null
                || nombre_usuario.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (nombre_completo == null
                || nombre_completo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre completo es obligatorio.");
        }

        if (contrasena == null || contrasena.length < 6) {
            throw new IllegalArgumentException(
                    "La contrasena debe tener al menos 6 caracteres.");
        }

        if (rol == null
                || (!rol.equals("administrador")
                && !rol.equals("vendedor"))) {
            throw new IllegalArgumentException(
                    "El rol debe ser administrador o vendedor.");
        }

        String hash = gestor_contrasenas.generar_hash(
                contrasena);

        String sql = "INSERT INTO usuarios "
                + "(nombre_usuario, contrasena_hash, "
                + "nombre_completo, rol) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, nombre_usuario.trim());
            consulta.setString(2, hash);
            consulta.setString(3, nombre_completo.trim());
            consulta.setString(4, rol);

            consulta.executeUpdate();
        }
    }

    public usuario autenticar(
            String nombre_usuario,
            char[] contrasena)
            throws SQLException, IOException,
            GeneralSecurityException {

        String sql = "SELECT id_usuario, nombre_usuario, "
                + "contrasena_hash, nombre_completo, rol "
                + "FROM usuarios WHERE nombre_usuario = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, nombre_usuario);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {

                    String hash_guardado =
                            resultado.getString("contrasena_hash");

                    boolean contrasena_correcta =
                            gestor_contrasenas.verificar(
                                    contrasena, hash_guardado);

                    if (contrasena_correcta) {

                        usuario encontrado = new usuario();

                        encontrado.setId_usuario(
                                resultado.getInt("id_usuario"));

                        encontrado.setNombre_usuario(
                                resultado.getString("nombre_usuario"));

                        encontrado.setContrasena_hash(
                                hash_guardado);

                        encontrado.setNombre_completo(
                                resultado.getString("nombre_completo"));

                        encontrado.setRol(
                                resultado.getString("rol"));

                        return encontrado;
                    }
                }
            }
        }

        return null;
    }

    public usuario buscar_por_nombre(String nombre_usuario)
            throws SQLException, IOException {

        String sql = "SELECT id_usuario, nombre_usuario, "
                + "contrasena_hash, nombre_completo, rol "
                + "FROM usuarios WHERE nombre_usuario = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(1, nombre_usuario);

            try (ResultSet resultado =
                    consulta.executeQuery()) {

                if (resultado.next()) {

                    usuario encontrado = new usuario();

                    encontrado.setId_usuario(
                            resultado.getInt("id_usuario"));

                    encontrado.setNombre_usuario(
                            resultado.getString("nombre_usuario"));

                    encontrado.setContrasena_hash(
                            resultado.getString("contrasena_hash"));

                    encontrado.setNombre_completo(
                            resultado.getString("nombre_completo"));

                    encontrado.setRol(
                            resultado.getString("rol"));

                    return encontrado;
                }
            }
        }

        return null;
    }
    
    
public List<usuario> listar_usuarios()
        throws SQLException, IOException {

    List<usuario> lista = new ArrayList<>();

    String sql = "SELECT id_usuario, nombre_usuario, "
            + "nombre_completo, rol "
            + "FROM usuarios ORDER BY id_usuario";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql);
         ResultSet resultado = consulta.executeQuery()) {

        while (resultado.next()) {

            usuario encontrado = new usuario();

            encontrado.setId_usuario(
                    resultado.getInt("id_usuario"));

            encontrado.setNombre_usuario(
                    resultado.getString("nombre_usuario"));

            encontrado.setNombre_completo(
                    resultado.getString("nombre_completo"));

            encontrado.setRol(
                    resultado.getString("rol"));

            lista.add(encontrado);
        }
    }

    return lista;
}


public void actualizar_usuario(
        int id_usuario,
        String nombre_usuario,
        String nombre_completo,
        String rol)
        throws SQLException, IOException {

    if (nombre_usuario == null
            || nombre_usuario.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "El nombre de usuario es obligatorio.");
    }

    if (nombre_completo == null
            || nombre_completo.trim().isEmpty()) {
        throw new IllegalArgumentException(
                "El nombre completo es obligatorio.");
    }

    if (rol == null
            || (!rol.equals("administrador")
            && !rol.equals("vendedor"))) {
        throw new IllegalArgumentException(
                "El rol debe ser administrador o vendedor.");
    }
    
    if (id_usuario == 3 && !"administrador".equals(rol)) {
    throw new IllegalArgumentException(
            "La cuenta de Hernan está protegida. "
            + "Debe conservar el rol de administrador.");
}

    String sql = "UPDATE usuarios "
            + "SET nombre_usuario = ?, "
            + "nombre_completo = ?, rol = ? "
            + "WHERE id_usuario = ?";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql)) {

        consulta.setString(1, nombre_usuario.trim());
        consulta.setString(2, nombre_completo.trim());
        consulta.setString(3, rol);
        consulta.setInt(4, id_usuario);

        int filas = consulta.executeUpdate();

        if (filas == 0) {
            throw new SQLException(
                    "No se encontró el usuario seleccionado.");
        }
    }
}

public void cambiar_contrasena(
        int id_usuario,
        char[] nueva_contrasena)
        throws SQLException, IOException,
        GeneralSecurityException {

    if (nueva_contrasena == null
            || nueva_contrasena.length < 6) {
        throw new IllegalArgumentException(
                "La contraseña debe tener al menos 6 caracteres.");
    }

    String hash = gestor_contrasenas.generar_hash(
            nueva_contrasena);

    String sql = "UPDATE usuarios "
            + "SET contrasena_hash = ? "
            + "WHERE id_usuario = ?";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql)) {

        consulta.setString(1, hash);
        consulta.setInt(2, id_usuario);

        int filas = consulta.executeUpdate();

        if (filas == 0) {
            throw new SQLException(
                    "No se encontró el usuario seleccionado.");
        }
    }
}


public void eliminar_usuario(int id_usuario)
        throws SQLException, IOException {

    String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

if (id_usuario == 3) {
    throw new SQLException(
            "La cuenta de Hernan está protegida y no se puede eliminar."
    );
}


    try (Connection conexion = conexionbd.conectar()) {

        conexion.setAutoCommit(false);

        try {
            // Comprobar que no sea el último administrador.
            String consulta_admin =
                    "SELECT rol FROM usuarios "
                    + "WHERE id_usuario = ? FOR UPDATE";

            String rol_usuario = null;

            try (PreparedStatement consulta =
                    conexion.prepareStatement(consulta_admin)) {

                consulta.setInt(1, id_usuario);

                try (ResultSet resultado = consulta.executeQuery()) {
                    if (resultado.next()) {
                        rol_usuario = resultado.getString("rol");
                    } else {
                        throw new SQLException(
                                "No se encontró el usuario seleccionado.");
                    }
                }
            }

            if ("administrador".equals(rol_usuario)) {
                String contar_admin =
                        "SELECT COUNT(*) FROM usuarios "
                        + "WHERE rol = 'administrador'";

                try (PreparedStatement consulta =
                        conexion.prepareStatement(contar_admin);
                     ResultSet resultado = consulta.executeQuery()) {

                    if (resultado.next()
                            && resultado.getInt(1) <= 1) {
                        throw new SQLException(
                                "No se puede eliminar al último administrador.");
                    }
                }
            }

            try (PreparedStatement consulta =
                    conexion.prepareStatement(sql)) {

                consulta.setInt(1, id_usuario);

                if (consulta.executeUpdate() == 0) {
                    throw new SQLException(
                            "No se encontró el usuario seleccionado.");
                }
            }

            conexion.commit();

        } catch (SQLException ex) {
            conexion.rollback();
            throw ex;
        } finally {
            conexion.setAutoCommit(true);
        }
    }
}


}
