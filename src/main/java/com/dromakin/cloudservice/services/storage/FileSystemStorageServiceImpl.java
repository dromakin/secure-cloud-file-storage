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
import com.dromakin.cloudservice.models.Status;
import com.dromakin.cloudservice.models.security.User;
import com.dromakin.cloudservice.repositories.storage.local.FileRepository;
import com.dromakin.cloudservice.repositories.storage.local.FileSystemRepository;
import com.dromakin.cloudservice.repositories.user.UserRepository;
import com.dromakin.cloudservice.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Profile("local")
@AllArgsConstructor
public class FileSystemStorageServiceImpl extends AbstractStorageService {

    private final FileSystemRepository fileSystemRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;


    // create
    @Override
    public Long save(MultipartFile multipartFile, String fileName, String originalName) throws StorageException {

        File file;

        try {
            byte[] bytes = multipartFile.getBytes();
            String location = fileSystemRepository.save(bytes, fileName);
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

        } catch (IOException e) {
            throw new StorageException(e.getMessage());
        }

        File fileDb = fileRepository.save(file);
        return fileDb.getId();
    }

    // read
    @Override
    public Resource getByName(String filename) throws FileNotFoundException {
        File file = fileRepository.getFileByNameAndStatus(filename, Status.ACTIVE).orElseThrow(FileNotFoundException::new);
        return fileSystemRepository.findInFileSystem(file.getLocation());
    }

    public FileSystemResource findById(Long id) throws FileNotFoundException {
        File file = fileRepository.findById(id).orElseThrow(FileNotFoundException::new);
        return fileSystemRepository.findInFileSystem(file.getLocation());
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
        for (File file : files) {
            if (fileSystemRepository.remove(file.getLocation())) {
                fileRepository.delete(file);
            }
        }
    }

    // other
    protected User getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
