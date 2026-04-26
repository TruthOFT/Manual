package com.manual.manual.model.vo.home;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeProductVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

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

    private String categoryName;
}
