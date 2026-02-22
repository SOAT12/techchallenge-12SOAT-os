package com.fiap.soat12.os.dto.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFunctionRequestDTO {
    @NotBlank(message = "A descrição da função não pode estar em branco.")
    @Size(max = 100, message = "A descrição da função não pode exceder 100 caracteres.")
    private String description;
}
