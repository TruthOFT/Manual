package com.manual.manual.model.vo.coupon;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminCouponVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String couponName;

    private Integer couponType;

    private BigDecimal thresholdAmount;

    private BigDecimal discountAmount;

    private BigDecimal discountRate;

    private Integer totalCount;

    private Integer receiveCount;

    private Integer usedCount;

    private String startTime;

    private String endTime;

    private Integer couponStatus;

    private String createTime;

    private String updateTime;
}
