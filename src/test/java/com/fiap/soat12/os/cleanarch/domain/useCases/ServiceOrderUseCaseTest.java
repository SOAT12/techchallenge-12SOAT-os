package com.fiap.soat12.os.cleanarch.domain.useCases;

import com.fiap.soat12.os.cleanarch.domain.model.*;
import com.fiap.soat12.os.cleanarch.exception.NotFoundException;
import com.fiap.soat12.os.cleanarch.gateway.ServiceOrderGateway;
import com.fiap.soat12.os.cleanarch.util.Status;
import com.fiap.soat12.os.dto.serviceorder.ServiceOrderRequestDTO;
import jakarta.mail.MessagingException;
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

class ServiceOrderUseCaseTest {

    @Mock
    private ServiceOrderGateway serviceOrderGateway;

    @Mock
    private EmployeeUseCase employeeUseCase;

    @Mock
    private CustomerUseCase customerUseCase;

    @Mock
    private NotificationUseCase notificationUseCase;

    @Mock
    private VehicleUseCase vehicleUseCase;

    @Mock
    private VehicleServiceUseCase vehicleServiceUseCase;

    @Mock
    private io.micrometer.core.instrument.MeterRegistry meterRegistry;

    @Mock
    private com.fiap.soat12.os.cleanarch.infrastructure.messaging.publisher.SqsEventPublisher sqsEventPublisher;

    @Mock
    private com.fiap.soat12.os.cleanarch.infrastructure.persistence.repository.jpa.ServiceOrderJpaRepository serviceOrderJpaRepository;

    @InjectMocks
    private ServiceOrderUseCase serviceOrderUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class FindById {

        @Test
        void shouldReturnServiceOrderWhenFound() {
            // Arrange
            Long id = 1L;
            ServiceOrder serviceOrder = ServiceOrder.builder().id(id).build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(serviceOrder));

            // Act
            ServiceOrder result = serviceOrderUseCase.findById(id);

            // Assert
            assertNotNull(result);
            assertEquals(id, result.getId());
            verify(serviceOrderGateway, times(1)).findById(id);
        }

