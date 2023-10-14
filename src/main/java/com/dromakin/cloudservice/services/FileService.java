/*
 * File:     FileService
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
package com.dromakin.cloudservice.services;

import com.dromakin.cloudservice.models.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface FileService {

    Resource getByName(String filename) throws FileNotFoundException;

    FileSystemResource findById(Long id) throws FileNotFoundException;

    List<File> getFiles();

    String setNewFilename(String fileName, String newFileName) throws FileNotFoundException;

    Long save(byte[] bytes, String fileName, String originalName) throws IOException, NoSuchAlgorithmException;

    void delete(String filename) throws FileNotFoundException;

    void clear(String filename) throws IOException;


}
