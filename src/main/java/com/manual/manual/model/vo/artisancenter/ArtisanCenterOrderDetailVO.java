package com.manual.manual.model.vo.artisancenter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArtisanCenterOrderDetailVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long skuId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String orderNo;

    private String productName;

    private String skuName;

    private String productCover;

    private String specText;

    private Integer quantity;

    private BigDecimal salePrice;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    private Integer orderStatus;

    private Integer payStatus;

    private Integer deliveryStatus;

    private String buyerName;

    private String buyerRemark;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String createTime;

    private String payTime;

    private String deliveryTime;

    private ArtisanCenterOrderLogisticsVO logistics;
}
