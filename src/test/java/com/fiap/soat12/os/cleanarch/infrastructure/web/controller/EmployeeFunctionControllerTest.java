package com.fiap.soat12.os.cleanarch.infrastructure.web.controller;

import com.fiap.soat12.os.cleanarch.domain.repository.EmployeeFunctionRepository;
import com.fiap.soat12.os.cleanarch.exception.NotFoundException;
import com.fiap.soat12.os.cleanarch.infrastructure.persistence.entity.EmployeeFunctionJpaEntity;
import com.fiap.soat12.os.dto.employee.EmployeeFunctionRequestDTO;
import com.fiap.soat12.os.dto.employee.EmployeeFunctionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeFunctionControllerTest {

    @Mock
    private EmployeeFunctionRepository employeeFunctionRepository;

    @InjectMocks
    private EmployeeFunctionController employeeFunctionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateEmployeeFunction {
        @Test
        void shouldCreateEmployeeFunctionSuccessfully() {
            // Arrange
            EmployeeFunctionRequestDTO requestDTO = new EmployeeFunctionRequestDTO();
            requestDTO.setDescription("Mecânico");

            EmployeeFunctionJpaEntity entity = EmployeeFunctionJpaEntity.builder().id(1L).description("Mecânico")
                    .active(true).build();
            when(employeeFunctionRepository.save(any(EmployeeFunctionJpaEntity.class))).thenReturn(entity);

            // Act
            EmployeeFunctionResponseDTO result = employeeFunctionController.createEmployeeFunction(requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Mecânico", result.getDescription());
            verify(employeeFunctionRepository).save(any(EmployeeFunctionJpaEntity.class));
        }
    }

    @Nested
    class GetEmployeeFunctionById {
        @Test
        void shouldReturnEmployeeFunctionWhenFound() {
            // Arrange
            Long id = 1L;
            EmployeeFunctionJpaEntity entity = EmployeeFunctionJpaEntity.builder().id(1L).description("Mecânico")
                    .active(true).build();
            when(employeeFunctionRepository.findById(id)).thenReturn(Optional.of(entity));

            // Act
            EmployeeFunctionResponseDTO result = employeeFunctionController.getEmployeeFunctionById(id);

            // Assert
            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("Mecânico", result.getDescription());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenNotFound() {
            // Arrange
            Long id = 1L;
            when(employeeFunctionRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> employeeFunctionController.getEmployeeFunctionById(id));
        }
    }

    @Nested
    class GetAllEmployeeFunctions {
        @Test
        void shouldReturnAllActiveEmployeeFunctions() {
            // Arrange
            EmployeeFunctionJpaEntity entity = EmployeeFunctionJpaEntity.builder().id(1L).description("Mecânico")
                    .active(true).build();
            when(employeeFunctionRepository.findAll()).thenReturn(List.of(entity));

            // Act
            List<EmployeeFunctionResponseDTO> result = employeeFunctionController.getAllActiveEmployeeFunctions();

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("Mecânico", result.get(0).getDescription());
        }

        @Test
        void shouldReturnAllEmployeeFunctions() {
            // Arrange
            EmployeeFunctionJpaEntity entity = EmployeeFunctionJpaEntity.builder().id(1L).description("Mecânico")
                    .active(false).build();
            when(employeeFunctionRepository.findAll()).thenReturn(List.of(entity));

            // Act
            List<EmployeeFunctionResponseDTO> result = employeeFunctionController.getAllEmployeeFunctions();

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
        }
    }

    @Nested
    class UpdateEmployeeFunctionById {
        @Test
        void shouldUpdateSuccessfullyWhenFound() {
            // Arrange
            Long id = 1L;
            EmployeeFunctionRequestDTO requestDTO = new EmployeeFunctionRequestDTO();
            requestDTO.setDescription("Mecânico Senior");

            EmployeeFunctionJpaEntity entity = EmployeeFunctionJpaEntity.builder().id(1L).description("Mecânico")
                    .active(true).build();
            EmployeeFunctionJpaEntity updatedEntity = EmployeeFunctionJpaEntity.builder().id(1L)
                    .description("Mecânico Senior").active(true).build();

            when(employeeFunctionRepository.findById(id)).thenReturn(Optional.of(entity));
            when(employeeFunctionRepository.save(any(EmployeeFunctionJpaEntity.class))).thenReturn(updatedEntity);

            // Act
            EmployeeFunctionResponseDTO result = employeeFunctionController.updateEmployeeFunctionById(id, requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals("Mecânico Senior", result.getDescription());
        }
    }

    @Nested
    class StatusManagement {
        @Test
        void shouldInactivateSuccessfully() {
            // Arrange
            Long id = 1L;
            EmployeeFunctionJpaEntity entity = EmployeeFunctionJpaEntity.builder().id(1L).description("Mecânico")
                    .active(true).build();
            when(employeeFunctionRepository.findById(id)).thenReturn(Optional.of(entity));
            when(employeeFunctionRepository.save(any(EmployeeFunctionJpaEntity.class))).thenReturn(entity);

            // Act & Assert
            assertDoesNotThrow(() -> employeeFunctionController.inactivateEmployeeFunction(id));
            verify(employeeFunctionRepository).save(any(EmployeeFunctionJpaEntity.class));
        }

        @Test
        void shouldThrowExceptionWhenInactivatingNonExistent() {
            // Arrange
            Long id = 1L;
            when(employeeFunctionRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> employeeFunctionController.inactivateEmployeeFunction(id));
        }

        @Test
        void shouldActivateSuccessfully() {
            // Arrange
            Long id = 1L;
            EmployeeFunctionJpaEntity entity = EmployeeFunctionJpaEntity.builder().id(1L).description("Mecânico")
                    .active(false).build();
            when(employeeFunctionRepository.findById(id)).thenReturn(Optional.of(entity));
            when(employeeFunctionRepository.save(any(EmployeeFunctionJpaEntity.class))).thenReturn(entity);

            // Act & Assert
            assertDoesNotThrow(() -> employeeFunctionController.activateEmployeeFunction(id));
            verify(employeeFunctionRepository).save(any(EmployeeFunctionJpaEntity.class));
        }
    }
}
