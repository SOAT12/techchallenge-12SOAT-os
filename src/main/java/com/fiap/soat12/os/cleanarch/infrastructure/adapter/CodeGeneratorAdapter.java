package com.fiap.soat12.os.cleanarch.infrastructure.adapter;

import com.fiap.soat12.os.cleanarch.domain.port.CodeGeneratorPort;
import com.fiap.soat12.os.cleanarch.util.CodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class CodeGeneratorAdapter implements CodeGeneratorPort {

    @Override
    public String generateCode() {
        return CodeGenerator.generateCode();
    }
}