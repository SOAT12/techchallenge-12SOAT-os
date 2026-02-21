package com.fiap.soat12.os.cleanarch.infrastructure.adapter;

import com.fiap.soat12.os.cleanarch.domain.port.TokenServicePort;
import com.fiap.soat12.os.cleanarch.util.JwtTokenUtil;
import com.fiap.soat12.os.cleanarch.util.UUIDGeneratorUtil;
import com.fiap.soat12.os.config.SessionToken;
import com.fiap.soat12.os.dto.LoginResponseDTO;
import com.fiap.soat12.os.cleanarch.domain.model.Employee;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAdapter implements TokenServicePort {

    private final JwtTokenUtil jwtTokenUtil;
    private final SessionToken sessionToken;

    @Override
    public LoginResponseDTO generateToken(Employee employee, List<String> authorities) {
        String uuid = UUIDGeneratorUtil.getInstance().next();

        Map<String, Object> claims = new HashedMap<>();
        claims.put("authorities", authorities.toArray(new String[0]));

        final String token = jwtTokenUtil.generateToken(claims, uuid);

        sessionToken.removeTransactionAndSession(employee.getCpf());
        sessionToken.addTransactionAndSession(employee.getCpf(), uuid);

        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(token);

        return loginResponse;
    }
}