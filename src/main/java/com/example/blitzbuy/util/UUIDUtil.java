package com.example.blitzbuy.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author Heather
 * @version 1.0
 * Utility class for generating UUID (Universally Unique Identifier) strings.
 * A UUID represents a 128-bit value.
 *
 * This class provides a simple method to generate random UUIDs
 * in a standardized format without hyphens.
 */
public class UUIDUtil {

    //UUID.randomUUID().toString() will return a string with "-"
    // (e.g., "79d170bb-fd22-49c4-86f4-bf8551d9b4ed")
    // need to remove "-"
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
