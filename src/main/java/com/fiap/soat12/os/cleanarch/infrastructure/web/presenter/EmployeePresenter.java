package com.fiap.soat12.os.cleanarch.infrastructure.web.presenter;

import com.fiap.soat12.os.cleanarch.domain.model.Employee;
import com.fiap.soat12.os.cleanarch.domain.model.EmployeeFunction;
import com.fiap.soat12.os.dto.employee.EmployeeRequestDTO;
import com.fiap.soat12.os.dto.employee.EmployeeResponseDTO;

public class EmployeePresenter {

    private final EmployeeFunctionPresenter employeeFunctionPresenter;

    public EmployeePresenter(EmployeeFunctionPresenter employeeFunctionPresenter) {
        this.employeeFunctionPresenter = employeeFunctionPresenter;
    }

    public EmployeeResponseDTO toEmployeeResponseDTO(Employee employee) {
        if (employee == null)
            return null;
        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .cpf(employee.getCpf())
                .name(employee.getName())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .active(employee.getActive())
                .created_at(employee.getCreatedAt())
                .updated_at(employee.getUpdatedAt())
                .employeeFunction(
                        employeeFunctionPresenter.toEmployeeFunctionResponseDTO(employee.getEmployeeFunction()))
                .build();
    }

    public Employee toEmployee(EmployeeRequestDTO dto, EmployeeFunction employeeFunction) {
        if (dto == null)
            return null;
        return Employee.builder()
                .cpf(dto.getCpf())
                .name(dto.getName())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .active(dto.getActive())
                .employeeFunction(employeeFunction)
                .build();
    }
}
