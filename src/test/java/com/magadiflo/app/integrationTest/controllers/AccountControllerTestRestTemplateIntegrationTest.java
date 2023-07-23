package com.magadiflo.app.integrationTest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magadiflo.app.models.dto.TransactionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"/test-account-cleanup.sql", "/test-account-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTestRestTemplateIntegrationTest {
    @Autowired
    private TestRestTemplate client;
    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;

    @Test
    void should_transfer_amount_between_accounts() throws JsonProcessingException {
        TransactionDTO dto = new TransactionDTO(1L, 1L, 2L, new BigDecimal("500"));

        ResponseEntity<String> response = this.client.postForEntity(this.createAbsolutePath("/api/v1/accounts/transfer"), dto, String.class);
        String jsonString = response.getBody();
        JsonNode jsonNode = this.objectMapper.readTree(jsonString);

        assertNotNull(jsonString);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("transferencia exitosa", jsonNode.get("message").asText());
    }

    private String createAbsolutePath(String uri) {
        return String.format("http://localhost:%d%s", this.port, uri);
    }
}