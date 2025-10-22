package com.example.blitzbuy.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Heather
 * @version 2.0
 * 
 * Utility class for validating phone numbers in multiple formats.
 * Supports both Chinese and US phone number formats.
 */
public class ValidatorUtil {

    //Pre-compile the pattern for chinese mobile format
    private static final Pattern CN_PHONE_PATTERN =
            Pattern.compile("^(?:(?:\\+|00)86)?1[3-9]\\d{9}$");

    //Pre-compile the pattern for us mobile format (11 digits starting with 1)
    private static final Pattern US_PHONE_PATTERN = Pattern.compile(
            "^1[2-9]\\d{2}[2-9]\\d{2}\\d{4}$"
    );

    /**
     * Validates phone numbers for both Chinese and US formats
     * @param mobile The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isMobile(String mobile){
        if(!StringUtils.hasText(mobile)){
            return false;
        }
        // First try US phone number format
        Matcher usMatcher = US_PHONE_PATTERN.matcher(mobile);
        if(usMatcher.matches()){
            return true;
        }
        // Then try Chinese phone number format
        Matcher cnMatcher = CN_PHONE_PATTERN.matcher(mobile);
        return cnMatcher.matches();
    }

    /**
     * Determines if a phone number is in US format
     * @param mobile The phone number to check
     * @return true if US format, false otherwise
     */
    public static boolean isUSMobile(String mobile){
        if(!StringUtils.hasText(mobile)){
            return false;
        }
        Matcher usMatcher = US_PHONE_PATTERN.matcher(mobile);
        return usMatcher.matches();
    }

    /**
     * Determines if a phone number is in Chinese format
     * @param mobile The phone number to check
     * @return true if Chinese format, false otherwise
     */
    public static boolean isChineseMobile(String mobile) {
        if(!StringUtils.hasText(mobile)){
            return false;
        }
        Matcher cnMatcher = CN_PHONE_PATTERN.matcher(mobile);
        return cnMatcher.matches();
    }

}
