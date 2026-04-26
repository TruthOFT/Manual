package com.manual.manual.model.dto.coupon;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AdminCouponReceiveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
}
