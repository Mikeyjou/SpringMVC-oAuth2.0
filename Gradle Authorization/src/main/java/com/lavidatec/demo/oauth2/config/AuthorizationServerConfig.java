package com.lavidatec.demo.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * Created by Arip Hidayat on 12/03/2016.
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    public static final String RESOURCE_ID = "arip";

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(new InMemoryTokenStore()).authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.checkTokenAccess("hasRole('CLIENT')");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer client) throws Exception {
        client.inMemory()
            .withClient("clientapp")
                .secret("123456")
                .authorizedGrantTypes("password")
                .scopes("read", "write")
                .resourceIds(RESOURCE_ID)
            .and()
            .withClient("clientcred")
                .secret("123456")
                .authorizedGrantTypes("client_credentials")
                .scopes("trust")
                .resourceIds(RESOURCE_ID)
            .and()
            .withClient("clientauthcode")
                .secret("123456")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .scopes("read", "write")
                .resourceIds(RESOURCE_ID)
            .and()
            .withClient("jsclient")
                .secret("123456")
                .authorizedGrantTypes("implicit")
                .scopes("read", "write")
                .resourceIds(RESOURCE_ID)
                .authorities("CLIENT")
                .redirectUris("http://localhost:8080/resource-server/api/state/verify")
                .accessTokenValiditySeconds(3600)
                .autoApprove(true);
    }
}