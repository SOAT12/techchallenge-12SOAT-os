package com.fiap.soat12.os.dto.serviceorder;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderRequestDTO {

    @NotNull
    private Long customerId;

    @NotNull
    private Long vehicleId;

    private Long employeeId;

    private String notes;

    private List<VehicleServiceItemDTO> services;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleServiceItemDTO {
        @NotNull
        private Long serviceId;
    }
}
