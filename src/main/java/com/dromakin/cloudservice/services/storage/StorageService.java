/*
 * File:     StorageService
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
package com.dromakin.cloudservice.services.storage;

import com.dromakin.cloudservice.exceptions.StorageException;
import com.dromakin.cloudservice.models.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

public interface StorageService {

    Long save(MultipartFile multipartFile, String fileName, String originalName) throws StorageException;

    Resource getByName(String filename) throws FileNotFoundException, StorageException;

    List<File> getFiles();

    String setNewFilename(String fileName, String newFileName) throws FileNotFoundException;

    void delete(String filename) throws FileNotFoundException;

    void clear(String filename) throws StorageException;


}
