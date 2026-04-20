package com.manual.manual.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserRechargeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private BigDecimal amount;
}