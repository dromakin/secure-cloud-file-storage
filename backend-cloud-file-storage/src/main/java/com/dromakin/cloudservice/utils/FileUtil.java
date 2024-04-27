/*
 * File:     FileUtil
 * Package:  com.dromakin.cloudservice.utils
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 10.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.10
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.utils;

import org.apache.commons.io.FileUtils;

public class FileUtil {

    public static String bytesToHumanString(long bytes) {
        return FileUtils.byteCountToDisplaySize(bytes);
    }

    public static Long getSizeByBytes(byte[] bytes) {
        return (long) (bytes.length / 1024.0);
    }
}
