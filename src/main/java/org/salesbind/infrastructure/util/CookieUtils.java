package org.salesbind.infrastructure.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * A utility class for handling HTTP cookies.
 */
public final class CookieUtils {

    private CookieUtils() {
        //
    }

    public static void addCookie(HttpServletResponse response, String name, String value, long maxAge) {
        addCookie(response, name, value, maxAge, "/");
    }

    public static void addCookie(HttpServletResponse response, String name, String value, long maxAge, String path) {
        var cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) maxAge);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        removeCookie(request, response, name, "/");
    }

    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name,
            String path) {

        getCookie(request, name).ifPresent(cookie -> {
            cookie.setValue("");
            cookie.setPath(path);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        });
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }
}
