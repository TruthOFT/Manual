package com.manual.manual.model.vo.artisanapplication;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminArtisanApplicationListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userAccount;

    private String userName;

    private String artisanName;

    private String shopName;

    private String contactPhone;

    private BigDecimal depositAmount;

    private Integer auditStatus;

    private String applyTime;

    private String auditTime;
}