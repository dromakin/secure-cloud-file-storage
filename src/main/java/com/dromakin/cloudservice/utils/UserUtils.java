/*
 * File:     UserUtils
 * Package:  com.dromakin.cloudservice.utils
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 06.12.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.12.06
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.utils;

public abstract class UserUtils {
    public static String getUserBucket(long id) {
        return "user-" + id + "-files";
    }
}

