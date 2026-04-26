package com.manual.manual.model.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminProductListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    private String categoryName;

    private String productName;

    private String productSubtitle;

    private String productCover;

    private Integer inventory;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer auditStatus;

    private Integer status;

    private String createTime;

    private String updateTime;
}