        @Test
        void shouldThrowNotFoundExceptionWhenServiceOrderNotFound() {
            // Arrange
            Long id = 1L;
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> serviceOrderUseCase.findById(id));
            verify(serviceOrderGateway, times(1)).findById(id);
        }
    }

    @Nested
    class CreateServiceOrder {

        @Test
        void shouldCreateServiceOrderWithEmployeeId() {
            // Arrange
            ServiceOrderRequestDTO requestDTO = new ServiceOrderRequestDTO();
            requestDTO.setCustomerId(1L);
            requestDTO.setVehicleId(1L);
            requestDTO.setEmployeeId(1L);
            requestDTO.setStockItems(List.of());
            requestDTO.setServices(List.of());

            Customer customer = Customer.builder().id(1L).build();
            Vehicle vehicle = Vehicle.builder().id(1L).build();
            Employee employee = Employee.builder().id(1L).build();

            io.micrometer.core.instrument.Counter counter = mock(io.micrometer.core.instrument.Counter.class);

            when(customerUseCase.getCustomerById(anyLong())).thenReturn(customer);
            when(vehicleUseCase.getVehicleById(anyLong())).thenReturn(vehicle);
            when(employeeUseCase.getEmployeeById(anyLong())).thenReturn(employee);
            when(serviceOrderGateway.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArguments()[0]);
            when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(counter);

            // Act
            ServiceOrder result = serviceOrderUseCase.createServiceOrder(requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(customer, result.getCustomer());
            assertEquals(vehicle, result.getVehicle());
            assertEquals(employee, result.getEmployee());
            verify(serviceOrderGateway, times(1)).save(any(ServiceOrder.class));
            verify(notificationUseCase, times(1)).notifyMechanicAssignedToOS(any(ServiceOrder.class),
                    any(Employee.class));
            verify(counter, times(1)).increment();
        }

        @Test
        void shouldCreateServiceOrderFindingMostAvailableEmployee() {
            // Arrange
            ServiceOrderRequestDTO requestDTO = new ServiceOrderRequestDTO();
            requestDTO.setCustomerId(1L);
            requestDTO.setVehicleId(1L);
            requestDTO.setStockItems(List.of());
            requestDTO.setServices(List.of());

            Customer customer = Customer.builder().id(1L).build();
            Vehicle vehicle = Vehicle.builder().id(1L).build();
            Employee employee = Employee.builder().id(1L).build();

            io.micrometer.core.instrument.Counter counter = mock(io.micrometer.core.instrument.Counter.class);

            when(customerUseCase.getCustomerById(anyLong())).thenReturn(customer);
            when(vehicleUseCase.getVehicleById(anyLong())).thenReturn(vehicle);
            when(employeeUseCase.getByEmployeeFunction(anyString())).thenReturn(List.of(employee));
            when(serviceOrderGateway.countByEmployeeAndStatusIn(any(), any())).thenReturn(0L);
            when(serviceOrderGateway.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArguments()[0]);
            when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(counter);

            // Act
            ServiceOrder result = serviceOrderUseCase.createServiceOrder(requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(customer, result.getCustomer());
            assertEquals(vehicle, result.getVehicle());
            assertEquals(employee, result.getEmployee());
            verify(serviceOrderGateway, times(1)).save(any(ServiceOrder.class));
            verify(notificationUseCase, times(1)).notifyMechanicAssignedToOS(any(ServiceOrder.class),
                    any(Employee.class));
            verify(counter, times(1)).increment();
        }
    }

    // @Nested
    // class CreateServiceOrderFull {
    //
    // @Test
    // void shouldCreateServiceOrderFull() {
    // // Arrange
    // ServiceOrderFullCreationRequestDTO requestDTO = new
    // ServiceOrderFullCreationRequestDTO();
    // requestDTO.setCustomer(new
    // com.fiap.soat12.os.dto.customer.CustomerRequestDTO());
    // requestDTO.setVehicle(new
    // com.fiap.soat12.os.dto.vehicle.VehicleRequestDTO());
    //
    // Customer customer = Customer.builder().id(1L).build();
    // Vehicle vehicle = Vehicle.builder().id(1L).build();
    // Employee employee = Employee.builder().id(1L).build();
    //
    // when(customerUseCase.createCustomer(any())).thenReturn(customer);
    // when(vehicleUseCase.create(any())).thenReturn(vehicle);
    // when(employeeUseCase.getByEmployeeFunction(anyString())).thenReturn(List.of(employee));
    // when(serviceOrderGateway.countByEmployeeAndStatusIn(any(),
    // any())).thenReturn(0L);
    // when(serviceOrderGateway.save(any(ServiceOrder.class))).thenAnswer(i ->
    // i.getArguments()[0]);
    //
    // // Act
    // ServiceOrder result = serviceOrderUseCase.createServiceOrder(requestDTO);
    //
    // // Assert
    // assertNotNull(result);
    // assertEquals(customer, result.getCustomer());
    // assertEquals(vehicle, result.getVehicle());
    // assertEquals(employee, result.getEmployee());
    // verify(serviceOrderGateway, times(1)).save(any(ServiceOrder.class));
    // verify(notificationUseCase,
    // times(1)).notifyMechanicAssignedToOS(any(ServiceOrder.class),
    // any(Employee.class));
    // }
    // }

    @Nested
    class FindAllOrders {
        @Test
        void shouldReturnAllOrders() {
            // Arrange
            when(serviceOrderGateway.findAll()).thenReturn(List.of(ServiceOrder.builder().build()));

            // Act
            List<ServiceOrder> result = serviceOrderUseCase.findAllOrders();

            // Assert
            assertFalse(result.isEmpty());
            verify(serviceOrderGateway).findAll();
        }
    }

    @Nested
    class FindAllOrdersFiltered {
        @Test
        void shouldReturnFilteredOrders() {
            // Arrange
            when(serviceOrderGateway.findAllFilteredAndSorted(any()))
                    .thenReturn(List.of(ServiceOrder.builder().build()));

            // Act
            List<ServiceOrder> result = serviceOrderUseCase.findAllOrdersFiltered();

            // Assert
            assertFalse(result.isEmpty());
            verify(serviceOrderGateway).findAllFilteredAndSorted(any());
        }
    }

    @Nested
    class FindByCustomerInfo {
        @Test
        void shouldReturnOrdersForCustomer() {
            // Arrange
            String cpf = "12345678900";
            Customer customer = Customer.builder().cpf(cpf).build();
            when(customerUseCase.getCustomerByCpf(cpf)).thenReturn(customer);
            when(serviceOrderGateway.findByCustomerAndFinishedAtIsNull(customer))
                    .thenReturn(List.of(ServiceOrder.builder().build()));

            // Act
            List<ServiceOrder> result = serviceOrderUseCase.findByCustomerInfo(cpf);

            // Assert
            assertFalse(result.isEmpty());
            verify(serviceOrderGateway).findByCustomerAndFinishedAtIsNull(customer);
        }
    }

    @Nested
    class FindByVehicleInfo {
        @Test
        void shouldReturnOrdersForVehicle() {
            // Arrange
            String licensePlate = "ABC-1234";
            Vehicle vehicle = Vehicle.builder().licensePlate(licensePlate).build();
            when(vehicleUseCase.getVehicleByLicensePlate(licensePlate)).thenReturn(vehicle);
            when(serviceOrderGateway.findByVehicleAndFinishedAtIsNull(vehicle))
                    .thenReturn(List.of(ServiceOrder.builder().build()));

            // Act
            List<ServiceOrder> result = serviceOrderUseCase.findByVehicleInfo(licensePlate);

            // Assert
            assertFalse(result.isEmpty());
            verify(serviceOrderGateway).findByVehicleAndFinishedAtIsNull(vehicle);
        }
    }

    @Nested
    class DeleteOrderLogically {
        @Test
        void shouldDeleteOrderLogically() {
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder().id(id).build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));

            // Act & Assert
            assertThrows(com.fiap.soat12.os.cleanarch.exception.NotFoundException.class, () -> {
                serviceOrderUseCase.deleteOrderLogically(id);
            });
        }
    }

    @Nested
    class Diagnose {
        @Test
        void shouldDiagnoseOrder() {
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder().id(id).status(com.fiap.soat12.os.cleanarch.util.Status.OPENED)
                    .build();
            Employee employee = Employee.builder().id(1L).build();

            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(employeeUseCase.getByEmployeeFunction(anyString())).thenReturn(List.of(employee));
            when(serviceOrderGateway.countByEmployeeAndStatusIn(any(), any())).thenReturn(0L);
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            ServiceOrder result = serviceOrderUseCase.diagnose(id, null);

            // Assert
            assertNotNull(result);
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.IN_DIAGNOSIS, result.getStatus());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class WaitForApproval {
        @Test
        void shouldWaitForApproval() throws Exception {
            // Arrange
            Long id = 1L;
            Customer customer = Customer.builder().name("Test").email("test@test.com").build();
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(com.fiap.soat12.os.cleanarch.util.Status.IN_DIAGNOSIS)
                    .customer(customer)
                    .build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            ServiceOrder result = serviceOrderUseCase.waitForApproval(id);

            // Assert
            assertNotNull(result);
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.WAITING_FOR_APPROVAL, result.getStatus());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class Approve {
        @Test
        void shouldApproveOrder(){
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(com.fiap.soat12.os.cleanarch.util.Status.WAITING_FOR_APPROVAL)
                    .employee(Employee.builder().build())
                    .build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            ServiceOrder result = serviceOrderUseCase.approve(id, null);

            // Assert
            assertNotNull(result);
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.APPROVED, result.getStatus());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class Reject {
        @Test
        void shouldRejectOrder(){
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(com.fiap.soat12.os.cleanarch.util.Status.WAITING_FOR_APPROVAL)
                    .build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            ServiceOrder result = serviceOrderUseCase.reject(id, "reason");

            // Assert
            assertNotNull(result);
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.REJECTED, result.getStatus());
            assertEquals("reason", result.getNotes());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class StartOrderExecution {
        @Test
        void shouldStartOrderExecution() {
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(com.fiap.soat12.os.cleanarch.util.Status.APPROVED)
                    .build();

            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);
            when(employeeUseCase.getByEmployeeFunction(anyString())).thenReturn(List.of(Employee.builder().build()));
            when(serviceOrderGateway.countByEmployeeAndStatusIn(any(), any())).thenReturn(0L);

            // Act
            ServiceOrder result = serviceOrderUseCase.startOrderExecution(id);

            // Assert
            assertNotNull(result);
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.IN_EXECUTION, result.getStatus());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class Finish {
        @Test
        void shouldFinishOrder() throws MessagingException {
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(com.fiap.soat12.os.cleanarch.util.Status.IN_EXECUTION)
                    .customer(Customer.builder().name("Test").email("test@test.com").build())
                    .vehicle(Vehicle.builder().build())
                    .build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            ServiceOrder result = serviceOrderUseCase.finish(id);

            // Assert
            assertNotNull(result);
            assertEquals(Status.WAITING_PAYMENT, result.getStatus());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class Deliver {
        @Test
        void shouldDeliverOrder(){
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(Status.PAYMENT_APPROVAL)
                    .build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            ServiceOrder result = serviceOrderUseCase.deliver(id);

            // Assert
            assertNotNull(result);
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.DELIVERED, result.getStatus());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class UpdateOrder {
        @Test
        void shouldUpdateOrderSuccessfully() {
            // Arrange
            Long id = 1L;
            ServiceOrderRequestDTO requestDTO = new ServiceOrderRequestDTO();
            requestDTO.setNotes("Updated notes");

            com.fiap.soat12.os.dto.serviceorder.ServiceOrderRequestDTO.VehicleServiceItemDTO srvDto = new com.fiap.soat12.os.dto.serviceorder.ServiceOrderRequestDTO.VehicleServiceItemDTO();
            srvDto.setServiceId(10L);
            requestDTO.setServices(List.of(srvDto));

            com.fiap.soat12.os.cleanarch.domain.model.VehicleService vehicleService = com.fiap.soat12.os.cleanarch.domain.model.VehicleService
                    .builder().id(10L).value(new java.math.BigDecimal("100.0"))
                    .build();
            ServiceOrder existingOrder = ServiceOrder.builder()
                    .id(id)
                    .services(new java.util.HashSet<>())
                    .build();

            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(existingOrder));
            when(vehicleServiceUseCase.getById(10L)).thenReturn(vehicleService);
            when(serviceOrderGateway.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArguments()[0]);

            // Act
            ServiceOrder result = serviceOrderUseCase.updateOrder(id, requestDTO);

            // Assert
            assertNotNull(result);
            assertEquals(Status.WAITING_FOR_APPROVAL, result.getStatus());
            assertEquals("Updated notes", result.getNotes());
            assertFalse(result.getServices().isEmpty());
            verify(serviceOrderGateway).save(existingOrder);
        }

        @Test
        void shouldThrowExceptionWhenOrderNotFoundForUpdate() {
            // Arrange
            Long id = 1L;
            ServiceOrderRequestDTO requestDTO = new ServiceOrderRequestDTO();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(NotFoundException.class, () -> serviceOrderUseCase.updateOrder(id, requestDTO));
            verify(serviceOrderGateway, never()).save(any());
        }
    }

    @Nested
    class CalculateAverageExecutionTime {
        @Test
        void shouldReturnZeroWhenNoOrdersFound() {
            // Arrange
            when(serviceOrderGateway.findAllWithFilters(any(), any(), any())).thenReturn(List.of());

            // Act
            java.time.Duration result = serviceOrderUseCase.calculateAverageExecutionTime(new java.util.Date(),
                    new java.util.Date(), List.of(1L));

            // Assert
            assertEquals(java.time.Duration.ZERO, result);
        }

        @Test
        void shouldCalculateAverageCorrectly() {
            // Arrange
            long startMillis = System.currentTimeMillis();
            java.util.Date startDate1 = new java.util.Date(startMillis);
            java.util.Date endDate1 = new java.util.Date(startMillis + 10000); // 10 seconds

            java.util.Date startDate2 = new java.util.Date(startMillis);
            java.util.Date endDate2 = new java.util.Date(startMillis + 20000); // 20 seconds

            ServiceOrder order1 = ServiceOrder.builder().createdAt(startDate1).finishedAt(endDate1).build();
            ServiceOrder order2 = ServiceOrder.builder().createdAt(startDate2).finishedAt(endDate2).build();

            when(serviceOrderGateway.findAllWithFilters(any(), any(), any())).thenReturn(List.of(order1, order2));

            // Act
            java.time.Duration result = serviceOrderUseCase.calculateAverageExecutionTime(startDate1, endDate2,
                    List.of(1L));

            // Assert
            assertEquals(15000, result.toMillis()); // Average of 10s and 20s is 15s
        }
    }

    @Nested
    class Approval {
        @Test
        void shouldApproveWhenTrue(){
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(com.fiap.soat12.os.cleanarch.util.Status.WAITING_FOR_APPROVAL)
                    .build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            serviceOrderUseCase.approval(id, true);

            // Assert
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.APPROVED, order.getStatus());
            verify(serviceOrderGateway).save(order);
        }

        @Test
        void shouldRejectWhenFalse() throws Exception {
            // Arrange
            Long id = 1L;
            ServiceOrder order = ServiceOrder.builder()
                    .id(id)
                    .status(com.fiap.soat12.os.cleanarch.util.Status.WAITING_FOR_APPROVAL)
                    .build();
            when(serviceOrderGateway.findById(id)).thenReturn(Optional.of(order));
            when(serviceOrderGateway.save(order)).thenReturn(order);

            // Act
            serviceOrderUseCase.approval(id, false);

            // Assert
            assertEquals(com.fiap.soat12.os.cleanarch.util.Status.REJECTED, order.getStatus());
            verify(serviceOrderGateway).save(order);
        }
    }

    @Nested
    class UpdateStatusOs {
        @Test
        void shouldCallRepositoryUpdateStatus() {
            // Arrange
            com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto.OsUpdateDto dto = new com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto.OsUpdateDto();
            dto.setOsId(1L);
            dto.setNewStatus(com.fiap.soat12.os.cleanarch.util.Status.APPROVED);

            // Act
            serviceOrderUseCase.updateStatusOs(dto);

            // Assert
            verify(serviceOrderJpaRepository).updateStatusById(1L, com.fiap.soat12.os.cleanarch.util.Status.APPROVED);
        }
    }
}
