package com.fiap.soat12.os.cleanarch.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOrderStockIdEntity implements Serializable {

    @Column(name = "service_order_id")
    private Long serviceOrderId;

    @Column(name = "stock_id")
    private Long stockId;
}