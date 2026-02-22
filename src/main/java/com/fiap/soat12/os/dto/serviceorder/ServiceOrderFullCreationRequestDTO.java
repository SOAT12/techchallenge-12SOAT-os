package com.fiap.soat12.os.dto.serviceorder;

import com.fiap.soat12.os.dto.customer.CustomerRequestDTO;
import com.fiap.soat12.os.dto.vehicle.VehicleRequestDTO;
import com.fiap.soat12.os.dto.vehicleservice.VehicleServiceRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderFullCreationRequestDTO {

    private CustomerRequestDTO customer;

    private VehicleRequestDTO vehicle;

    private List<VehicleServiceRequestDTO> services;

    private Long employeeId;

    private String notes;

}
