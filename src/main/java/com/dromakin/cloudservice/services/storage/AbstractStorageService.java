/*
 * File:     AbstractStorageService
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

import com.dromakin.cloudservice.models.security.User;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractStorageService implements StorageService {
    protected static final String FOLDER_STATIC_FILE_NAME = ".folder.ini";
    abstract protected User getUser();



}
