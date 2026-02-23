package com.fiap.soat12.os.cleanarch.exception;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Mock
    private WebRequest request;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
    }

    @Nested
    class CustomExceptions {

        @Test
        @DisplayName("Should handle InvalidTransitionException and return HTTP 422")
        void shouldHandleInvalidTransitionException() {
            // Arrange
            InvalidTransitionException ex = new InvalidTransitionException("Falha na transição de status");
            when(request.getDescription(false)).thenReturn("uri=/api/test");
            when(meterRegistry.counter("business.service_order.error", "reason", "invalid_status")).thenReturn(counter);

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleInvalidTransitionException(ex, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Falha na transição de status", response.getBody().getDetail());
            assertEquals("/api/test", response.getBody().getPath());
            verify(meterRegistry).counter("business.service_order.error", "reason", "invalid_status");
            verify(counter).increment();
        }

        @Test
        @DisplayName("Should handle NotFoundException and return HTTP 404")
        void shouldHandleNotFoundException() {
            // Arrange
            NotFoundException ex = new NotFoundException("Recurso não encontrado");
            when(request.getDescription(false)).thenReturn("uri=/api/test");
            when(meterRegistry.counter("business.service_order.error", "reason", "data_not_found")).thenReturn(counter);

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleNotFoundException(ex, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Recurso não encontrado", response.getBody().getDetail());
            assertEquals("/api/test", response.getBody().getPath());
            verify(meterRegistry).counter("business.service_order.error", "reason", "data_not_found");
            verify(counter).increment();
        }
    }

    @Nested
    class InternalExceptions {

        @Test
        @DisplayName("Should handle MessagingException and return HTTP 500")
        void shouldHandleMessagingException() {
            // Arrange
            MessagingException ex = new MessagingException("Erro e-mail");
            when(request.getDescription(false)).thenReturn("uri=/api/test");
            when(meterRegistry.counter("business.service_order.error", "reason", "notification_failed"))
                    .thenReturn(counter);

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleMessagingException(ex, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Falha ao enviar notificação da Ordem de Serviço.", response.getBody().getDetail());
            assertEquals("/api/test", response.getBody().getPath());
            verify(meterRegistry).counter("business.service_order.error", "reason", "notification_failed");
            verify(counter).increment();
        }

        @Test
        @DisplayName("Should handle generic Exception and return HTTP 500")
        void shouldHandleGlobalException() {
            // Arrange
            Exception ex = new Exception("Erro genérico banco caiu");
            when(request.getDescription(false)).thenReturn("uri=/api/test");
            when(meterRegistry.counter("business.service_order.error", "reason", "crash")).thenReturn(counter);

            // Act
            ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleGlobalException(ex, request);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Erro interno no processamento da requisição.", response.getBody().getDetail());
            assertEquals("/api/test", response.getBody().getPath());
            verify(meterRegistry).counter("business.service_order.error", "reason", "crash");
            verify(counter).increment();
        }
    }

    @Nested
    class StandardExceptions {

        @Test
        @DisplayName("Should handle IllegalArgumentException and return a formatted ErrorResponseDTO with HTTP 400")
        void handleIllegalArgumentException_shouldReturnBadRequest() {
            // Arrange
            String errorMessage = "Invalid argument provided.";
            IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
            String requestUri = "uri=/api/test";

            when(request.getDescription(false)).thenReturn(requestUri);

            // Act
            ResponseEntity<ErrorResponseDTO> responseEntity = exceptionHandler.handleIllegalArgumentException(exception,
                    request);
            ErrorResponseDTO responseBody = responseEntity.getBody();

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            assertNotNull(responseBody);
            assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.getStatus());
            assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody.getTitle());
            assertEquals(errorMessage, responseBody.getDetail());
            assertEquals("/api/test", responseBody.getPath());
        }
    }
}