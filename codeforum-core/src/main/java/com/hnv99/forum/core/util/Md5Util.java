package com.hnv99.forum.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for calculating MD5 hash values
 */
public class Md5Util {
    private Md5Util() {
    }

    /**
     * Calculate the MD5 hash value of a string
     *
     * @param data Input string
     * @return MD5 hash value
     */
    public static String encode(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return encode(bytes);
    }

    /**
     * Calculate the MD5 hash value of a byte array
     *
     * @param bytes Input byte array
     * @return MD5 hash value
     */
    public static String encode(byte[] bytes) {
        return encode(bytes, 0, bytes.length);
    }

    /**
     * Calculate the MD5 hash value of a byte array
     *
     * @param data   Input byte array
     * @param offset Offset within the byte array to start calculation
     * @param len    Length of the byte array to be used for calculation
     * @return MD5 hash value
     */
    public static String encode(byte[] data, int offset, int len) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var5) {
            throw new RuntimeException(var5);
        }

        md.update(data, offset, len);
        byte[] secretBytes = md.digest();
        return getFormattedText(secretBytes);
    }

    /**
     * Convert the byte array to a hexadecimal string
     *
     * @param src Byte array
     * @return Hexadecimal string
     */
    private static String getFormattedText(byte[] src) {
        if (src != null && src.length != 0) {
            StringBuilder stringBuilder = new StringBuilder(32);

            for (byte b : src) {
                int v = b & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return "";
        }
    }
}
