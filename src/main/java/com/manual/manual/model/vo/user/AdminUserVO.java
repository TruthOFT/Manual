package com.manual.manual.model.vo.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AdminUserVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String userAccount;

    private String userName;

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
