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

import com.dromakin.cloudservice.dao.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Validated
@AllArgsConstructor
@Data
@Builder
public class NewUserDTO {
    @NotBlank
    private String login;

    @NotBlank
    private String password;

    @NotBlank
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @JsonProperty("last_name")
    private String lastName;

    @NotBlank
    private String email;

    private List<String> roles;
}
