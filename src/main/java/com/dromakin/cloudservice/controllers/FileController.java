/*
 * File:     FileController
 * Package:  com.dromakin.cloudservice.controllers
 * Project:  netology-cloud-service
 *
 * Created by dromakin as 10.10.2023
 *
 * author - dromakin
 * maintainer - dromakin
 * version - 2023.10.10
 * copyright - ORGANIZATION_NAME Inc. 2023
 */
package com.dromakin.cloudservice.controllers;

import com.dromakin.cloudservice.config.SwaggerConfig;
import com.dromakin.cloudservice.dto.FileResponseDTO;
import com.dromakin.cloudservice.dto.FilenameDTO;
import com.dromakin.cloudservice.exceptions.StorageException;
import com.dromakin.cloudservice.models.File;
import com.dromakin.cloudservice.services.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(value = "/cloud")
@AllArgsConstructor
public class FileController {

    private final StorageService storageService;

    @Operation(
            summary = "Download File",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "File"),
                    @ApiResponse(responseCode = "400", description = "File not found!")
            }
    )
    @GetMapping(value = "/file")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("filename") String filename
    ) throws IOException, StorageException {
        Resource resource = storageService.getByName(filename);
        var headers = new HttpHeaders();
        var encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Operation(
            summary = "Edit file name",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "File name updated!"),
                    @ApiResponse(responseCode = "400", description = "File not found!")
            }
    )
    @PutMapping(value = "/file")
    public FilenameDTO editFilename(
            @RequestParam String filename,
            @RequestBody FilenameDTO fileNameDTO
    ) throws FileNotFoundException {
        String acceptedFileName = storageService.setNewFilename(filename, fileNameDTO.getName());
        return FilenameDTO.builder().name(acceptedFileName).build();
    }

    @Operation(
            summary = "Upload File",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok"),
                    @ApiResponse(responseCode = "400", description = "Upload failed!")
            }
    )
    @PostMapping(value = "/file")
    public void uploadFile(
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile multipartFile
    ) throws StorageException {
        storageService.save(multipartFile, filename, multipartFile.getOriginalFilename());
    }

    @Operation(
            summary = "Delete file",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "File deleted!"),
                    @ApiResponse(responseCode = "400", description = "File not found")
            }
    )
    @DeleteMapping(value = "/file")
    public void delete(@RequestParam String filename) throws FileNotFoundException {
        storageService.delete(filename);
    }


    // optional
    @Operation(
            summary = "Delete file by Admin",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "File deleted and clear!"),
                    @ApiResponse(responseCode = "400", description = "File not found")
            }
    )
    @DeleteMapping(value = "/clear")
    public void clear(@RequestParam String filename) throws StorageException {
        storageService.clear(filename);
    }

    // list of files
    @Operation(
            summary = "Get files",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Get List of Files"),
                    @ApiResponse(responseCode = "400", description = "Files not found")
            }
    )
    @GetMapping(value = "/list")
    public List<FileResponseDTO> getListFiles(@RequestParam int limit) {
        List<File> files = storageService.getFiles();
        return files.stream()
                .map(file -> FileResponseDTO.builder().filename(file.getName()).size(file.getSize()).build())
                .limit(limit <= 0 ? 1 : limit)
                .collect(Collectors.toList());
    }
}
