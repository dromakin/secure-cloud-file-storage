/*
 * File:     UserService
 * Package:  com.dromakin.cloudservice.services
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 10.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.10
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.services.user;

import com.dromakin.cloudservice.dao.security.Role;
import com.dromakin.cloudservice.dao.security.User;
import com.dromakin.cloudservice.exceptions.UserServiceException;

import java.util.List;

public interface UserService {

    User findByLogin(String login);

    boolean isUserLoginExist(String login);

    List<User> getAllUsers();

    User getById(Long id);

    User createUser(User user);

    void setLastEnter(Long userId, Long lastEnter);

    void update(User user);

    void delete(Long id);

    // password
    void updatePassword(String login, String oldPassword, String newPassword) throws UserServiceException;

    // roles
    List<Role> getRoles();

    void addRoles(String login, List<String> newRoles) throws UserServiceException;

    void updateRoles(String login, List<String> newRoles) throws UserServiceException;

}
