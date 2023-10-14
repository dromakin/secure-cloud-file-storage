/*
 * File:     AuthenticationController
 * Package:  com.dromakin.cloudservice.controllers
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 10.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.10
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.controllers;

import com.dromakin.cloudservice.config.SwaggerConfig;
import com.dromakin.cloudservice.dto.AuthRequestDTO;
import com.dromakin.cloudservice.dto.AuthResponseDTO;
import com.dromakin.cloudservice.services.security.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/cloud/")
@AllArgsConstructor
public class AuthenticationController {

    private final JWTService jwtService;

    @Operation(
            summary = "Login",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(description = "Token",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "User not authenticated")
            }
    )
    @PostMapping("login")
    public AuthResponseDTO login(
            @RequestBody @Valid AuthRequestDTO authRequestDTO
    ) {
        return jwtService.login(authRequestDTO);
    }

    @Operation(
            summary = "Logout",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok"),
                    @ApiResponse(responseCode = "400", description = "Have been logout")
            }
    )
    @PostMapping("logout")
    public void logout(HttpServletRequest request) {
        jwtService.logout(request);
    }

}
