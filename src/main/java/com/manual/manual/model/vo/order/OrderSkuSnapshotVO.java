package com.manual.manual.model.vo.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSkuSnapshotVO {

    private Long skuId;

    private Long productId;

    private String productName;

    private String productCover;

    private String skuCover;

    private String skuName;

    private String specText;

    private BigDecimal price;

    private Integer stock;

    private Integer lockedStock;

    private Integer skuStatus;
}
