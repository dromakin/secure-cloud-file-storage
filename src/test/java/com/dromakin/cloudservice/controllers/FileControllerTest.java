package com.dromakin.cloudservice.controllers;

import com.dromakin.cloudservice.dto.AuthRequestDTO;
import com.dromakin.cloudservice.dto.AuthResponseDTO;
import com.dromakin.cloudservice.dto.FilenameDTO;
import com.dromakin.cloudservice.models.File;
import com.dromakin.cloudservice.services.security.JWTService;
import com.dromakin.cloudservice.services.storage.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.DockerComposeContainer;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest {

    private final static String ENDPOINT_FILE = "/cloud/file";
    private final static String ENDPOINT_LIST = "/cloud/list";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTService jwtService;

    @MockBean
    private StorageService storageService;

    private final static String PATH_TO_TEST_FILE = "src/test/resources/some-file.txt";

    public static DockerComposeContainer environment = new DockerComposeContainer(
            new java.io.File("src/test/resources/docker-compose-db-only.yml")).withLocalCompose(true);

    @BeforeAll
    public static void setUp() {
        environment.start();
    }

    @AfterAll
    public static void setDown() {
        environment.stop();
    }

    private static String TOKEN = "Bearer ";

    @BeforeEach
    public void setup() {
        var authRequestDTO = new AuthRequestDTO("admin@localhost", "admin");
        AuthResponseDTO authResponseDTO = jwtService.login(authRequestDTO);
        TOKEN = "Bearer " + authResponseDTO.getToken();
    }

    @Test
    public void testListFileEmpty() throws Exception {
        List<File> files = new ArrayList<>();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String responseFilesStringJson = ow.writeValueAsString(files);

        when(storageService.getFiles()).thenReturn(files);

        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", TOKEN);

        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_LIST + "?limit=3").contentType(org.springframework.http.MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(responseFilesStringJson))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testLoadFile() throws Exception {
        String filename = "hello.txt";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", TOKEN);

        mockMvc.perform(MockMvcRequestBuilders.multipart(ENDPOINT_FILE + "?filename=" + filename).file(file).headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testChangeNameFile() throws Exception {
        String filename = "hello.txt";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", TOKEN);

        storageService.save(file, filename, filename);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String newFileName = "newnamefile.txt";
        FilenameDTO fileNameDTO = new FilenameDTO(newFileName);
        String jsonString = ow.writeValueAsString(fileNameDTO);

        when(storageService.setNewFilename(filename, fileNameDTO.getName())).thenReturn(newFileName);

        mockMvc.perform(
                MockMvcRequestBuilders.put(ENDPOINT_FILE + "?filename=" + filename)
                        .headers(headers)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(jsonString)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonString))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testListDeleteFile() throws Exception {
        String filename = "hello.txt";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("auth-token", TOKEN);

        storageService.save(file, filename, filename);

        mockMvc.perform(MockMvcRequestBuilders.delete(ENDPOINT_FILE + "?filename=" + filename).headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        List<File> files = new ArrayList<>();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String responseFilesStringJson = ow.writeValueAsString(files);

        when(storageService.getFiles()).thenReturn(files);

        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT_LIST + "?limit=3").contentType(org.springframework.http.MediaType.APPLICATION_JSON).headers(headers))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(responseFilesStringJson))
                .andDo(MockMvcResultHandlers.print());
    }

}

