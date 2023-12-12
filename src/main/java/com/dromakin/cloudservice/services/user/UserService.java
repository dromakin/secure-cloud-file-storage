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

import com.dromakin.cloudservice.models.security.User;

import java.util.List;

public interface UserService {

    User findByLogin(String login);

    List<User> getAllUsers();

    User getById(Long id);

    User register(User user);

    void setLastEnter(Long userId, Long lastEnter);

    User update(User user);

    void delete(Long id);

}
