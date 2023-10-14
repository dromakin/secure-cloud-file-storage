/*
 * File:     ErrorResponseDTO
 * Package:  com.dromakin.netology_money_transfer_service.dto
 * Project:  netology_money_transfer_service
 *
 * Created by dromakin as 28.08.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.08.28
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ErrorResponseDTO {
    private final int id;
    private final String message;
}
