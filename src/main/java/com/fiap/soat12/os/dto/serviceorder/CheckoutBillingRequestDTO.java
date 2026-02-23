package com.fiap.soat12.os.dto.serviceorder;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutBillingRequestDTO {

    @NotNull
    private Long serviceOrderId;

    @NotNull
    private CustomerDTO customer;

    private List<ItemDTO> items;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private ResponseUrlsDTO responseUrls;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDTO {
        @NotNull
        private Long customerId;

        @NotNull
        private String customerName;

        private String document;

        @NotNull
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDTO {
        @NotNull
        private Long id;

        @NotNull
        private String itemName;

        @NotNull
        private Integer quantity;

        @NotNull
        private BigDecimal price;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseUrlsDTO {
        private String successUrl;
        private String failureUrl;
        private String pendingUrl;
    }
}