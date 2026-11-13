
package dao;

import conexion.conexionbd;
import modelo.concierto;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class concierto_dao {

    public void registrar_concierto(concierto nuevo)
            throws SQLException, IOException {

        if (nuevo == null
                || nuevo.getTitulo_evento() == null
                || nuevo.getTitulo_evento().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El titulo del evento es obligatorio.");
        }

        if (nuevo.getFecha_concierto() == null) {
            throw new IllegalArgumentException(
                    "La fecha del concierto es obligatoria.");
        }

        if (nuevo.getHora_concierto() == null) {
            throw new IllegalArgumentException(
                    "La hora del concierto es obligatoria.");
        }

        if (nuevo.getRecinto() == null
                || nuevo.getRecinto().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El recinto es obligatorio.");
        }

        String sql = "INSERT INTO conciertos "
                + "(id_artista, titulo_evento, fecha_concierto, "
                + "hora_concierto, recinto, estado, ruta_cartel, ruta_video) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, nuevo.getId_artista());

            consulta.setString(
                    2, nuevo.getTitulo_evento().trim());

            consulta.setDate(
                    3, nuevo.getFecha_concierto());

            consulta.setTime(
                    4, nuevo.getHora_concierto());

            consulta.setString(
                    5, nuevo.getRecinto().trim());

            consulta.setString(
                    6, nuevo.getEstado() == null
                            ? "programado" : nuevo.getEstado());

            consulta.setString(7, nuevo.getRuta_cartel());
            consulta.setString(8, nuevo.getRuta_video());

            consulta.executeUpdate();
        }
    }

    public void actualizar_concierto(concierto actual)
            throws SQLException, IOException {

        if (actual == null || actual.getId_concierto() <= 0) {
            throw new IllegalArgumentException(
                    "Selecciona un concierto válido.");
        }

        if (actual.getTitulo_evento() == null
                || actual.getTitulo_evento().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El título del evento es obligatorio.");
        }

        if (actual.getFecha_concierto() == null) {
            throw new IllegalArgumentException(
                    "La fecha del concierto es obligatoria.");
        }

        if (actual.getHora_concierto() == null) {
            throw new IllegalArgumentException(
                    "La hora del concierto es obligatoria.");
        }

        if (actual.getRecinto() == null
                || actual.getRecinto().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El recinto es obligatorio.");
        }

        String sql = "UPDATE conciertos SET "
                + "id_artista = ?, "
                + "titulo_evento = ?, "
                + "fecha_concierto = ?, "
                + "hora_concierto = ?, "
                + "recinto = ?, "
                + "estado = ?, "
                + "ruta_cartel = ?, "
                + "ruta_video = ? "
                + "WHERE id_concierto = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, actual.getId_artista());

            consulta.setString(
                    2, actual.getTitulo_evento().trim());

            consulta.setDate(
                    3, actual.getFecha_concierto());

            consulta.setTime(
                    4, actual.getHora_concierto());

            consulta.setString(
                    5, actual.getRecinto().trim());

            consulta.setString(
                    6,
                    actual.getEstado() == null
                            ? "programado" : actual.getEstado());

            consulta.setString(7, actual.getRuta_cartel());
            consulta.setString(8, actual.getRuta_video());
            consulta.setInt(9, actual.getId_concierto());

            int filas_actualizadas = consulta.executeUpdate();

            if (filas_actualizadas == 0) {
                throw new SQLException(
                        "No se encontró el concierto que deseas actualizar.");
            }
        }
    }

    public List<concierto> listar_conciertos()
            throws SQLException, IOException {

        List<concierto> lista = new ArrayList<>();

        String sql = "SELECT id_concierto, id_artista, "
                + "titulo_evento, fecha_concierto, hora_concierto, "
                + "recinto, estado, ruta_cartel, ruta_video "
                + "FROM conciertos ORDER BY fecha_concierto";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {

                concierto actual = new concierto();

                actual.setId_concierto(
                        resultado.getInt("id_concierto"));

                actual.setId_artista(
                        resultado.getInt("id_artista"));

                actual.setTitulo_evento(
                        resultado.getString("titulo_evento"));

                actual.setFecha_concierto(
                        resultado.getDate("fecha_concierto"));

                actual.setHora_concierto(
                        resultado.getTime("hora_concierto"));

                actual.setRecinto(
                        resultado.getString("recinto"));

                actual.setEstado(
                        resultado.getString("estado"));

                actual.setRuta_cartel(
                        resultado.getString("ruta_cartel"));

                actual.setRuta_video(
                        resultado.getString("ruta_video"));

                lista.add(actual);
            }
        }

        return lista;
    }

    public void eliminar_concierto(int id_concierto)
            throws SQLException, IOException {

        if (id_concierto <= 0) {
            throw new IllegalArgumentException(
                    "Selecciona un concierto válido.");
        }

        String sql =
                "DELETE FROM conciertos WHERE id_concierto = ?";

        try (Connection conexion = conexionbd.conectar();
             PreparedStatement consulta =
                     conexion.prepareStatement(sql)) {

            consulta.setInt(1, id_concierto);

            int filas = consulta.executeUpdate();

            if (filas == 0) {
                throw new SQLException(
                        "No se encontró el concierto que deseas eliminar.");
            }
        }
    }
}
