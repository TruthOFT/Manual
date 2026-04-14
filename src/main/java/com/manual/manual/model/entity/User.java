package com.manual.manual.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String userAccount;

    private String userPassword;

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

    @TableLogic
    private Integer isDelete;
}
