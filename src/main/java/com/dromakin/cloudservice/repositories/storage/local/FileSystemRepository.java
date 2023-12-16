/*
 * File:     FileSystemRepository
 * Package:  com.dromakin.cloudservice.repositories
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

import java.io.IOException;

public interface FileSystemRepository {
    FileSystemResource findInFileSystem(String location);
    String save(byte[] content, String fileName) throws IOException;
    boolean remove(String location) throws StorageException;
}
