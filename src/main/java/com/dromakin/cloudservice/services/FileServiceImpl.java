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
import com.dromakin.cloudservice.models.Status;
import com.dromakin.cloudservice.models.security.User;
import com.dromakin.cloudservice.repositories.FileRepository;
import com.dromakin.cloudservice.repositories.FileSystemRepository;
import com.dromakin.cloudservice.repositories.UserRepository;
import com.dromakin.cloudservice.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileSystemRepository fileSystemRepository;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Override
    public Resource getByName(String filename) throws FileNotFoundException {
        File file = fileRepository.getFileByName(filename).orElseThrow(FileNotFoundException::new);
        return fileSystemRepository.findInFileSystem(file.getLocation());
    }

    @Override
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

    @Override
    public String setNewFilename(String fileName, String newFileName) throws FileNotFoundException {
        File file = fileRepository.getFileByName(fileName).orElseThrow(FileNotFoundException::new);
        file.setName(newFileName);
        fileRepository.save(file);
        return file.getName();
    }

    @Override
    public Long save(byte[] bytes, String fileName, String originalName) throws IOException {
        String location = fileSystemRepository.save(bytes, fileName);
        File file = new File(
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

        File fileDb = fileRepository.save(file);
        return fileDb.getId();
    }

    @Override
    public void delete(String filename) throws FileNotFoundException {
        File file = fileRepository.getFileByName(filename).orElseThrow(FileNotFoundException::new);
        file.setStatus(Status.DELETED);
        file.setUpdated(new Date());
        fileRepository.save(file);
    }

    @Override
    public void clear(String filename) throws IOException {
        File file = fileRepository.getFileByName(filename).orElseThrow(FileNotFoundException::new);
        if (fileSystemRepository.remove(file.getLocation())) {
            fileRepository.delete(file);
        } else {
            throw new FileNotFoundException();
        }
    }

    private User getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
