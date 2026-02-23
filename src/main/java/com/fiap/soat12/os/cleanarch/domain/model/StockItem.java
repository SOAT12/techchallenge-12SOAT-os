package com.fiap.soat12.os.cleanarch.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockItem {
    private UUID stockId; // ID externo do microserviço
    private Integer requiredQuantity;
    private BigDecimal unitPrice;
}