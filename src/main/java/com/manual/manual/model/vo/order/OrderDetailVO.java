package com.manual.manual.model.vo.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    private String orderNo;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String buyerName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long addressId;

    private Integer orderStatus;

    private Integer payStatus;

    private String payType;

    private Integer deliveryStatus;

    private BigDecimal productAmount;

    private BigDecimal discountAmount;

    private BigDecimal freightAmount;

    private BigDecimal payAmount;

    private String buyerRemark;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    private String detailAddress;

    private String payTime;

    private String deliveryTime;

    private String receiveTime;

    private String finishTime;

    private String cancelTime;

    private String createTime;

    private String expireTime;

    private OrderItemVO item;
}
