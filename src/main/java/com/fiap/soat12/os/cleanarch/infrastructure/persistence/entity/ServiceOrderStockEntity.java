package com.fiap.soat12.os.cleanarch.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long externalStockId;

    private Integer quantity;
}