package com.dromakin.cloudservice.controllers;

import com.dromakin.cloudservice.dto.AuthRequestDTO;
import com.dromakin.cloudservice.dto.AuthResponseDTO;
import com.dromakin.cloudservice.services.security.JWTService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthControllerTest {

    private final static String ENDPOINT_LOGIN = "/cloud/login";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

	public static DockerComposeContainer environment = new DockerComposeContainer(
            new File("src/test/resources/docker-compose-db-only.yml")).withLocalCompose(true);

    @BeforeAll
    public static void setUp() {
        environment.start();
    }

    @AfterAll
    public static void setDown() {
        environment.stop();
    }

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testLoginUser() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        var authRequestDTO = new AuthRequestDTO("admin@localhost", "admin");
        String authRequestJsonString = ow.writeValueAsString(authRequestDTO);
        var authResponseDTO = new AuthResponseDTO("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBsb2NhbGhvc3QiLCJyb2xlcyI6WyJST0xFX0FETUlOIl0sImVudGVyVGltZSI6MTcwMjQ1MzMwNzg0NiwiaWF0IjoxNzAyNDUzMzA3LCJleHAiOjE3MDI0NTY5MDd9.E8Qi-zKmLySB3bLNSuoI0dkoQ_AWm707wtYIehdX9Z0");
        String authResponseJsonString = ow.writeValueAsString(authResponseDTO);

        when(jwtService.login(authRequestDTO)).thenReturn(authResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequestJsonString)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.auth-token").value(authResponseDTO.getToken()))
                .andDo(MockMvcResultHandlers.print());
    }

}

