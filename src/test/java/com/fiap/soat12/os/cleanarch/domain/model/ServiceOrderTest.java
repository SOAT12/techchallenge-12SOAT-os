package com.fiap.soat12.os.cleanarch.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceOrderTest {

    @Nested
    class CalculateTotalValue {

        @Test
        void shouldReturnZeroForNullSets() {
            ServiceOrder serviceOrder = new ServiceOrder();
            BigDecimal result = serviceOrder.calculateTotalValue(null);
            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        void shouldReturnZeroForEmptySets() {
            ServiceOrder serviceOrder = new ServiceOrder();
            BigDecimal result = serviceOrder.calculateTotalValue(new HashSet<>());
            assertEquals(BigDecimal.ZERO, result);
        }

        @Test
        void shouldCalculateValueForServicesOnly() {
            ServiceOrder serviceOrder = new ServiceOrder();
            Set<VehicleService> services = new HashSet<>();
            services.add(VehicleService.builder().value(BigDecimal.TEN).build());
            services.add(VehicleService.builder().value(BigDecimal.ONE).build());

            BigDecimal result = serviceOrder.calculateTotalValue(services);

            assertEquals(new BigDecimal("11"), result);
        }
    }
}
