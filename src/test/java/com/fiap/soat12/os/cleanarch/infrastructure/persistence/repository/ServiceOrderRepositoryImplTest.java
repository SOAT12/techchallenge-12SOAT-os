package com.fiap.soat12.os.cleanarch.infrastructure.persistence.repository;

import com.fiap.soat12.os.cleanarch.domain.model.*;
import com.fiap.soat12.os.cleanarch.infrastructure.persistence.entity.*;
import com.fiap.soat12.os.cleanarch.infrastructure.persistence.mapper.*;
import com.fiap.soat12.os.cleanarch.infrastructure.persistence.repository.jpa.ServiceOrderJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class) // Substitui o MockitoAnnotations.openMocks(this)
class ServiceOrderRepositoryImplTest {

    @Mock
    private ServiceOrderJpaRepository serviceOrderJpaRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ServiceOrderMapper serviceOrderMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private ServiceOrderRepositoryImpl serviceOrderRepository;

    @Nested
    class FindAll {
        @Test
        void shouldFindAll() {
            when(serviceOrderJpaRepository.findAll()).thenReturn(List.of(new ServiceOrderEntity()));
            when(serviceOrderMapper.toServiceOrder(any())).thenReturn(new ServiceOrder());

            List<ServiceOrder> result = serviceOrderRepository.findAll();

            assertFalse(result.isEmpty());
            verify(serviceOrderJpaRepository).findAll();
        }
    }

    @Nested
    class FindById {
        @Test
        void shouldFindById() {
            Long id = 1L;
            when(serviceOrderJpaRepository.findById(id)).thenReturn(Optional.of(new ServiceOrderEntity()));
            when(serviceOrderMapper.toServiceOrder(any())).thenReturn(new ServiceOrder());

            Optional<ServiceOrder> result = serviceOrderRepository.findById(id);

            assertTrue(result.isPresent());
            verify(serviceOrderJpaRepository).findById(id);
        }
    }

    @Nested
    class Save {

        private ServiceOrder serviceOrder;
        private ServiceOrderEntity serviceOrderEntity;

        @BeforeEach
        void setUpSave() {
            // Setup base para o Domínio
            serviceOrder = new ServiceOrder();
            serviceOrder.setId(10L);
            serviceOrder.setCustomer(Customer.builder().id(1L).build());
            serviceOrder.setVehicle(Vehicle.builder().id(1L).build());
            serviceOrder.setEmployee(Employee.builder().id(1L).build());

            VehicleService service = VehicleService.builder()
            .id(1L)
            .build();
            serviceOrder.setServices(Set.of(service));

            // Setup base para a Entidade
            serviceOrderEntity = new ServiceOrderEntity();
            serviceOrderEntity.setId(10L);
        }

