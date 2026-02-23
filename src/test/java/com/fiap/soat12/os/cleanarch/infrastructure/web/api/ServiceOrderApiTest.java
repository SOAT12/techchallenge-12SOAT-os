package com.fiap.soat12.os.cleanarch.infrastructure.web.api;

import com.fiap.soat12.os.cleanarch.infrastructure.web.controller.ServiceOrderController;
import com.fiap.soat12.os.dto.serviceorder.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceOrderApiTest {

    @Mock
    private ServiceOrderController serviceOrderController;

    @InjectMocks
    private ServiceOrderApi serviceOrderApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrder() {
        ServiceOrderRequestDTO request = new ServiceOrderRequestDTO();
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.createOrder(any())).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.createOrder(request);

        assertEquals(response, result);
        verify(serviceOrderController).createOrder(request);
    }

    @Test
    void shouldGetOrderById() {
        Long id = 1L;
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.findOrderById(id)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.getOrderById(id);

        assertEquals(response, result);
        verify(serviceOrderController).findOrderById(id);
    }

    @Test
    void shouldGetAllOrders() {
        List<ServiceOrderResponseDTO> response = List.of(new ServiceOrderResponseDTO());
        when(serviceOrderController.findAllOrders()).thenReturn(response);

        List<ServiceOrderResponseDTO> result = serviceOrderApi.getAllOrders();

        assertEquals(response, result);
        verify(serviceOrderController).findAllOrders();
    }

    @Test
    void shouldGetAllOrdersFiltered() {
        List<ServiceOrderResponseDTO> response = List.of(new ServiceOrderResponseDTO());
        when(serviceOrderController.findAllOrdersFiltered()).thenReturn(response);

        List<ServiceOrderResponseDTO> result = serviceOrderApi.getAllOrdersFiltered();

        assertEquals(response, result);
        verify(serviceOrderController).findAllOrdersFiltered();
    }

    @Test
    void shouldConsultOrderByDocument() {
        String document = "12345678901";
        List<ServiceOrderResponseDTO> response = List.of(new ServiceOrderResponseDTO());
        when(serviceOrderController.findByCustomerInfo(document)).thenReturn(response);

        List<ServiceOrderResponseDTO> result = serviceOrderApi.consultOrder(document, null);

        assertEquals(response, result);
        verify(serviceOrderController).findByCustomerInfo(document);
    }

    @Test
    void shouldConsultOrderByLicensePlate() {
        String licensePlate = "ABC1234";
        List<ServiceOrderResponseDTO> response = List.of(new ServiceOrderResponseDTO());
        when(serviceOrderController.findByVehicleInfo(licensePlate)).thenReturn(response);

        List<ServiceOrderResponseDTO> result = serviceOrderApi.consultOrder(null, licensePlate);

        assertEquals(response, result);
        verify(serviceOrderController).findByVehicleInfo(licensePlate);
    }

    @Test
    void shouldConsultOrderWithEmptyResultWhenBothParamsAreNull() {
        List<ServiceOrderResponseDTO> result = serviceOrderApi.consultOrder(null, null);

        assertEquals(0, result.size());
    }

    @Test
    void shouldDeleteOrder() {
        Long id = 1L;

        serviceOrderApi.deleteOrder(id);

        verify(serviceOrderController).deleteOrderLogically(id);
    }

    @Test
    void shouldUpdateOrder() {
        Long id = 1L;
        ServiceOrderRequestDTO request = new ServiceOrderRequestDTO();
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.updateServiceOrder(eq(id), any())).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.updateOrder(id, request);

        assertEquals(response, result);
        verify(serviceOrderController).updateServiceOrder(id, request);
    }

    @Test
    void shouldDiagnose() {
        Long id = 1L;
        Long employeeId = 2L;
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.diagnose(id, employeeId)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.diagnose(id, employeeId);

        assertEquals(response, result);
        verify(serviceOrderController).diagnose(id, employeeId);
    }

    @Test
    void shouldWaitForApproval() throws Exception {
        Long id = 1L;
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.waitForApproval(id)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.waitForApproval(id);

        assertEquals(response, result);
        verify(serviceOrderController).waitForApproval(id);
    }

    @Test
    void shouldApprove() {
        Long id = 1L;
        Long employeeId = 2L;
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.approve(id, employeeId)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.approve(id, employeeId);

        assertEquals(response, result);
        verify(serviceOrderController).approve(id, employeeId);
    }

    @Test
    void shouldReject() {
        Long id = 1L;
        String reason = "Too expensive";
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.reject(id, reason)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.reject(id, reason);

        assertEquals(response, result);
        verify(serviceOrderController).reject(id, reason);
    }

    @Test
    void shouldStartServiceOrderExecution() {
        Long id = 1L;
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.startOrderExecution(id)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.startServiceOrderExecution(id);

        assertEquals(response, result);
        verify(serviceOrderController).startOrderExecution(id);
    }

    @Test
    void shouldFinish() throws Exception {
        Long id = 1L;
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.finish(id)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.finish(id);

        assertEquals(response, result);
        verify(serviceOrderController).finish(id);
    }

    @Test
    void shouldDeliver() {
        Long id = 1L;
        ServiceOrderResponseDTO response = new ServiceOrderResponseDTO();
        when(serviceOrderController.deliver(id)).thenReturn(response);

        ServiceOrderResponseDTO result = serviceOrderApi.deliver(id);

        assertEquals(response, result);
        verify(serviceOrderController).deliver(id);
    }

    @Test
    void shouldGetAverageExecutionTime() {
        Date startDate = new Date();
        Date endDate = new Date();
        List<Long> serviceIds = List.of(1L, 2L);
        AverageExecutionTimeResponseDTO response = AverageExecutionTimeResponseDTO.builder()
                .averageExecutionTimeHours(1L)
                .averageExecutionTimeFormatted("1h")
                .build();
        when(serviceOrderController.calculateAverageExecutionTime(startDate, endDate, serviceIds)).thenReturn(response);

        AverageExecutionTimeResponseDTO result = serviceOrderApi.getAverageExecutionTime(startDate, endDate,
                serviceIds);

        assertEquals(response, result);
        verify(serviceOrderController).calculateAverageExecutionTime(startDate, endDate, serviceIds);
    }

    @Test
    void shouldGetServiceOrderStatus() {
        Long id = 1L;
        ServiceOrderStatusResponseDTO response = ServiceOrderStatusResponseDTO.builder().status("Test").build();
        when(serviceOrderController.getServiceOrderStatus(id)).thenReturn(response);

        ServiceOrderStatusResponseDTO result = serviceOrderApi.getServiceOrderStatus(id);

        assertEquals(response, result);
        verify(serviceOrderController).getServiceOrderStatus(id);
    }

    @Test
    void shouldApproval() {
        Long id = 1L;
        Boolean approval = true;

        serviceOrderApi.approval(id, approval);

        verify(serviceOrderController).approval(id, approval);
    }
}
