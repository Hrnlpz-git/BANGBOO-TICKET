
package seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class gestor_contrasenas {

    private static final int ITERACIONES = 600000;
    private static final int LONGITUD_HASH = 256;
    private static final int LONGITUD_SAL = 16;

    private static final SecureRandom ALEATORIO =
            new SecureRandom();

    public static String generar_hash(char[] contrasena)
            throws GeneralSecurityException {

        byte[] sal = new byte[LONGITUD_SAL];
        ALEATORIO.nextBytes(sal);

        byte[] hash = calcular_hash(
                contrasena, sal, ITERACIONES);

        return "pbkdf2_sha256$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(sal)
                + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(
            char[] contrasena, String hash_guardado)
            throws GeneralSecurityException {

        if (hash_guardado == null) {
            return false;
        }

        String[] partes = hash_guardado.split("\\$");

        if (partes.length != 4
                || !partes[0].equals("pbkdf2_sha256")) {
            return false;
        }

        try {
            int iteraciones = Integer.parseInt(partes[1]);

            if (iteraciones < 100000
                    || iteraciones > 1000000) {
                return false;
            }

            byte[] sal = Base64.getDecoder()
                    .decode(partes[2]);

            byte[] hash_original = Base64.getDecoder()
                    .decode(partes[3]);

            if (sal.length != LONGITUD_SAL
                    || hash_original.length
                    != LONGITUD_HASH / 8) {
                return false;
            }

            byte[] hash_nuevo = calcular_hash(
                    contrasena, sal, iteraciones);

            return MessageDigest.isEqual(
                    hash_original, hash_nuevo);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] calcular_hash(
            char[] contrasena, byte[] sal,
            int iteraciones)
            throws GeneralSecurityException {

        PBEKeySpec especificacion = new PBEKeySpec(
                contrasena, sal, iteraciones,
                LONGITUD_HASH);

        try {
            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256");

            return fabrica.generateSecret(especificacion)
                    .getEncoded();

        } finally {
            especificacion.clearPassword();
        }
    }
}
