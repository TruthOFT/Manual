package com.manual.manual.model.dto.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminUserUpdateRequest {

    private String userPassword;

    private String userName;

    private String avatarUrl;

    private String phone;

    private String email;

    private Integer gender;

    private String userRole;

    private Integer userStatus;

    private BigDecimal balance;
}
