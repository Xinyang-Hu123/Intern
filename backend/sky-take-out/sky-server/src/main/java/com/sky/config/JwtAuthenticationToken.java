package com.sky.config;

import org.apache.shiro.authc.AuthenticationToken;

public class JwtAuthenticationToken implements AuthenticationToken {

    public static final String ADMIN = "admin";
    public static final String USER = "user";

    private final String token;
    private final String clientType;

    public JwtAuthenticationToken(String token, String clientType) {
        this.token = token;
        this.clientType = clientType;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public String getToken() {
        return token;
    }

    public String getClientType() {
        return clientType;
    }
}
