package com.example.blitzbuy.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Heather
 * @version 1.0
 */
public class ValidatorUtil {

    //Pre-compile the pattern for chinese mobile format
    private static final Pattern CN_PHONE_PATTERN =
            Pattern.compile("^(?:(?:\\+|00)86)?1[3-9]\\d{9}$");

    //Pre-compile the pattern for us mobile format
    private static final Pattern US_PHONE_PATTERN = Pattern.compile(
            "^(\\+1\\s?)?(\\([2-9]\\d{2}\\)\\s?|[2-9]\\d{2}[\\s\\-]?)\\d{3}[\\s\\-]?\\d{4}$"
    );

    /**
     * Validates a chinese phone number
     * @param mobile The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isMobile(String mobile){
        if(!StringUtils.hasText(mobile)){
            return false;
        }
        Matcher matcher = CN_PHONE_PATTERN.matcher(mobile);
        return matcher.matches();

    }

}
