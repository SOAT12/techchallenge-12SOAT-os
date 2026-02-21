package com.fiap.soat12.os.cleanarch.domain.port;

import com.fiap.soat12.os.dto.LoginResponseDTO;
import com.fiap.soat12.os.cleanarch.domain.model.Employee;

import java.util.List;

public interface TokenServicePort {

    LoginResponseDTO generateToken(Employee employee, List<String> authorities);
}