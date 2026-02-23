package com.fiap.soat12.os.cleanarch.domain.useCases;

import com.fiap.soat12.os.cleanarch.domain.model.*;
import com.fiap.soat12.os.cleanarch.exception.InvalidTransitionException;
import com.fiap.soat12.os.cleanarch.exception.NotFoundException;
import com.fiap.soat12.os.cleanarch.gateway.ServiceOrderGateway;
import com.fiap.soat12.os.cleanarch.infrastructure.messaging.publisher.SqsEventPublisher;
import com.fiap.soat12.os.cleanarch.infrastructure.persistence.repository.jpa.ServiceOrderJpaRepository;
import com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto.OsUpdateDto;
import com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto.StockItemsDto;
import com.fiap.soat12.os.cleanarch.util.Status;
import com.fiap.soat12.os.dto.serviceorder.CheckoutBillingRequestDTO;
import com.fiap.soat12.os.dto.serviceorder.ServiceOrderRequestDTO;
import com.fiap.soat12.os.service.MailClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class ServiceOrderUseCase {

    protected static final String FUNCTION_MECHANIC_DESCRIPTION = "Mecânico";
    protected static final String DEFAULT_REJECT_REASON = "Recusada através de notificação externa";

    private final ServiceOrderGateway serviceOrderGateway;
    private final EmployeeUseCase employeeUseCase;
    private final CustomerUseCase customerUseCase;
    private final NotificationUseCase notificationUseCase;
    private final VehicleUseCase vehicleUseCase;
    private final VehicleServiceUseCase vehicleServiceUseCase;
    private final MailClient mailClient;
    private final MeterRegistry meterRegistry;
    private final SqsEventPublisher sqsEventPublisher;

    private final ServiceOrderJpaRepository serviceOrderJpaRepository;

    @Value("${app.url.billing}")
    private String url_billing;

    public ServiceOrder createServiceOrder(ServiceOrderRequestDTO requestDTO) {
        ServiceOrder serviceOrder = new ServiceOrder();

        Customer customer = customerUseCase.getCustomerById(requestDTO.getCustomerId());
        Vehicle vehicle = vehicleUseCase.getVehicleById(requestDTO.getVehicleId());

        Employee employee = nonNull(requestDTO.getEmployeeId())
                ? employeeUseCase.getEmployeeById(requestDTO.getEmployeeId())
                : this.findMostAvailableEmployee();

        serviceOrder.setCustomer(customer);
        serviceOrder.setVehicle(vehicle);
        serviceOrder.setEmployee(employee);

        // 1. Mapeia para o domínio ANTES de salvar (para persistir no banco)
        mapServicesDetail(requestDTO, serviceOrder);
        mapStockItemsToDomain(requestDTO, serviceOrder);

        serviceOrder.setNotes(requestDTO.getNotes());
        serviceOrder.setStatus(Status.OPENED);
        serviceOrder.setTotalValue(serviceOrder.calculateTotalValue(serviceOrder.getServices()));

        // 2. Salva no banco de dados com tudo preenchido
        ServiceOrder savedOrder = serviceOrderGateway.save(serviceOrder);

        // 3. Dispara a notificação SQS (agora extraída para um método limpo)
        publishSqsStockRemoval(savedOrder);

        notificationUseCase.notifyMechanicAssignedToOS(savedOrder, employee);
        meterRegistry.counter("techchallenge.orders.created", "type", "standard").increment();
        return savedOrder;
    }

    private void mapStockItemsToDomain(ServiceOrderRequestDTO request, ServiceOrder order) {
        if (request.getStockItems() == null) return;

        Set<StockItem> items = request.getStockItems().stream()
                .map(dto -> StockItem.builder()
                        .stockId(dto.getStockId())
                        .requiredQuantity(dto.getRequiredQuantity())
                        .unitPrice(BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toSet());

        order.setStockItems(items);
    }

    // Método 2: Exclusivo para disparar a baixa no estoque via SQS
    private void publishSqsStockRemoval(ServiceOrder savedOrder) {
        if (savedOrder.getStockItems() == null || savedOrder.getStockItems().isEmpty()) {
            return; // Se não tem peça, não manda SQS
        }

        List<StockItemsDto.StockUpdateDto> updateItems = savedOrder.getStockItems().stream()
                .map(stock -> StockItemsDto.StockUpdateDto.builder()
                        .id(stock.getStockId())
                        .quantity(stock.getRequiredQuantity())
                        .build())
                .toList();

        StockItemsDto eventDto = new StockItemsDto();
        eventDto.setOsId(savedOrder.getId());
        eventDto.setItems(updateItems);

        sqsEventPublisher.publishRemoveStock(eventDto);
    }

    public ServiceOrder findById(Long id) {
        return serviceOrderGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Ordem de Serviço não encontrada: " + id));
    }

    public List<ServiceOrder> findAllOrders() {
        return serviceOrderGateway.findAll();
    }

    public List<ServiceOrder> findAllOrdersFiltered() {
        List<Status> activeStatuses = Arrays.asList(
                Status.OPENED,
                Status.IN_DIAGNOSIS,
                Status.WAITING_FOR_APPROVAL,
                Status.APPROVED,
                Status.IN_EXECUTION);
        return serviceOrderGateway.findAllFilteredAndSorted(activeStatuses);
    }

    public List<ServiceOrder> findByCustomerInfo(String document) {
        Customer customer = customerUseCase.getCustomerByCpf(document);

        return serviceOrderGateway.findByCustomerAndFinishedAtIsNull(customer);
    }

    public List<ServiceOrder> findByVehicleInfo(String licensePlate) {
        Vehicle vehicle = vehicleUseCase.getVehicleByLicensePlate(licensePlate);
        return serviceOrderGateway.findByVehicleAndFinishedAtIsNull(vehicle);
    }

    public ServiceOrder updateOrder(Long id, ServiceOrderRequestDTO requestDTO) {
        return serviceOrderGateway.findById(id)
                .map(existingOrder -> {
                    BeanUtils.copyProperties(requestDTO, existingOrder);
                    existingOrder.setStatus(Status.WAITING_FOR_APPROVAL);
                    existingOrder.setUpdatedAt(new Date());

                    existingOrder.getServices().clear();
                    if (requestDTO.getServices() != null) {
                        requestDTO.getServices().forEach(serviceDto -> {
                            VehicleService service = vehicleServiceUseCase.getById(serviceDto.getServiceId());
                            existingOrder.getServices().add(service);
                        });
                    }

                    existingOrder.setTotalValue(existingOrder.calculateTotalValue(existingOrder.getServices()));

                    return serviceOrderGateway.save(existingOrder);
                }).orElseThrow(() -> new NotFoundException("Ordem de serviço não encontrado"));
    }

    public void deleteOrderLogically(Long id) {
        serviceOrderGateway.findById(id)
                .map(order -> {
                    order.setStatus(Status.CANCELED);
                    serviceOrderGateway.save(order);
                    return null;
                }).orElseThrow(() -> new NotFoundException("Veículo não encontrado"));
    }

    public ServiceOrder diagnose(Long id, Long employeeId) throws InvalidTransitionException {
        Employee employee = null;
        ServiceOrder order = findById(id);

        if (employeeId != null) {
            employee = employeeUseCase.getEmployeeById(employeeId);
            order.setEmployee(employee);
        } else {
            Employee availableEmployee = this.findMostAvailableEmployee();
            order.setEmployee(availableEmployee);
        }

        order.getStatus().diagnose(order);
        return serviceOrderGateway.save(order);
    }

    public ServiceOrder waitForApproval(Long id) throws InvalidTransitionException, MessagingException {
        final String BASE_URL = "http://localhost:8080/api/service-orders/";
        ServiceOrder order = findById(id);
        order.getStatus().waitForApproval(order);
        ServiceOrder serviceOrder = serviceOrderGateway.save(order);

        Map<String, Object> variables = new HashMap<>();
        variables.put("totalValue", order.getTotalValue());
        variables.put("userName", order.getCustomer().getName());
        variables.put("services", serviceOrder.getServices());
        variables.put("directApproveLink", BASE_URL + id + "/webhook/approval?approval=true");
        variables.put("directRejectLink", BASE_URL + id + "/webhook/approval?approval=false");

        String subject = order.getCustomer().getName()
                + " Seu Orçamento de Serviços está Pronto! (Aprovação Necessária)";

//        mailClient.sendMail(order.getCustomer().getEmail(), subject, "mailTemplateServices", variables);

        return serviceOrder;
    }

    public ServiceOrder approve(Long id, Long employeeId) throws InvalidTransitionException {
        Employee employee = null;
        ServiceOrder order = findById(id);

        if (employeeId != null) {
            employee = employeeUseCase.getEmployeeById(employeeId);
            order.setEmployee(employee);
        }

        notificationUseCase.notifyMechanicOSApproved(order, order.getEmployee());

        order.getStatus().approve(order);
        return serviceOrderGateway.save(order);
    }

    public ServiceOrder reject(Long id, String reason) throws InvalidTransitionException {
        ServiceOrder order = findById(id);
        order.getStatus().reject(order);
        order.setNotes(reason);
        return serviceOrderGateway.save(order);
    }

    public ServiceOrder startOrderExecution(Long serviceOrderId) {
        ServiceOrder order = findById(serviceOrderId);

        order.getStatus().execute(order);
        Employee employee = this.findMostAvailableEmployee();
        order.setEmployee(employee);
        notificationUseCase.notifyMechanicAssignedToOS(order, employee);

        return serviceOrderGateway.save(order);
    }

    public ServiceOrder finish(Long id) throws InvalidTransitionException, MessagingException {
        ServiceOrder order = findById(id);
        order.getStatus().waitingPayment(order);
        notificationUseCase.notifyAttendantsOSCompleted(order);
        ServiceOrder serviceOrder = serviceOrderGateway.save(order);

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", order.getId());
        variables.put("totalValue", order.getTotalValue());
        variables.put("userName", order.getCustomer().getName());
        variables.put("services", serviceOrder.getServices());
        variables.put("vehicle", serviceOrder.getVehicle());

        String subject = order.getCustomer().getName() + " Seus serviços solicitados foram finalizados";

//        mailClient.sendMail(order.getCustomer().getEmail(), subject, "mailTemplateServiceFinish", variables);

        CheckoutBillingRequestDTO checkoutDTO = new CheckoutBillingRequestDTO();
        checkoutDTO.setServiceOrderId(order.getId());
        checkoutDTO.setTotalAmount(new BigDecimal("10.00"));

        CheckoutBillingRequestDTO.CustomerDTO customer = new CheckoutBillingRequestDTO.CustomerDTO(
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getCustomer().getCpf(),
                order.getCustomer().getEmail()
        );
        checkoutDTO.setCustomer(customer);

        checkoutDTO.setResponseUrls(new CheckoutBillingRequestDTO.ResponseUrlsDTO(
                "https://trinh-dispensible-instructively.ngrok-free.dev/api/v1/webhooks/mercadopago",
                "https://trinh-dispensible-instructively.ngrok-free.dev/checkout/failure",
                "https://trinh-dispensible-instructively.ngrok-free.dev/checkout/pending"
        ));

        List<CheckoutBillingRequestDTO.ItemDTO> itensFalsos = List.of(
                new CheckoutBillingRequestDTO.ItemDTO(
                        999L,                      // ID falso
                        "Troca de Óleo Falsa",     // Nome falso
                        1,                         // Quantidade falsa
                        new BigDecimal("10.00")    // Preço falso
                )

        );
        checkoutDTO.setItems(itensFalsos);
//        List<CheckoutBillingRequestDTO.ItemDTO> items = order.getServices().stream()
//                .map(s -> new CheckoutBillingRequestDTO.ItemDTO(s.getId(), s.getName(), 1, s.getPrice()))
//                .toList();
//        checkoutDTO.setItems(items);

        try {
            // 1. Logando o Body que será enviado
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            // O writerWithDefaultPrettyPrinter() deixa o JSON formatado com quebras de linha e recuos
            String jsonEnviado = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(checkoutDTO);

            System.out.println("==================================================");
            System.out.println("BODY ENVIADO PARA A API DO MERCADO PAGO:");
            System.out.println(jsonEnviado);
            System.out.println("==================================================");

            // 2. Fazendo a chamada
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url_billing, checkoutDTO, String.class);

            System.out.println("Resposta do Mercado Pago (Status " + response.getStatusCode() + "): " + response.getBody());

        } catch (Exception e) {
            System.err.println("Erro ao chamar a API de Billing: " + e.getMessage());
            e.printStackTrace(); // Isso vai te mostrar a stack trace inteira caso dê erro 400 ou 500 na chamada
        }

        if (order.getCreatedAt() != null) {
            long durationSeconds = (new Date().getTime() - order.getCreatedAt().getTime()) / 1000;

            Timer.builder("techchallenge.orders.execution_time")
                    .description("Tempo total de execução da ordem de serviço")
                    .tag("status", "finished")
                    .register(meterRegistry)
                    .record(Duration.ofSeconds(durationSeconds));
        }

        return serviceOrder;
    }

    public ServiceOrder deliver(Long id) throws InvalidTransitionException {
        ServiceOrder order = findById(id);
        order.getStatus().deliver(order);
        return serviceOrderGateway.save(order);
    }

    public Duration calculateAverageExecutionTime(Date startDate, Date endDate, List<Long> serviceIds) {
        List<ServiceOrder> finishedOrders = serviceOrderGateway.findAllWithFilters(startDate, endDate, serviceIds);

        if (finishedOrders.isEmpty()) {
            return Duration.ZERO;
        }

        long totalMillis = finishedOrders.stream()
                .mapToLong(order -> {
                    Date startedAt = order.getCreatedAt();
                    Date finishedAt = order.getFinishedAt();
                    return finishedAt.getTime() - startedAt.getTime();
                })
                .sum();

        long avgMillis = totalMillis / finishedOrders.size();
        return Duration.ofMillis(avgMillis);
    }

    public void approval(Long id, Boolean approval) {
        if (approval) {
            this.approve(id, null);
        } else {
            this.reject(id, DEFAULT_REJECT_REASON);
        }
    }

    private void mapServicesDetail(ServiceOrderRequestDTO request, ServiceOrder order) {
        if (request.getServices() != null) {
            request.getServices()
                    .forEach(dto -> {
                        VehicleService vehicleService = vehicleServiceUseCase.getById(dto.getServiceId());
                        order.getServices().add(vehicleService);
                    });
        }
    }

    protected Employee findMostAvailableEmployee() {
        List<Status> activeStatuses = Status.getStatusesForPendingOrders();

        List<Employee> activeEmployees = employeeUseCase.getByEmployeeFunction(FUNCTION_MECHANIC_DESCRIPTION);

        if (activeEmployees.isEmpty()) {
            throw new NotFoundException("Nenhum mecânico disponível");
        }

        return activeEmployees.stream()
                .min(Comparator
                        .comparingLong((Employee employee) -> serviceOrderGateway.countByEmployeeAndStatusIn(employee,
                                activeStatuses))
                        .thenComparing(employee -> {
                            Date oldestOrderDate = findOldestOrderDateForEmployee(employee, activeStatuses);
                            return oldestOrderDate == null ? Long.MIN_VALUE : oldestOrderDate.getTime();
                        }))
                .orElseThrow(() -> new NotFoundException("Nenhum mecânico disponível"));
    }

    protected Date findOldestOrderDateForEmployee(Employee employee, List<Status> activeStatuses) {
        List<ServiceOrder> activeOrders = serviceOrderGateway.findByEmployeeAndStatusIn(employee, activeStatuses);
        return activeOrders.stream()
                .map(ServiceOrder::getCreatedAt)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public void updateStatusOs(OsUpdateDto dto) {
        serviceOrderJpaRepository.updateStatusById(dto.getOsId(), dto.getNewStatus());
    }
}
