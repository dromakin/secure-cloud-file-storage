/*
 * File:     MinioRepository
 * Package:  com.dromakin.cloudservice.repositories
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 06.12.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.12.06
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.repositories.storage.remote;


import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioRepository {

    // create
    void uploadFile(long userId, String rawFullPath, MultipartFile file) throws MinioException;

    // read
    InputStream findInStorage(Long userId, String filename) throws MinioException;

    // update

    // delete
    boolean delete(String path) throws MinioException;

    // other
    void createFolder(long userId, String rawPath) throws MinioException;

    boolean checkIfUserFolderExist(long userId) throws MinioException;
}
