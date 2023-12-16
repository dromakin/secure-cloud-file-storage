/*
 * File:     BaseStorageService
 * Package:  com.dromakin.cloudservice.services.storage
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 11.12.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.12.11
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.services.storage;

import com.dromakin.cloudservice.models.File;
import com.dromakin.cloudservice.models.Status;
import com.dromakin.cloudservice.models.security.User;
import com.dromakin.cloudservice.repositories.storage.local.FileRepository;
import com.dromakin.cloudservice.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BaseStorageService {

    public static final String FOLDER_STATIC_FILE_NAME = ".folder.ini";

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Autowired
    public BaseStorageService(UserRepository userRepository, FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    // read
    public List<File> getFiles() {
        List<File> files = fileRepository.findFilesByUserLoginAndStatus(getUser().getLogin(), Status.ACTIVE);

        if (files == null) {
            return new ArrayList<>();
        }

        return files;
    }

    // update
    public String setNewFilename(String fileName, String newFileName) throws FileNotFoundException {
        File file = fileRepository.getFileByNameAndStatus(fileName, Status.ACTIVE).orElseThrow(FileNotFoundException::new);
        file.setName(newFileName);
        fileRepository.save(file);
        return file.getName();
    }

    // delete
    public void delete(String filename) throws FileNotFoundException {
        File file = fileRepository.getFileByNameAndStatus(filename, Status.ACTIVE).orElseThrow(FileNotFoundException::new);
        file.setStatus(Status.DELETED);
        file.setUpdated(new Date());
        fileRepository.save(file);
    }

    // other
    protected User getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

}
