/*
 * File:     JWTService
 * Package:  com.dromakin.cloudservice.services.security
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 12.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.12
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.services.security;

import com.dromakin.cloudservice.dto.AuthRequestDTO;
import com.dromakin.cloudservice.dto.AuthResponseDTO;

import javax.servlet.http.HttpServletRequest;

public interface JWTService {

    AuthResponseDTO login(AuthRequestDTO authRequestDTO);

    void logout(HttpServletRequest request);
}
