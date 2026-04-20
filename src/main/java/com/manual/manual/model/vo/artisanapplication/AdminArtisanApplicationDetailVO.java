package com.manual.manual.model.vo.artisanapplication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AdminArtisanApplicationDetailVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userAccount;

    private String userName;

    private String userAvatarUrl;

    private String phone;

    private String email;

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

    @JsonIgnore
    private String qualificationImagesRaw;

    private List<String> qualificationImages = new ArrayList<>();

    private BigDecimal depositAmount;

    private String auditRemark;

    private Integer auditStatus;

    private Integer shelfStatus;

    private String applyTime;

    private String auditTime;

    private String createTime;

    private String updateTime;
}