package com.manual.manual.model.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuId;

    private String productName;

    private String skuName;

    private String productCover;

    private String skuCover;

    private String specText;

    private Integer quantity;

    private BigDecimal salePrice;

    private BigDecimal totalAmount;
}