        @Test
        void shouldSaveWithStockItems() {
            // Arrange
            StockItem stockItem = new StockItem(UUID.randomUUID(), 2, new BigDecimal("50.00"));
            serviceOrder.setStockItems(Set.of(stockItem));

            // Usando lenient() e matchers genéricos para evitar a UnnecessaryStubbingException
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(CustomerJpaEntity.class), any())).thenReturn(new CustomerJpaEntity());
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(VehicleJpaEntity.class), any())).thenReturn(new VehicleJpaEntity());
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(EmployeeJpaEntity.class), any())).thenReturn(new EmployeeJpaEntity());
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(VehicleServiceJpaEntity.class), any())).thenReturn(new VehicleServiceJpaEntity());

            org.mockito.Mockito.lenient().when(serviceOrderMapper.toServiceOrderEntity(any(), any(), any(), any(), any()))
                    .thenReturn(serviceOrderEntity);

            org.mockito.Mockito.lenient().when(serviceOrderJpaRepository.save(any())).thenReturn(serviceOrderEntity);
            org.mockito.Mockito.lenient().when(serviceOrderMapper.toServiceOrder(any())).thenReturn(serviceOrder);

            // Act
            ServiceOrder result = serviceOrderRepository.save(serviceOrder);

            // Assert
            assertNotNull(result);
            verify(serviceOrderJpaRepository, times(1)).save(serviceOrderEntity);
            assertNotNull(serviceOrderEntity.getStockItems());
            assertEquals(1, serviceOrderEntity.getStockItems().size());
        }

        @Test
        void shouldSaveWithoutStockItems() {
            // Arrange
            serviceOrder.setStockItems(null);

            // Usando lenient() e matchers genéricos para evitar a UnnecessaryStubbingException
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(CustomerJpaEntity.class), any())).thenReturn(new CustomerJpaEntity());
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(VehicleJpaEntity.class), any())).thenReturn(new VehicleJpaEntity());
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(EmployeeJpaEntity.class), any())).thenReturn(new EmployeeJpaEntity());
            org.mockito.Mockito.lenient().when(entityManager.getReference(eq(VehicleServiceJpaEntity.class), any())).thenReturn(new VehicleServiceJpaEntity());

            org.mockito.Mockito.lenient().when(serviceOrderMapper.toServiceOrderEntity(any(), any(), any(), any(), any()))
                    .thenReturn(serviceOrderEntity);

            org.mockito.Mockito.lenient().when(serviceOrderJpaRepository.save(any())).thenReturn(serviceOrderEntity);
            org.mockito.Mockito.lenient().when(serviceOrderMapper.toServiceOrder(any())).thenReturn(serviceOrder);

            // Act
            ServiceOrder result = serviceOrderRepository.save(serviceOrder);

            // Assert
            assertNotNull(result);
            verify(serviceOrderJpaRepository, times(1)).save(serviceOrderEntity);
            assertNull(serviceOrderEntity.getStockItems());
        }
    }

    @Nested
    class FindAllFilteredAndSorted {
        @Test
        void shouldFindAllFilteredAndSorted() {
            when(serviceOrderJpaRepository.findAllFilteredAndSorted(any())).thenReturn(List.of(new ServiceOrderEntity()));
            when(serviceOrderMapper.toServiceOrder(any())).thenReturn(new ServiceOrder());

            List<ServiceOrder> result = serviceOrderRepository.findAllFilteredAndSorted(List.of());

            assertFalse(result.isEmpty());
            verify(serviceOrderJpaRepository).findAllFilteredAndSorted(any());
        }
    }

    @Nested
    class FindAllWithFilters {
        @Test
        void shouldFindAllWithFilters() {
            when(serviceOrderJpaRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class))).thenReturn(List.of(new ServiceOrderEntity()));
            when(serviceOrderMapper.toServiceOrder(any())).thenReturn(new ServiceOrder());

            List<ServiceOrder> result = serviceOrderRepository.findAllWithFilters(null, null, null);

            assertFalse(result.isEmpty());
            verify(serviceOrderJpaRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
        }
    }

    @Nested
    class CountByEmployeeAndStatusIn {
        @Test
        void shouldCountByEmployeeAndStatusIn() {
            when(employeeMapper.toEmployeeJpaEntity(any())).thenReturn(new EmployeeJpaEntity());
            when(serviceOrderJpaRepository.countByEmployeeAndStatusIn(any(), any())).thenReturn(1L);

            Long result = serviceOrderRepository.countByEmployeeAndStatusIn(Employee.builder().build(), List.of());

            assertNotNull(result);
            verify(serviceOrderJpaRepository).countByEmployeeAndStatusIn(any(), any());
        }
    }

    @Nested
    class FindByEmployeeAndStatusIn {
        @Test
        void shouldFindByEmployeeAndStatusIn() {
            when(employeeMapper.toEmployeeJpaEntity(any())).thenReturn(new EmployeeJpaEntity());
            when(serviceOrderJpaRepository.findByEmployeeAndStatusIn(any(), any())).thenReturn(List.of(new ServiceOrderEntity()));
            when(serviceOrderMapper.toServiceOrder(any())).thenReturn(new ServiceOrder());

            List<ServiceOrder> result = serviceOrderRepository.findByEmployeeAndStatusIn(Employee.builder().build(), List.of());

            assertFalse(result.isEmpty());
            verify(serviceOrderJpaRepository).findByEmployeeAndStatusIn(any(), any());
        }
    }

    @Nested
    class FindByCustomerAndFinishedAtIsNull {
        @Test
        void shouldFindByCustomerAndFinishedAtIsNull() {
            when(customerMapper.toCustomerJpaEntity(any())).thenReturn(new CustomerJpaEntity());
            when(serviceOrderJpaRepository.findByCustomerAndFinishedAtIsNull(any())).thenReturn(List.of(new ServiceOrderEntity()));
            when(serviceOrderMapper.toServiceOrder(any())).thenReturn(new ServiceOrder());

            List<ServiceOrder> result = serviceOrderRepository.findByCustomerAndFinishedAtIsNull(Customer.builder().build());

            assertFalse(result.isEmpty());
            verify(serviceOrderJpaRepository).findByCustomerAndFinishedAtIsNull(any());
        }
    }

    @Nested
    class FindByVehicleAndFinishedAtIsNull {
        @Test
        void shouldFindByVehicleAndFinishedAtIsNull() {
            when(vehicleMapper.toVehicleJpaEntity(any())).thenReturn(new VehicleJpaEntity());
            when(serviceOrderJpaRepository.findByVehicleAndFinishedAtIsNull(any())).thenReturn(List.of(new ServiceOrderEntity()));
            when(serviceOrderMapper.toServiceOrder(any())).thenReturn(new ServiceOrder());

            List<ServiceOrder> result = serviceOrderRepository.findByVehicleAndFinishedAtIsNull(Vehicle.builder().build());

            assertFalse(result.isEmpty());
            verify(serviceOrderJpaRepository).findByVehicleAndFinishedAtIsNull(any());
        }
    }
}