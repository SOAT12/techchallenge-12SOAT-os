package com.fiap.soat12.os.cleanarch.infrastructure.persistence.repository.jpa;

import com.fiap.soat12.os.cleanarch.infrastructure.persistence.entity.EmployeeFunctionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeFunctionJpaRepository extends JpaRepository<EmployeeFunctionJpaEntity, Long> {
}
