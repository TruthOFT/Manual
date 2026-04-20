package com.manual.manual.model.dto.artisancenter;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArtisanCenterProductSkuRequest {

    private String skuName;

    private String skuCover;

    private String specText;

    private String materialType;

    private BigDecimal weight;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer status;
}
