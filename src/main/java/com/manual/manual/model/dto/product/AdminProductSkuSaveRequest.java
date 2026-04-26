package com.manual.manual.model.dto.product;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AdminProductSkuSaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String skuCode;

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
