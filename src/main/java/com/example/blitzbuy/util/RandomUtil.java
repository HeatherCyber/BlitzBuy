package com.example.blitzbuy.util;

import java.security.SecureRandom;

/**
 * @author Heather
 * @version 1.0
 * RandomUtil: Random string generation utility class
 * Uses SecureRandom to generate secure random strings for generating random salts, verification codes, etc.
 */
public class RandomUtil {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMERIC = "0123456789";
    private static final String ALPHABETIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String ASCII = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generate random string of specified length (containing numbers and letters)
     * @param length String length
     * @return Random string
     */
    public static String randomString(int length) {
        return generateRandomString(ALPHANUMERIC, length);
    }

    /**
     * Generate random numeric string of specified length
     * @param length String length
     * @return Random numeric string
     */
    public static String randomNumeric(int length) {
        return generateRandomString(NUMERIC, length);
    }

    /**
     * Generate random alphabetic string of specified length
     * @param length String length
     * @return Random alphabetic string
     */
    public static String randomAlphabetic(int length) {
        return generateRandomString(ALPHABETIC, length);
    }

    /**
     * Generate random string of specified length (containing numbers, letters and special characters)
     * @param length String length
     * @return Random string
     */
    public static String randomAscii(int length) {
        return generateRandomString(ASCII, length);
    }

    /**
     * Core method for generating random strings
     * @param charset Character set
     * @param length String length
     * @return Random string
     */
    private static String generateRandomString(String charset, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(charset.length());
            sb.append(charset.charAt(randomIndex));
        }
        return sb.toString();
    }
} 