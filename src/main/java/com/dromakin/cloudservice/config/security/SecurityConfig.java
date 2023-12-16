package com.dromakin.cloudservice.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${server.cors.originFromHeader.label}")
    private String corsOriginFromHeaderLabel;

    @Value("#{'${server.cors.allowedOrigins}'.split(';')}")
    private List<String> corsAllowedOrigins;

    @Value("#{'${server.cors.allowedMethods}'.split(';')}")
    private List<String> corsAllowedMethods;

    private final JWTAuthServer jwtAuthServer;

    private final UserDetailsService userDetailsService;


    @Autowired
    public SecurityConfig(JWTAuthServer jwtAuthServer, @Qualifier("JWTUserDetailsService") UserDetailsService userDetailsService) {
        this.jwtAuthServer = jwtAuthServer;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/actuator/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/cloud/login", "/cloud/logout").permitAll()
                .antMatchers("/cloud/clear").hasRole("ADMIN")
                .antMatchers("/cloud/list").hasAnyRole("USER", "ADMIN", "WRITER")
                .antMatchers("/cloud/file").hasAnyRole("USER", "ADMIN", "WRITER")
                .anyRequest().authenticated()
                .and().httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().apply(new JWTConfigurer(jwtAuthServer))
                .and().exceptionHandling().authenticationEntryPoint(SecurityConfig::commence)
                .and().headers().frameOptions().disable()
                .and().cors().configurationSource(this::getCorsConfiguration)
                .and().csrf().disable()
                .headers().frameOptions().disable();
    }

    private CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration cc = new CorsConfiguration().applyPermitDefaultValues();
        cc.setAllowCredentials(true);
        for (String corsAllowedMethod : corsAllowedMethods) {
            cc.addAllowedMethod(HttpMethod.valueOf(corsAllowedMethod));
        }
        List<String> list = new ArrayList<>();
        for (String s : corsAllowedOrigins) {

            String originH = request.getHeader("Origin");

            if (originH == null) {
                continue;
            }

            String origin = s.replace(corsOriginFromHeaderLabel, request.getHeader("Origin"));

            list.add(origin);
        }
        cc.setAllowedOrigins(list);
        return cc;
    }

    private static void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException {
        resp.setStatus(HttpStatus.UNAUTHORIZED.value());
        resp.setContentType("application/json");
        resp.getWriter().write("{\"message\":\"Unauthorized request\",\"id\":\"401\"}");
    }
}
