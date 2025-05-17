package com.phonebook;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    @Test
    void handleAllExceptions_returnsInternalServerError() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new RuntimeException("Test error");
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<Object> response = handler.handleAllExceptions(ex, request);

        assertNotNull(response.getBody());
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Test error"));
        assertTrue(response.getBody().toString().contains("Internal Server Error"));
    }
} 