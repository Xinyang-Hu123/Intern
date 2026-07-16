package com.sky.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageUrlResolverTest {

    @Test
    void keepsExternalHttpsImageUrl() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/dish/list");
        String image = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format";

        assertEquals(image, ImageUrlResolver.resolve(request, image));
    }

    @Test
    void convertsLegacyImageNameToLocalDownloadUrl() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/dish/list");
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8088);

        assertEquals("http://localhost:8088/common/download?name=1.png",
                ImageUrlResolver.resolve(request, "1.png"));
    }
}
