package com.dromakin.cloudservice.config;

import com.dromakin.cloudservice.config.security.JWTAuthServer;
import com.dromakin.cloudservice.config.security.JWTFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private JWTAuthServer jwtAuthServer;

    public JWTConfigurer(JWTAuthServer jwtAuthServer) {
        this.jwtAuthServer = jwtAuthServer;
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        JWTFilter jwtFilter = new JWTFilter(jwtAuthServer);
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
