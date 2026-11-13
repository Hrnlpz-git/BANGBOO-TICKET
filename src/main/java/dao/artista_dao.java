
package dao;

import conexion.conexionbd;
import modelo.artista;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class artista_dao {

    public void registrar_artista(artista nuevo)
            throws SQLException, IOException {

        if (nuevo == null
                || nuevo.getNombre_artistico() == null
                || nuevo.getNombre_artistico().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El nombre artistico es obligatorio.");
        }

        String sql = "INSERT INTO artistas "
                + "(nombre_artistico, genero_musical, "
                + "pais_origen, ruta_imagen, ruta_video) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(
                    1, nuevo.getNombre_artistico().trim());

            consulta.setString(
                    2, nuevo.getGenero_musical());

            consulta.setString(
                    3, nuevo.getPais_origen());

            consulta.setString(
                    4, nuevo.getRuta_imagen());

            consulta.setString(
                    5, nuevo.getRuta_video());

            consulta.executeUpdate();
        }
    }

    public List<artista> listar_artistas()
            throws SQLException, IOException {

        List<artista> lista = new ArrayList<>();

        String sql = "SELECT id_artista, nombre_artistico, "
                + "genero_musical, pais_origen, ruta_imagen, "
                + "ruta_video "
                + "FROM artistas ORDER BY id_artista";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                artista actual = new artista();

                actual.setId_artista(
                        resultado.getInt("id_artista"));

                actual.setNombre_artistico(
                        resultado.getString("nombre_artistico"));

                actual.setGenero_musical(
                        resultado.getString("genero_musical"));

                actual.setPais_origen(
                        resultado.getString("pais_origen"));

                actual.setRuta_imagen(
                        resultado.getString("ruta_imagen"));

                actual.setRuta_video(
                        resultado.getString("ruta_video"));

                lista.add(actual);
            }
        }

        return lista;
    }
    

    public void actualizar_artista(artista actualizado)
            throws SQLException, IOException {

        if (actualizado == null
                || actualizado.getId_artista() <= 0
                || actualizado.getNombre_artistico() == null
                || actualizado.getNombre_artistico().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Selecciona un artista y escribe su nombre artístico.");
        }

        String sql = "UPDATE artistas SET "
                + "nombre_artistico = ?, "
                + "genero_musical = ?, "
                + "pais_origen = ?, "
                + "ruta_imagen = ?, "
                + "ruta_video = ? "
                + "WHERE id_artista = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setString(
                    1, actualizado.getNombre_artistico().trim());

            consulta.setString(
                    2, actualizado.getGenero_musical());

            consulta.setString(
                    3, actualizado.getPais_origen());

            consulta.setString(
                    4, actualizado.getRuta_imagen());

            consulta.setString(
                    5, actualizado.getRuta_video());

            consulta.setInt(
                    6, actualizado.getId_artista());

            int filas = consulta.executeUpdate();

            if (filas == 0) {
                throw new SQLException(
                        "No se encontró el artista que deseas actualizar.");
            }
        }
    }
   
public void eliminar_artista(int id_artista)
        throws SQLException, IOException {

    if (id_artista <= 0) {
        throw new IllegalArgumentException(
                "Selecciona un artista válido.");
    }

    // Comprobar si el artista tiene conciertos asociados.
    String sql_comprobar =
            "SELECT COUNT(*) FROM conciertos WHERE id_artista = ?";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql_comprobar)) {

        consulta.setInt(1, id_artista);

        try (ResultSet resultado = consulta.executeQuery()) {
            if (resultado.next() && resultado.getInt(1) > 0) {
                throw new SQLException(
                        "No se puede eliminar este artista porque "
                        + "tiene conciertos registrados. "
                        + "Primero debes gestionar esos conciertos.");
            }
        }
    }

    String sql_eliminar =
            "DELETE FROM artistas WHERE id_artista = ?";

    try (Connection conexion = conexionbd.conectar();
         PreparedStatement consulta =
                 conexion.prepareStatement(sql_eliminar)) {

        consulta.setInt(1, id_artista);

        int filas = consulta.executeUpdate();

        if (filas == 0) {
            throw new SQLException(
                    "No se encontró el artista que deseas eliminar.");
        }
    }
}
    
}
