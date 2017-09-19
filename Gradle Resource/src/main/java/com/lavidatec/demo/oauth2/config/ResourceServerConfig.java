package com.lavidatec.demo.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

/**
 * Created by Arip Hidayat on 12/03/2016.
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    public static final String RESOURCE_ID = "arip";

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/api/admin").hasRole("ADMIN");
        http.authorizeRequests().antMatchers("/api/staff").hasRole("STAFF");
        http.authorizeRequests().antMatchers("/api/client").access("#oauth2.hasScope('trust')");
        http.authorizeRequests().antMatchers("/Train/search").hasRole("ADMIN");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setClientId("jsclient");
        tokenService.setClientSecret("123456");
        tokenService.setCheckTokenEndpointUrl("http://localhost:8081/authorization-server/oauth/check_token");

        resources.resourceId(RESOURCE_ID).tokenServices(tokenService);
    }
}