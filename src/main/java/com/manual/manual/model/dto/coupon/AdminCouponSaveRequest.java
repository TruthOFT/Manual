package com.manual.manual.model.dto.coupon;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AdminCouponSaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String couponName;

    private Integer couponType;

    private BigDecimal thresholdAmount;

    private BigDecimal discountAmount;

    private BigDecimal discountRate;

    private Integer totalCount;

    private String startTime;

    private String endTime;

    private Integer couponStatus;
}
