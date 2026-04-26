package com.manual.manual.model.dto.customer;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AdminCustomerSaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;

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
}
