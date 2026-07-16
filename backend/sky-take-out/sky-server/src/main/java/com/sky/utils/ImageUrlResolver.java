package com.sky.utils;

import javax.servlet.http.HttpServletRequest;

public final class ImageUrlResolver {

    private ImageUrlResolver() {
    }

    public static String resolve(HttpServletRequest request, String image) {
        if (image == null || image.isEmpty() || image.startsWith("http://") || image.startsWith("https://")) {
            return image;
        }
        if (image.contains("download?name=")) {
            image = image.substring(image.lastIndexOf('=') + 1);
        } else if (image.contains("/")) {
            image = image.substring(image.lastIndexOf('/') + 1);
        }
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return base + "/common/download?name=" + image;
    }
}
