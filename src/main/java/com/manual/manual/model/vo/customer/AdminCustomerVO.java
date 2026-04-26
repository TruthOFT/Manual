package com.manual.manual.model.vo.customer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminCustomerVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long profileId;

    private String userAccount;

    private String userName;

    private String phone;

    private String email;

    private Integer gender;

    private Integer userStatus;

    private Integer customerLevel;

    private Integer points;

    private BigDecimal totalAmount;

    private Integer orderCount;

    private String preferenceTags;

    private String lastPurchaseTime;

    private String createTime;

    private String updateTime;
}
