package com.fiap.soat12.os.service;

import com.fiap.soat12.os.cleanarch.util.DateUtils;
import com.fiap.soat12.os.dto.ChangePasswordRequestDTO;
import com.fiap.soat12.os.dto.ForgotPasswordRequestDTO;
import com.fiap.soat12.os.dto.LoginRequestDTO;
import com.fiap.soat12.os.dto.employee.EmployeeRequestDTO;
import com.fiap.soat12.os.dto.employee.EmployeeResponseDTO;
import com.fiap.soat12.os.entity.Employee;
import com.fiap.soat12.os.entity.EmployeeFunction;
import com.fiap.soat12.os.mapper.EmployeeMapper;
import com.fiap.soat12.os.repository.EmployeeFunctionRepository;
import com.fiap.soat12.os.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private static final String FALHA_IDENTIFICACAO_MSG = "FALHA NA IDENTIFICAÇÃO: ";

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeeFunctionRepository employeeFunctionRepository;

    private final MailClient mailClient;

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toEmployeeResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponseDTO> getAllActiveEmployees() {
        return employeeRepository.findAllByActiveTrue().stream()
                .map(employeeMapper::toEmployeeResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<EmployeeResponseDTO> getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toEmployeeResponseDTO);
    }

    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        EmployeeFunction function = employeeFunctionRepository.findById(requestDTO.getEmployeeFunctionId())
                .orElseThrow(() -> new IllegalArgumentException("Função não encontrada"));
        Employee employee = employeeMapper.toEmployee(requestDTO, function);
        employee.setCreatedAt(new Date());
        employee.setUpdatedAt(new Date());
        employee.setActive(true);
        employee.setUseTemporaryPassword(false);
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toEmployeeResponseDTO(saved);
    }

    @Transactional
    public Optional<EmployeeResponseDTO> updateEmployee(Long id, EmployeeRequestDTO requestDTO) {
        return employeeRepository.findById(id).map(existing -> {
            EmployeeFunction function = employeeFunctionRepository.findById(requestDTO.getEmployeeFunctionId())
                    .orElseThrow(() -> new IllegalArgumentException("Função não encontrada"));
            existing.setCpf(requestDTO.getCpf());
            existing.setName(requestDTO.getName());
            existing.setPhone(requestDTO.getPhone());
            existing.setEmail(requestDTO.getEmail());
            existing.setActive(requestDTO.getActive());
            existing.setEmployeeFunction(function);
            existing.setUpdatedAt(new Date());
            Employee updated = employeeRepository.save(existing);
            return employeeMapper.toEmployeeResponseDTO(updated);
        });
    }

    @Transactional
    public boolean inactivateEmployee(Long id) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setActive(false);
            employee.setUpdatedAt(new Date());
            employee.setUpdatedAt(new Date());

            employeeRepository.save(employee);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean activateEmployee(Long id) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setActive(true);
            employee.setUpdatedAt(new Date());
            employeeRepository.save(employee);
            return true;
        }).orElse(false);
    }


}
