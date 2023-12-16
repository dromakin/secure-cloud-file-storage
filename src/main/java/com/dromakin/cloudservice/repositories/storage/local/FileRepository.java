/*
 * File:     FileRepository
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
package com.dromakin.cloudservice.repositories.storage.local;

import com.dromakin.cloudservice.models.File;
import com.dromakin.cloudservice.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> getFileByNameAndStatus(String name, Status status);

    List<File> findFilesByStatus(Status status);

    List<File> findFilesByUserLoginAndStatus(String login, Status status);
}
