package com.manual.manual.model.vo.home;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeProductVO {

    private Long id;

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
