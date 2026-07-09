package com.sky.config;

import com.sky.properties.JwtProperties;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public JwtRealm jwtRealm(JwtProperties jwtProperties) {
        return new JwtRealm(jwtProperties);
    }

    @Bean
    public DefaultWebSecurityManager securityManager(JwtRealm jwtRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(jwtRealm);

        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
                                                         JwtProperties jwtProperties) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("jwt", new JwtFilter(jwtProperties));
        factoryBean.setFilters(filters);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/admin/employee/login", "anon");
        filterChainDefinitionMap.put("/user/user/login", "anon");
        filterChainDefinitionMap.put("/user/shop/status", "anon");
        filterChainDefinitionMap.put("/user/shop/info", "anon");
        filterChainDefinitionMap.put("/user/shop/phone", "anon");
        filterChainDefinitionMap.put("/user/category/list", "anon");
        filterChainDefinitionMap.put("/user/dish/list", "anon");
        filterChainDefinitionMap.put("/user/dish/recommend", "anon");
        filterChainDefinitionMap.put("/user/dish/column", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/admin/**", "jwt");
        filterChainDefinitionMap.put("/user/**", "jwt");
        filterChainDefinitionMap.put("/**", "anon");
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return factoryBean;
    }
}

