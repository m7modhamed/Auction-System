package com.auction.utility;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Sampson Alfred
 */

public class UrlUtil {

    public static String getClientUrl(HttpServletRequest request) {
        return request.getHeader("Origin");
    }

}
