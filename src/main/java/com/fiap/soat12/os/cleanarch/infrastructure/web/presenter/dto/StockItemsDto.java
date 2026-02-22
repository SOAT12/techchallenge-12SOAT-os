package com.fiap.soat12.os.cleanarch.infrastructure.web.presenter.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemsDto {

    private Long osId;
    private List<StockUpdateDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StockUpdateDto {
        private UUID id;
        private Integer quantity;
    }

}