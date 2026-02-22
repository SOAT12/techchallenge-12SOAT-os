package com.fiap.soat12.os.cleanarch.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void shouldCreateBusinessException() {
        String message = "Test Exception";
        BusinessException exception = new BusinessException(message);
        assertEquals(message, exception.getMessage());
    }
}
