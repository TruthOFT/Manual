package com.manual.manual.model.dto.order;

import lombok.Data;

@Data
public class OrderCreateRequest {

    private Long skuId;

    private Integer quantity;

    private Long addressId;

    private Long couponReceiveId;

    private String buyerRemark;
}
