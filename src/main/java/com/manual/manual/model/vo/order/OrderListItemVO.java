package com.manual.manual.model.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    private String orderNo;

    private Integer orderStatus;

    private Integer payStatus;

    private String payType;

    private BigDecimal payAmount;

    private String createTime;

    private String payTime;

    private OrderItemVO item;
}
