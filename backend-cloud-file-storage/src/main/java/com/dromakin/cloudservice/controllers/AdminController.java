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
import com.dromakin.cloudservice.dao.security.User;
import com.dromakin.cloudservice.dto.UserProfileDTO;
import com.dromakin.cloudservice.mappers.UserMapper;
import com.dromakin.cloudservice.services.security.JWTService;
import com.dromakin.cloudservice.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/")
@AllArgsConstructor
public class AdminController {

    UserService userService;

    UserMapper userMapper;

    JWTService jwtService;

    @Operation(
            summary = "Get current user",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Get info about current user"),
                    @ApiResponse(responseCode = "400", description = "Info about current user not found")
            }
    )
    @GetMapping(value = "/users/me")
    public UserProfileDTO getCurrentUser(HttpServletRequest request) {
        String login = jwtService.getLoginByToken(request);
        User user = userService.findByLogin(login);
        return userMapper.userToUserDto(user);
    }

    @Operation(
            summary = "Get users",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Get info about all users"),
                    @ApiResponse(responseCode = "400", description = "Info about users not found")
            }
    )
    @GetMapping(value = "/users")
    public List<UserProfileDTO> getUsers(HttpServletRequest request) {
        List<User> users = userService.getAllUsers();
        return userMapper.usersToUserDtos(users);
    }

    @Operation(
            summary = "Get users by id",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Get info about all users"),
                    @ApiResponse(responseCode = "400", description = "Info about users not found")
            }
    )
    @GetMapping(value = "/users/{id}")
    public UserProfileDTO getUserById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return userMapper.userToUserDto(user);
    }

//    @PostMapping(value = "/user")
//    public UserProfileDTO createUser(@RequestBody UserProfileDTO userDto) {
//
//    }

    // TODO: create / update / delete

    /*
  async updateUser(token: string, userId: number, data: IUserProfileUpdate) {
    return axios.put(`${apiUrl}/api/v1/users/${userId}`, data, authHeaders(token));
  },
  async createUser(token: string, data: IUserProfileCreate) {
    return axios.post(`${apiUrl}/api/v1/users/`, data, authHeaders(token));
  },
  async passwordRecovery(email: string) {
    return axios.post(`${apiUrl}/api/v1/password-recovery/${email}`);
  },
  async resetPassword(password: string, token: string) {
    return axios.post(`${apiUrl}/api/v1/reset-password/`, {
      new_password: password,
      token,
    });
  },
     */

}
