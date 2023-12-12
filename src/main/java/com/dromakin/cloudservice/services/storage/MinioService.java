/*
 * File:     MinioService
 * Package:  com.dromakin.cloudservice.services.storage
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 12.12.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.12.12
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.services.storage;

import com.dromakin.cloudservice.exceptions.StorageException;
import com.dromakin.cloudservice.models.File;
import com.dromakin.cloudservice.models.Status;
import com.dromakin.cloudservice.models.security.User;
import com.dromakin.cloudservice.repositories.storage.local.FileRepository;
import com.dromakin.cloudservice.repositories.storage.remote.MinioRepository;
import com.dromakin.cloudservice.repositories.storage.remote.MinioRepositoryImpl;
import com.dromakin.cloudservice.repositories.user.UserRepository;
import com.dromakin.cloudservice.services.user.UserService;
import com.dromakin.cloudservice.utils.FileUtil;
import io.minio.errors.MinioException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Profile("default")
@Slf4j
@AllArgsConstructor
public class MinioService extends AbstractStorageService {

    private final MinioRepository minioRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    private final UserService userService;


    // create
    @Override
    public Long save(MultipartFile multipartFile, String fileName, String originalName) throws StorageException {
        User user = getUser();
        String userFolder = ((MinioRepositoryImpl) minioRepository).getUserFolder(user.getId());
        String location = Paths.get(userFolder, originalName).toString();

        File file;

        try {
            minioRepository.uploadFile(user.getId(), originalName, multipartFile);

            byte[] bytes = multipartFile.getBytes();
            file = new File(
                    originalName,
                    fileName,
                    FileUtil.getSizeByBytes(bytes),
                    FileUtil.bytesToHumanString(bytes.length),
                    location,
                    new Date(),
                    new Date(),
                    Status.ACTIVE,
                    getUser()
            );

        } catch (MinioException | IOException e) {
            throw new StorageException(e.getMessage());
        }

        File fileDb = fileRepository.save(file);
        return fileDb.getId();
    }

    // read
    @Override
    public Resource getByName(String filename) throws FileNotFoundException {
        File file = fileRepository.getFileByNameAndStatus(filename, Status.ACTIVE).orElseThrow(FileNotFoundException::new);

        InputStream inputStream;

        try {
            inputStream = minioRepository.findInStorage(getUser().getId(), file.getOriginalName());
        } catch (MinioException e) {
            throw new FileNotFoundException(e.getMessage());
        }

        return new InputStreamResource(inputStream);
    }

    public Resource findById(Long id) throws FileNotFoundException {
        File file = fileRepository.findById(id).orElseThrow(FileNotFoundException::new);
        InputStream inputStream;

        try {
            inputStream = minioRepository.findInStorage(getUser().getId(), file.getOriginalName());
        } catch (MinioException e) {
            throw new FileNotFoundException(e.getMessage());
        }

        return new InputStreamResource(inputStream);
    }

    @Override
    public List<File> getFiles() {
        List<File> files = fileRepository.findFilesByUserLoginAndStatus(getUser().getLogin(), Status.ACTIVE);

        if (files == null) {
            return new ArrayList<>();
        }

        return files;
    }

    // update
    @Override
    public String setNewFilename(String fileName, String newFileName) throws FileNotFoundException {
        File file = fileRepository.getFileByNameAndStatus(fileName, Status.ACTIVE).orElseThrow(FileNotFoundException::new);
        file.setName(newFileName);
        fileRepository.save(file);
        return file.getName();
    }

    // delete
    @Override
    public void delete(String filename) throws FileNotFoundException {
        File file = fileRepository.getFileByNameAndStatus(filename, Status.ACTIVE).orElseThrow(FileNotFoundException::new);
        file.setStatus(Status.DELETED);
        file.setUpdated(new Date());
        fileRepository.save(file);
    }

    @Override
    public void clear(String filename) throws StorageException {
        List<File> files = fileRepository.findFilesByStatus(Status.DELETED);

        try {
            for (File file : files) {
                if (minioRepository.delete(file.getLocation())) {
                    fileRepository.delete(file);
                }
            }
        } catch (MinioException e) {
            throw new StorageException(e.getMessage());
        }
    }

    // other
    @EventListener(ApplicationReadyEvent.class)
    public void createFoldersByUsers() throws MinioException {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            createUserInitialFolder(user.getId());
        }
    }

    private void createUserInitialFolder(long userId) throws MinioException {
        minioRepository.createFolder(userId, "/" + FOLDER_STATIC_FILE_NAME);
    }

    @Override
    protected User getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
