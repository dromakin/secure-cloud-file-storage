/*
 * File:     MinioRepositoryImpl
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

import com.dromakin.cloudservice.utils.UserUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.dromakin.cloudservice.services.storage.BaseStorageService.FOLDER_STATIC_FILE_NAME;

@Slf4j
@Repository
public class MinioRepositoryImpl implements MinioRepository {

    @Value("${minio.bucket:user-files}")
    private String rootBucket;
    private final MinioClient minioClient;

    @Autowired
    public MinioRepositoryImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // create
    public void createFolder(long userId, String rawPath) throws MinioException {
        var path = Paths.get(getUserFolder(userId), rawPath).toString();

        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(rootBucket).object(path).stream(new ByteArrayInputStream(new byte[0]), 0, -1).build());
            log.info("Created folder: " + rawPath);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MinioException(e.getMessage());
        }
    }

    public void uploadFile(long userId, String rawFullPath, MultipartFile file) throws MinioException {
        String fullPath = Paths.get(getUserFolder(userId), rawFullPath).toString();

        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(rootBucket).object(fullPath).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());
            log.info("Uploaded file: " + file.getOriginalFilename());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MinioException(e.getMessage());
        }
    }

    // read
    public InputStream downloadFile(String fullPath) throws MinioException {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(rootBucket).object(fullPath).build());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MinioException("Error while getting file");
        }
    }

    @Override
    public InputStream findInStorage(Long userId, String filename) throws MinioException {
        String location = Paths.get(getUserFolder(userId), filename).toString();
        return downloadFile(location);
    }

    // update


    // delete
    public boolean delete(String path) throws MinioException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(rootBucket).object(path).build());
            log.info("Deleted object: " + path);
            return true;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioException(e.getMessage());
        }
    }

    public void deleteUserFolder(long userId) throws MinioException {
        var path = getUserFolder(userId);

        try {
            var objects = minioClient.listObjects(ListObjectsArgs.builder().bucket(rootBucket).recursive(true).prefix(path).build());

            for (var result : objects) {
                var item = result.get();
                delete(item.objectName());
                log.info("Deleted object: " + item.objectName());
            }

            this.delete(path);
            log.info("Deleted folder: " + path);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MinioException(e.getMessage());
        }
    }


    // utils
    public String getUserFolder(long userId) {
        return UserUtils.getUserBucket(userId);
    }

    public boolean checkIfUserFolderExist(long userId) throws MinioException {
        /*
        https://stackoverflow.com/questions/72611255/how-to-check-if-object-exist-in-minio-bucket-using-minio-java-sdk

        Чтобы проверить наличие папки в bucket приходится делать запрос и обрабатывать ошибку, считая ее ответом.
         */
        var path = Paths.get(getUserFolder(userId), "/" + FOLDER_STATIC_FILE_NAME).toString();

        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(rootBucket).object(path).build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (InsufficientDataException | InternalException | InvalidKeyException
                 | InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException
                 | XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioException(e.getMessage());
        }
    }

}
