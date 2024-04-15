package com.hnv99.forum.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * SessionUtil class for managing cookies.
 */
public class SessionUtil {
    private static final int COOKIE_AGE = 30 * 86400;

    /**
     * Create a new cookie.
     *
     * @param key     The cookie key.
     * @param session The session value.
     * @return The created cookie.
     */
    public static Cookie newCookie(String key, String session) {
        return newCookie(key, session, "/", COOKIE_AGE);
    }

    /**
     * Create a new cookie with custom parameters.
     *
     * @param key     The cookie key.
     * @param session The session value.
     * @param path    The cookie path.
     * @param maxAge  The cookie max age.
     * @return The created cookie.
     */
    public static Cookie newCookie(String key, String session, String path, int maxAge) {
        Cookie cookie = new Cookie(key, session);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    /**
     * Delete a cookie.
     *
     * @param key The cookie key.
     * @return The deleted cookie.
     */
    public static Cookie delCookie(String key) {
        return delCookie(key, "/");
    }

    /**
     * Delete a cookie with custom parameters.
     *
     * @param key  The cookie key.
     * @param path The cookie path.
     * @return The deleted cookie.
     */
    public static Cookie delCookie(String key, String path) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    /**
     * Find a cookie by its name in the request.
     *
     * @param request The HTTP servlet request.
     * @param name    The name of the cookie.
     * @return The found cookie.
     */
    public static Cookie findCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        return Arrays.stream(cookies).filter(cookie -> StringUtils.equalsAnyIgnoreCase(cookie.getName(), name))
                .findFirst().orElse(null);
    }

    /**
     * Find a cookie by its name in the server request.
     *
     * @param request The server HTTP request.
     * @param name    The name of the cookie.
     * @return The found cookie value.
     */
    public static String findCookieByName(ServerHttpRequest request, String name) {
        List<String> list = request.getHeaders().get("cookie");
        if (list == null || list.isEmpty()) {
            return null;
        }

        for (String sub : list) {
            String[] elements = StringUtils.split(sub, ";");
            for (String element : elements) {
                String[] subs = StringUtils.split(element, "=");
                if (subs.length == 2 && StringUtils.equalsAnyIgnoreCase(subs[0].trim(), name)) {
                    return subs[1].trim();
                }
            }
        }
        return null;
    }
}

