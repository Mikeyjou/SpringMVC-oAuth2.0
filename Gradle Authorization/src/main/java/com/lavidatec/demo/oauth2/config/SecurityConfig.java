package com.lavidatec.demo.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by Arip Hidayat on 12/10/2015.
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("passw0rd").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("staff").password("passw0rd").roles("STAFF");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(true);

        http.authorizeRequests().antMatchers("/login.jsp").permitAll();
        http.authorizeRequests().and().formLogin().loginPage("/login.jsp").loginProcessingUrl("/j_spring_security_check")
                .usernameParameter("j_username").passwordParameter("j_password");
        http.authorizeRequests().and().logout().logoutUrl("/j_spring_security_logout");
    }
}