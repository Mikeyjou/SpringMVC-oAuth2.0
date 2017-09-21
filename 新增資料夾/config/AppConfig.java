package com.lavidatec.demo.oauth2.config;

import com.lavidatec.demo.oauth2.filter.CORSFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Arip Hidayat on 12/8/2015.
 */
@Configuration
//@EnableWebMvc
//@ComponentScan
@PropertySource("classpath:clients.properties")
@ImportResource({"classpath:spring-mvc-config.xml"})
public class AppConfig extends WebMvcConfigurerAdapter {

    @Value("${allowedHosts}")
    private String allowedHosts;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
        configurer.enable();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        System.out.println("Allowed Host : "+allowedHosts);
        registry.addInterceptor(new CORSFilter(allowedHosts));
    }
}
