package com.example.blitzbuy.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Heather
 * @version 1.0
 */
public class CookieUtil {

    /**
     * Get Cookie value, no encoding
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String
            cookieName) {
        return getCookieValue(request, cookieName, false);
    }

    /**
     * Get Cookie value
     *
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String
            cookieName, boolean isDecoder) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (cookieList[i].getName().equals(cookieName)) {
                    if (isDecoder) {
                        retValue = URLDecoder.decode(cookieList[i].getValue(),
                                "UTF-8");
                    } else {
                        retValue = cookieList[i].getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * Get Cookie value
     *
     * @param request
     * @param cookieName
     * @param encodeString
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String
            cookieName, String encodeString) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookieList.length; i++) {
                if (cookieList[i].getName().equals(cookieName)) {
                    retValue = URLDecoder.decode(cookieList[i].getValue(),
                            encodeString);
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    /**
     * Set Cookie value, no expiration time set, expires when browser closes, no encoding
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
            response, String cookieName, String cookieValue) {
        setCookie(request, response, cookieName, cookieValue, -1);
    }

    /**
     * Set Cookie value, effective for specified time, but no encoding
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
            response, String cookieName, String cookieValue, int cookieMaxage) {
        setCookie(request, response, cookieName, cookieValue, cookieMaxage,
                false);
    }


    /**
     * Set Cookie value, no expiration time, but encoding
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
                                         response, String cookieName,
                                 String cookieValue, boolean isEncode) {
        setCookie(request, response, cookieName, cookieValue, -1, isEncode);
    }

    /**
     * Set Cookie value, effective for specified time,  encoding parameters
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
                                         response, String cookieName,
                                 String cookieValue, int cookieMaxage, boolean
                                         isEncode) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage,
                isEncode);
    }

    /**
     * Set Cookie value, effective for specified time,  encoding parameters(specified encoding)
     */
    public static void setCookie(HttpServletRequest request, HttpServletResponse
                                         response, String cookieName,
                                 String cookieValue, int cookieMaxage, String
                                         encodeString) {
        doSetCookie(request, response, cookieName, cookieValue, cookieMaxage,
                encodeString);
    }

    /**
     * Delete Cookie with cookie domain name
     */
    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String cookieName) {
        doSetCookie(request, response, cookieName, "", -1, false);
    }

    /**
     * Set Cookie value, and make it effective for specified time
     *
     * @param cookieMaxage cookie effective maximum seconds
     */
    private static final void doSetCookie(HttpServletRequest request,
                                          HttpServletResponse response,
                                          String cookieName, String cookieValue,
                                          int cookieMaxage, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0) {
                cookie.setMaxAge(cookieMaxage);
            }
//            if (null != request) {// Set domain cookie
//                String domainName = getDomainName(request);
//                if (!"localhost".equals(domainName)) {
//                    cookie.setDomain(domainName);
//                }
//            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set Cookie value, and make it effective for specified time
     *
     * @param cookieMaxage cookie effective maximum seconds
     */
    private static final void doSetCookie(HttpServletRequest request,
                                          HttpServletResponse response,
                                          String cookieName, String cookieValue,
                                          int cookieMaxage, String encodeString) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else {
                cookieValue = URLEncoder.encode(cookieValue, encodeString);
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage > 0) {
                cookie.setMaxAge(cookieMaxage);
            }
            if (null != request) {// Set domain cookie
                String domainName = getDomainName(request);
                System.out.println(domainName);
                if (!"localhost".equals(domainName)) {
                    cookie.setDomain(domainName);
                }
            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get cookie domain name
     */
    private static final String getDomainName(HttpServletRequest request) {
        String domainName = null;
        // Get accessed URL address through request object
        String serverName = request.getRequestURL().toString();
        if ("".equals(serverName)) {
            domainName = "";
        } else {
            // Convert URL to lowercase
            serverName = serverName.toLowerCase();
            // If URL starts with http://, remove http://
            if (serverName.startsWith("http://")) {
                serverName = serverName.substring(7);
            }
            int end = serverName.length();
            // Check if URL contains "/"
            if (serverName.contains("/")) {
                // Get position of first "/"
                end = serverName.indexOf("/");
            }
            // Substring
            serverName = serverName.substring(0, end);
            // Split by "."
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                // www.abc.com.cn
                domainName = domains[len - 3] + "." + domains[len - 2] + "." +
                        domains[len - 1];
            } else if (len > 1) {
                // abc.com or abc.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }
        if (domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        return domainName;
    }
}