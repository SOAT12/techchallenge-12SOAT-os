package com.fiap.soat12.os.cleanarch.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockUnavailableExceptionTest {

    @Test
    void shouldCreateStockUnavailableException() {
        String message = "Test Exception";
        StockUnavailableException exception = new StockUnavailableException(message);
        assertEquals(message, exception.getMessage());
    }
}
