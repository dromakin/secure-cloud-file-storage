/*
 * File:     UserServiceImpl
 * Package:  com.dromakin.cloudservice.services
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 11.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.11
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.services.user;

import com.dromakin.cloudservice.dao.Status;
import com.dromakin.cloudservice.dao.security.Role;
import com.dromakin.cloudservice.dao.security.User;
import com.dromakin.cloudservice.exceptions.UserServiceException;
import com.dromakin.cloudservice.repositories.user.RoleRepository;
import com.dromakin.cloudservice.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_USER_ROLE_NAME = "ROLE_USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByLogin(String login) {
        User user = userRepository.findByLogin(login).orElse(null);
        log.info("{} find by username {}", user == null ? null : user.getId(), login);
        return user;
    }

    public boolean isUserLoginExist(String login) {
        return findByLogin(login) != null;
    }

    @Override
    public User getById(Long id) {
        User result = userRepository.findById(id).orElse(null);
        log.info("User {} by id: {}", result == null ? "didn't find" : "found", id);
        return result;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        if (user.getRoles().isEmpty()) {
            user.setRoles(Collections.singletonList(roleRepository.findByName(DEFAULT_USER_ROLE_NAME)));
        }
        // password encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setLastEnter(1L);
        Date date = new Date();
        user.setCreated(date);
        user.setUpdated(date);
        User userDb = userRepository.save(user);
        log.info("{} created!", userDb.getId());
        return userDb;
    }

    @Override
    public void setLastEnter(Long userId, Long lastEnter) {
        userRepository.setLastEnter(userId, lastEnter);
    }

    @Override
    public void update(User user) {
        Date date = new Date();
        user.setUpdated(date);
        userRepository.updateUserDetails(
                user.getLogin(),
                user.getEmail(), user.getFirstName(), user.getLastName(),
                user.getUpdated()
        );
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("{} deleted!", id);
    }

    // password

    @Override
    public void updatePassword(String login, String oldPassword, String newPassword) throws UserServiceException {
        User user = findByLogin(login);
        if (user.getStatus() != Status.ACTIVE) {
            String errMsg = "Can't reset password! User is not active!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            String errMsg = "Can't reset password! Old password is not equals!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        if (oldPassword.equals(newPassword)) {
            String errMsg = "Can't reset password! Old password equals new password!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        Date date = new Date();
        userRepository.resetPasswordUser(login, passwordEncoder.encode(newPassword), date);
    }

    // roles
    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void addRoles(String login, List<String> newRoles) throws UserServiceException {
        User user = findByLogin(login);
        if (user.getStatus() != Status.ACTIVE) {
            String errMsg = "Can't add roles! User is not active!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        List<Role> updatedRoles = user.getRoles();
        for (String roleStr : newRoles) {
            Role role = roleRepository.findByName(roleStr);
            if (role != null) {
                if (!updatedRoles.contains(role)) {
                    updatedRoles.add(role);
                }

            } else {
                String errMsg = "Can't add roles! Wrong role name!";
                log.error(errMsg);
                throw new UserServiceException(errMsg);
            }

        }

        Date date = new Date();
        user.setUpdated(date);
        user.setRoles(updatedRoles);
        userRepository.save(user);
    }

    @Override
    public void updateRoles(String login, List<String> newRoles) throws UserServiceException {
        User user = findByLogin(login);
        if (user.getStatus() != Status.ACTIVE) {
            String errMsg = "Can't update roles! User is not active!";
            log.error(errMsg);
            throw new UserServiceException(errMsg);
        }

        List<Role> updatedRoles = new ArrayList<>();
        for (String roleStr : newRoles) {
            Role role = roleRepository.findByName(roleStr);
            if (role != null) {
                if (!updatedRoles.contains(role)) {
                    updatedRoles.add(role);
                }

            } else {
                String errMsg = "Can't update roles! Wrong role name!";
                log.error(errMsg);
                throw new UserServiceException(errMsg);
            }

        }

        Date date = new Date();
        user.setUpdated(date);
        user.setRoles(updatedRoles);
        userRepository.save(user);
    }
}
