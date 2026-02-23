package com.fiap.soat12.os.cleanarch.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "service_order_stock")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOrderStockEntity {

    @EmbeddedId
    private ServiceOrderStockIdEntity id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceOrderId")
    @JoinColumn(name = "service_order_id")
    private ServiceOrderEntity serviceOrder;

    @Column(name = "stock_id", insertable = false, updatable = false)
    private UUID externalStockId;

    @Column(name = "quantity", nullable = false)
    private Integer requiredQuantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}