package com.fiap.soat12.os.repository;

import com.fiap.soat12.os.entity.EmployeeFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeFunctionRepository extends JpaRepository<EmployeeFunction, Long> {
}
