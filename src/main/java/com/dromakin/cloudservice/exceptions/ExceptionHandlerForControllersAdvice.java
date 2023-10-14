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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestControllerAdvice
public class ExceptionHandlerForControllersAdvice {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleException(DataNotFoundException exception) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .id(HttpStatus.BAD_REQUEST.value())
                .message("No data: " + exception)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileNotFoundException(FileNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(400, e.getMessage()));
    }

    @ExceptionHandler(value = {IOException.class, NoSuchAlgorithmException.class})
    public ResponseEntity<ErrorResponseDTO> handleIOException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO(500, e.getMessage()));
    }

}
