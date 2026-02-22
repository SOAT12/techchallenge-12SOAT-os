package com.fiap.soat12.os.cleanarch.domain.model;

import com.fiap.soat12.os.cleanarch.util.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrder {

    private Long id;

    private Date finishedAt;

    private Status status;

    private BigDecimal totalValue;

    private String notes;

    private Customer customer;

    private Vehicle vehicle;

    private Employee employee;

    private Set<VehicleService> services = new HashSet<>();;

    private Date createdAt;

    private Date updatedAt;

    private Map<Long, Integer> consumedStocks;

    public BigDecimal calculateTotalValue(Set<VehicleService> services) {
        BigDecimal totalValue = BigDecimal.ZERO;
        if (services == null) {
            return totalValue;
        }
        totalValue = totalValue.add(services.stream()
                .map(VehicleService::getValue)
                .reduce(totalValue, BigDecimal::add));

        return totalValue;
    }

}
