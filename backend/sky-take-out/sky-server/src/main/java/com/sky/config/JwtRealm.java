package com.sky.config;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class JwtRealm extends AuthorizingRealm {

    private final JwtProperties jwtProperties;

    public JwtRealm(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtAuthenticationToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authenticationToken;
        String token = jwtAuthenticationToken.getToken();

        try {
            Long currentId;
            if (JwtAuthenticationToken.ADMIN.equals(jwtAuthenticationToken.getClientType())) {
                Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
                currentId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            } else {
                Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
                currentId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            }
            BaseContext.setCurrentId(currentId);
            return new SimpleAuthenticationInfo(currentId, token, getName());
        } catch (Exception ex) {
            throw new AuthenticationException("token校验失败", ex);
        }
    }
}
