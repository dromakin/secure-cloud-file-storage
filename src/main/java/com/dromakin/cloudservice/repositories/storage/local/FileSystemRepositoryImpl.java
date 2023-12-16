/*
 * File:     FileSystemRepositoryImpl
 * Package:  com.dromakin.cloudservice.repositories.impl
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 11.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.11
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.repositories.storage.local;

import com.dromakin.cloudservice.exceptions.StorageException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Repository
public class FileSystemRepositoryImpl implements FileSystemRepository {
    private static final String STRING_PATH_TO_DIR_RESOURCES = FileSystemRepository.class.getResource("/").getPath();

    @Override
    public FileSystemResource findInFileSystem(String location) {
        try {
            return new FileSystemResource(Paths.get(location));
        } catch (Exception ex) {
            // Handle access or file not found problems.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found!", ex);
        }
    }

    @Override
    public String save(byte[] content, String fileName) throws IOException {
        Path newFile = Paths.get(STRING_PATH_TO_DIR_RESOURCES + new Date().getTime() + "_" + fileName);
        Files.createDirectories(newFile.getParent());
        Files.write(newFile, content);
        return newFile.toAbsolutePath().toString();
    }

    @Override
    public boolean remove(String location) throws StorageException {
        Path file = Paths.get(location);
        try {
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }

    }

}
