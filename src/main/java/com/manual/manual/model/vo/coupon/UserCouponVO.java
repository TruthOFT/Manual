package com.manual.manual.model.vo.coupon;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCouponVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiveId;

    private String couponName;

    private Integer couponType;

    private BigDecimal thresholdAmount;

    private BigDecimal discountAmount;

    private BigDecimal discountRate;

    private Integer totalCount;

    private Integer receiveCount;

    private Integer remainingCount;

    private String startTime;

    private String endTime;

    private String receiveTime;

    private Integer useStatus;

    private String useTime;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
}
