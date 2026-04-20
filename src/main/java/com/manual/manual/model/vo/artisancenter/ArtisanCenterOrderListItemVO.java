package com.manual.manual.model.vo.artisancenter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArtisanCenterOrderListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuId;

    private String orderNo;

    private String productName;

    private String skuName;

    private String productCover;

    private Integer quantity;

    private BigDecimal totalAmount;

    private Integer orderStatus;

    private Integer deliveryStatus;

    private String buyerName;

    private String createTime;
}
