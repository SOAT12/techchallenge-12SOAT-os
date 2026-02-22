package com.fiap.soat12.os.cleanarch.domain.repository;

import com.fiap.soat12.os.cleanarch.infrastructure.persistence.entity.EmployeeFunctionJpaEntity;

import java.util.List;
import java.util.Optional;

public interface EmployeeFunctionRepository {

    List<EmployeeFunctionJpaEntity> findAll();

    Optional<EmployeeFunctionJpaEntity> findById(Long id);

    EmployeeFunctionJpaEntity save(EmployeeFunctionJpaEntity employeeFunction);
}
