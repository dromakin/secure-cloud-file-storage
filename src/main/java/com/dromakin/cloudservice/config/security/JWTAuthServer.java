package com.dromakin.cloudservice.config.security;

import com.dromakin.cloudservice.exceptions.JwtAuthenticationException;
import com.dromakin.cloudservice.models.security.JwtUser;
import com.dromakin.cloudservice.models.security.Role;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthServer {

    @Value("${jwt.token.secret}")
    private String tokenSecret;

    @Value("${jwt.token.expired}")
    private Long tokenExpiredInMilliseconds;

    @Value("${jwt.token.httpHeaderName}")
    private String httpHeaderName;

    private final UserDetailsService userDetailsService;

    public JWTAuthServer(@Qualifier("JWTUserDetailsService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        tokenSecret = Base64.getEncoder().encodeToString(tokenSecret.getBytes());
    }

    public Authentication getAuthentication(String token) {
        JwtUser userDetails = (JwtUser) this.userDetailsService.loadUserByUsername(getLoginByToken(token));
        Jws<Claims> claims = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
        return claims.getBody().get("enterTime").equals(userDetails.getLastEnter()) ? new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities()) : null;
    }

    public String getLoginByToken(String token) {
        return Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token).getBody().getSubject();
    }


    private List<String> getRoleNames(List<Role> userRoles) {
        return userRoles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    public String createToken(String username, List<Role> roles, Long lastEnter) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", getRoleNames(roles));
        claims.put("enterTime", lastEnter);

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenExpiredInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, tokenSecret)
                .compact();
    }


    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(httpHeaderName);

        if (bearerToken != null) {
            return bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : null;
        }

        return null;
    }

    public boolean validateToken(String token) {
        try {

            Jws<Claims> claims = Jwts.parser().setSigningKey(tokenSecret).parseClaimsJws(token);
            return (int) claims.getBody().get("iat") < (int) claims.getBody().get("exp");

        } catch (IllegalArgumentException | JwtException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid!");
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
