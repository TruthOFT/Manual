package com.manual.manual.model.vo.staff;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminStaffVO {

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

    private String staffName;

    private String staffNo;

    private String positionName;

    private BigDecimal salary;

    private String entryTime;

    private Integer staffStatus;

    private String createTime;

    private String updateTime;
}
