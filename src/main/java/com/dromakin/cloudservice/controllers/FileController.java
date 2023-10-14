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
import com.dromakin.cloudservice.dto.FilenameRequestDTO;
import com.dromakin.cloudservice.dto.FilenameResponseDTO;
import com.dromakin.cloudservice.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(
            summary = "Download File",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "File"),
                    @ApiResponse(responseCode = "400", description = "File not found!")
            }
    )
    @GetMapping(value = "/file")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam("filename") String filename
    ) throws IOException {
        Resource resource = fileService.getByName(filename);
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" +
                                Objects.requireNonNull(resource.getFilename()).replace(" ", "_")
                ).body(resource);
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
    @PreAuthorize("hasRole('ADMIN')")
    public FilenameResponseDTO editFilename(
            @RequestParam String filename,
            @RequestBody FilenameRequestDTO fileNameDTO
    ) throws FileNotFoundException {
        String acceptedFileName = fileService.setNewFilename(filename, fileNameDTO.getNewFileName());
        return FilenameResponseDTO.builder().name(acceptedFileName).build();
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
    @PreAuthorize("hasRole('WRITER')")
    public void uploadFile(
            @RequestParam("filename") String filename,
            @RequestParam("file") MultipartFile multipartFile
    ) throws Exception {
        fileService.save(multipartFile.getBytes(), filename, multipartFile.getOriginalFilename());
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
    @PreAuthorize("hasRole('WRITER')")
    public void delete(@RequestParam String filename) throws Exception {
        fileService.delete(filename);
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
    @PreAuthorize("hasRole('ADMIN')")
    public void clear(@RequestParam String filename) throws Exception {
        fileService.clear(filename);
    }

    // list of files
    @Operation(
            summary = "Delete file by Admin",
            security = {@SecurityRequirement(name = SwaggerConfig.AUTH_SECURITY_SCHEME)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "File deleted and clear!"),
                    @ApiResponse(responseCode = "400", description = "File not found")
            }
    )
    @GetMapping(value = "/list")
    @PreAuthorize("hasRole('USER')")
    public List<FileResponseDTO> getListFiles() {
        return fileService.getFiles().stream()
                .map(file -> FileResponseDTO.builder().filename(file.getName()).size(file.getSize()).build())
                .collect(Collectors.toList());
    }
}
