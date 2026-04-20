package com.manual.manual.model.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long artisanId;

    private String productName;

    private String productSubtitle;

    private String productCover;

    private String craftType;

    private String originPlace;

    private Integer handmadeCycleDays;

    private Integer supportCustom;

    private Integer soldQuantity;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String artisanName;

    private String shopName;

    private String categoryName;
}
