package com.manual.manual.model.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminOrderListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    private String orderNo;

    private String buyerName;

    private String userAccount;

    private String productName;

    private String skuName;

    private Integer quantity;

    private BigDecimal payAmount;

    private Integer orderStatus;

    private Integer payStatus;

    private String payType;

    private String createTime;

    private String payTime;
}
