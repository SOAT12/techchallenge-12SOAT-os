package com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOrderStockItemDTO {
    private Long stockId; // Corresponde ao externalStockId
    private Integer quantity;
}