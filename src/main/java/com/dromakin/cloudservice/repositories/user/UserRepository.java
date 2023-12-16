/*
 * File:     UserRepository
 * Package:  com.dromakin.cloudservice.repositories
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 10.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.10
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.repositories.user;

import com.dromakin.cloudservice.models.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.login = :login")
    Optional<User> findByLogin(@Param("login") String login);

    @Transactional
    @Modifying
    @Query("update User u set u.lastEnter = :lastEnter WHERE u.id = :userId")
    void setLastEnter(@Param("userId") Long userId, @Param("lastEnter") Long lastEnter);
}
