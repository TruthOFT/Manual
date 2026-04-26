package com.manual.manual.model.dto.staff;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AdminStaffSaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;

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
}
