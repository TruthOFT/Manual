package com.manual.manual.model.vo.coupon;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class AdminCouponReceiveVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long couponId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userAccount;

    private String userName;

    private String phone;

    private String receiveTime;

    private Integer useStatus;

    private String useTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
}
