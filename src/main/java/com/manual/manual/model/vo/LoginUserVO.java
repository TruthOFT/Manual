package com.manual.manual.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoginUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String userAccount;

    private String avatarUrl;

    private String phone;

    private String email;

    private Integer gender;

    private String userRole;

    private Integer userStatus;

    private BigDecimal balance;

    private Date lastLoginTime;

    private Date createTime;

    private Date updateTime;
}
