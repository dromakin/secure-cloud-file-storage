/*
 * File:     AuthRequestDTO
 * Package:  com.dromakin.cloudservice.dto
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 12.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.12
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@AllArgsConstructor
@Data
@Builder
public class UpdatePasswordUserDTO {
    @NotBlank
    private String login;

    @NotBlank
    @JsonProperty("new_password")
    private String newPassword;

    @NotBlank
    @JsonProperty("old_password")
    private String oldPassword;
}
