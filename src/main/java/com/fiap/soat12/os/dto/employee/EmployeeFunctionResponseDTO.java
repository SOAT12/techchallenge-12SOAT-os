package com.fiap.soat12.os.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFunctionResponseDTO {
    private Long id;
    private String description;
    private Boolean active;
}
