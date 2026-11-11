package com.HelloWay.HelloWay.Security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

/** Jetons aléatoires : QR des tables, mots de passe internes, codes de réinitialisation. */
public final class QrTokens {

    private static final SecureRandom RANDOM = new SecureRandom();

    private QrTokens() {
    }

    /** 32 caractères hexadécimaux (sans tiret, compatible avec le format idTable-jeton-idZone). */
    public static String newToken() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /** Code à 6 chiffres envoyé par e-mail. */
    public static String newSixDigitCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    /** Comparaison à temps constant. */
    public static boolean same(String a, String b) {
        if (a == null || b == null) return false;
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }

    /** Contenu d'un QR de table : idTable-jeton-idZone. */
    public static final class Parsed {
        public final long tableId;
        public final String token;
        public final long zoneId;

        private Parsed(long tableId, String token, long zoneId) {
            this.tableId = tableId;
            this.token = token;
            this.zoneId = zoneId;
        }
    }

    /** null si le format est invalide. */
    public static Parsed parse(String qr) {
        if (qr == null) return null;
        String[] parts = qr.split("-");
        if (parts.length != 3) return null;
        try {
            return new Parsed(Long.parseLong(parts[0]), parts[1], Long.parseLong(parts[2]));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
