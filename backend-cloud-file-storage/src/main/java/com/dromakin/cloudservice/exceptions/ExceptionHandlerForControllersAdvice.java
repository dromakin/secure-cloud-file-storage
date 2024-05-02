/*
 * File:     ExceptionHandlerForControllersAdvice
 * Package:  com.dromakin.netology_jdbc_dao.exceptions
 * Project:  netology_jdbc_dao
 *
 * Created by dromakin as 06.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.06
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.exceptions;

import com.dromakin.cloudservice.dto.ErrorResponseDTO;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletException;
import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerForControllersAdvice {

    // auth
    @ExceptionHandler(value = {UsernameNotFoundException.class, JwtAuthenticationException.class, BadCredentialsException.class, ServletException.class})
    public ResponseEntity<ErrorResponseDTO> handleAuthException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO(401, e.getMessage()));
    }

    // data
    @ExceptionHandler({FileNotFoundException.class, MinioException.class, UserServiceException.class})
    public ResponseEntity<ErrorResponseDTO> handleFileNotFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(400, e.getMessage()));
    }

    @ExceptionHandler(value = {IOException.class, IllegalStateException.class, Exception.class})
    public ResponseEntity<ErrorResponseDTO> handleIOException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(500, e.getMessage()));
    }

}
