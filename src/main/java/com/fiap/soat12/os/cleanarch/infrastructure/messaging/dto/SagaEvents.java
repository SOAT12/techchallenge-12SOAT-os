package com.fiap.soat12.os.cleanarch.infrastructure.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class SagaEvents {

    // --- EVENTOS PARA O STOCK ---
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StockOperationEvent {
        private Long serviceOrderId;
        private List<StockItem> items;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StockItem {
        private Long stockId; // ou externalStockId, conforme nomeado no Stock Service
        private Integer quantity;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class StockStatusUpdateEvent {
        private Long serviceOrderId;
        private String status; // Ex: "SUCCESS" ou "FAILED"
        private String message;
    }

    // --- EVENTOS PARA O BILLING ---
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BillingCreateRequestEvent {
        private Long orderId;
        private Double amount;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PaymentResultEvent {
        private Long orderId;
        private String reason; // Preenchido no caso do Failed
    }
}