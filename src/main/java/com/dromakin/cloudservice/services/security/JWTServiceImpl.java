package com.dromakin.cloudservice.services.security;

import com.dromakin.cloudservice.config.security.JWTAuthServer;
import com.dromakin.cloudservice.dto.AuthRequestDTO;
import com.dromakin.cloudservice.dto.AuthResponseDTO;
import com.dromakin.cloudservice.models.security.User;
import com.dromakin.cloudservice.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService {

    private final AuthenticationManager authenticationManager;
    private final JWTAuthServer jwtAuthServer;
    private final UserService userService;

    @Autowired
    public JWTServiceImpl(AuthenticationManager authenticationManager, JWTAuthServer jwtAuthServer, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthServer = jwtAuthServer;
        this.userService = userService;
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO authRequestDTO) {
        try {
            String login = authRequestDTO.getLogin();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, authRequestDTO.getPassword()));
            User user = userService.findByLogin(login);

            if (user == null) {
                throw new UsernameNotFoundException("User with login " + login + " not found!");
            }

            Long lastEnter = new Date().getTime();
            String token = jwtAuthServer.createToken(login, user.getRoles(), lastEnter);
            userService.setLastEnter(user.getId(), lastEnter);

            return AuthResponseDTO.builder()
                    .token(token)
                    .build();

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username / password!");
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = jwtAuthServer.resolveToken(request);
        User user = userService.findByLogin(jwtAuthServer.getLoginByToken(token));

        if (user == null) {
            throw new BadCredentialsException("Invalid username / password!");
        }

        userService.setLastEnter(user.getId(), 1L);
    }
}
