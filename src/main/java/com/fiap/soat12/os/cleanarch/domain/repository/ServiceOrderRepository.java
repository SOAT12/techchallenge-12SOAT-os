package com.fiap.soat12.os.cleanarch.domain.repository;

import com.fiap.soat12.os.cleanarch.domain.model.Customer;
import com.fiap.soat12.os.cleanarch.domain.model.Employee;
import com.fiap.soat12.os.cleanarch.domain.model.ServiceOrder;
import com.fiap.soat12.os.cleanarch.domain.model.Vehicle;
import com.fiap.soat12.os.cleanarch.util.Status;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ServiceOrderRepository {

    List<ServiceOrder> findAll();

    List<ServiceOrder> findAllFilteredAndSorted(List<Status> statuses);

    List<ServiceOrder> findAllWithFilters(Date startDate, Date endDate, List<Long> serviceIds);

    Optional<ServiceOrder> findById(Long id);

    Long countByEmployeeAndStatusIn(Employee employee, List<Status> statusList);

    List<ServiceOrder> findByEmployeeAndStatusIn(Employee employee, List<Status> statusList);

    List<ServiceOrder> findByCustomerAndFinishedAtIsNull(Customer customer);

    List<ServiceOrder> findByVehicleAndFinishedAtIsNull(Vehicle vehicle);

    ServiceOrder save(ServiceOrder serviceOrder);

}
