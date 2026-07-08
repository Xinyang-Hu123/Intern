package com.sky.config;

import com.sky.properties.JwtProperties;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtFilter extends AccessControlFilter {

    private final JwtProperties jwtProperties;

    public JwtFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            return true;
        }

        String uri = httpRequest.getRequestURI();
        String clientType = uri.startsWith("/user/") ? JwtAuthenticationToken.USER : JwtAuthenticationToken.ADMIN;
        String headerName = JwtAuthenticationToken.USER.equals(clientType)
                ? jwtProperties.getUserTokenName()
                : jwtProperties.getAdminTokenName();
        String token = httpRequest.getHeader(headerName);

        if (!StringUtils.hasText(token)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            SecurityUtils.getSubject().login(new JwtAuthenticationToken(token, clientType));
            return true;
        } catch (AuthenticationException ex) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
