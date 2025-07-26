package com.example.blitzbuy.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Heather
 * @version 1.0
 */
public class MD5Util {

    //prepare a SALT string randomly
    private static final String SALT = "4tIY5VcX";

    //define md5() method
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    //transfer origin password to medium password
    public static String inputPassToMidPass(String inputPass) {
        String str = SALT.charAt(0) + inputPass + SALT.charAt(6);
        return md5(str);
    }

    //transfer medium password to database-kept password
    /**
    @param salt is from database `blitzbuy`--table `user`-- field `salt`
     */
    public static String midPassToDBPass(String midPass, String salt) {
        String str = salt.charAt(2) + midPass + salt.charAt(4);
        return md5(str);
    }

    //transfer origin password to database-kept password
    public static String inputPassToDBPass(String inputPass, String salt) {
        String midPass = inputPassToMidPass(inputPass);
        return midPassToDBPass(midPass, salt);
    }


}
