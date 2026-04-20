package com.manual.manual.model.vo.artisanapplication;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ArtisanApplicationVO {

    private Boolean hasApplication;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userRole;

    private String artisanName;

    private String shopName;

    private String artisanAvatar;

    private String coverUrl;

    private String artisanStory;

    private String craftCategory;

    private String originPlace;

    private Integer experienceYears;

    private Integer supportCustom;

    private String contactPhone;

    private String qualificationDesc;

    private List<String> qualificationImages = new ArrayList<>();

    private BigDecimal depositAmount;

    private String auditRemark;

    private Integer auditStatus;

    private Integer shelfStatus;

    private String applyTime;

    private String auditTime;
}