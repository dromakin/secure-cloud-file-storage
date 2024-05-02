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
package com.dromakin.cloudservice.controllers.admin;

import com.dromakin.cloudservice.config.SwaggerConfig;
import com.dromakin.cloudservice.dao.security.Role;
import com.dromakin.cloudservice.dao.security.User;
import com.dromakin.cloudservice.dto.*;
import com.dromakin.cloudservice.exceptions.UserServiceException;
import com.dromakin.cloudservice.mappers.UserMapper;
import com.dromakin.cloudservice.services.security.JWTService;
import com.dromakin.cloudservice.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/")
@AllArgsConstructor
@Slf4j
public class UserController {

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

    @Operation(
            summary = "Create user",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "New User created"),
                    @ApiResponse(responseCode = "400", description = "Can't create new user")
            }
    )
    @PostMapping(value = "/user")
    public UserProfileDTO createUser(@RequestBody NewUserDTO userDto) throws UserServiceException {
        if (userService.isUserLoginExist(userDto.getLogin())) {
            String errMsg = "Login has already exist!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        User newUser = userService.createUser(userMapper.newUserDtoToUser(userDto));
        return userMapper.userToUserDto(newUser);
    }

    @Operation(
            summary = "Update user",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated"),
                    @ApiResponse(responseCode = "400", description = "Can't update user")
            }
    )
    @PatchMapping(value = "/user")
    public void updateUser(
            @RequestBody UpdateUserDTO updateUserDTO,
            @RequestParam String login
    ) throws UserServiceException {
        if (!userService.isUserLoginExist(login)) {
            String errMsg = "User login not found! Create before update!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        User userToUpdate = userMapper.updateUserDtoToUser(updateUserDTO);
        userToUpdate.setLogin(login);
        userService.update(userToUpdate);
    }

    @Operation(
            summary = "Delete user by login",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted!"),
                    @ApiResponse(responseCode = "400", description = "User not found!")
            }
    )
    @DeleteMapping(value = "/user")
    public void deleteUserByLogin(@RequestParam String login) throws UserServiceException {
        if (!userService.isUserLoginExist(login)) {
            String errMsg = "User login not found!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        userService.delete(userService.findByLogin(login).getId());
    }

    @Operation(
            summary = "Delete user by id",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted!"),
                    @ApiResponse(responseCode = "400", description = "User not found!")
            }
    )
    @DeleteMapping(value = "/users/{id}")
    public void deleteUserByLogin(@PathVariable("id") Long id) throws UserServiceException {
        User user = userService.getById(id);
        if (user != null) {
            String errMsg = "User login not found!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        userService.delete(id);
    }

    // password
    @Operation(
            summary = "Update password for user by login",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password updated!"),
                    @ApiResponse(responseCode = "400", description = "Password can't be updated!")
            }
    )
    @PatchMapping(value = "/user/password")
    public void updatePassword(@RequestBody UpdatePasswordUserDTO updatePasswordUserDTO) throws UserServiceException {
        if (!userService.isUserLoginExist(updatePasswordUserDTO.getLogin())) {
            String errMsg = "User login not found! Create before update!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        userService.updatePassword(updatePasswordUserDTO.getLogin(), updatePasswordUserDTO.getOldPassword(), updatePasswordUserDTO.getNewPassword());
    }

    // roles
    @Operation(
            summary = "Get roles",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Get roles!"),
                    @ApiResponse(responseCode = "400", description = "Roles not found!")
            }
    )
    @GetMapping(value = "/user/roles")
    public RolesDTO getRoles(HttpServletRequest request) {
        List<Role> roles = userService.getRoles();
        return userMapper.listRolesToDto(roles);
    }

    @Operation(
            summary = "Add roles to user by login",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Add new roles to user!"),
                    @ApiResponse(responseCode = "400", description = "Roles can't be added to user!")
            }
    )
    @PostMapping(value = "/user/roles")
    public void addRoles(@RequestBody RolesUserDTO rolesUserDTO) throws UserServiceException {
        if (!userService.isUserLoginExist(rolesUserDTO.getLogin())) {
            String errMsg = "User login not found! Create before update!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        userService.addRoles(rolesUserDTO.getLogin(), rolesUserDTO.getRoles());
    }

    @Operation(
            summary = "Update roles for user by login",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Roles updated!"),
                    @ApiResponse(responseCode = "400", description = "Roles can't be updated!")
            }
    )
    @PutMapping(value = "/user/roles")
    public void updateRoles(@RequestBody RolesUserDTO rolesUserDTO) throws UserServiceException {
        if (!userService.isUserLoginExist(rolesUserDTO.getLogin())) {
            String errMsg = "User login not found! Create before update!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        userService.updateRoles(rolesUserDTO.getLogin(), rolesUserDTO.getRoles());
    }

}
