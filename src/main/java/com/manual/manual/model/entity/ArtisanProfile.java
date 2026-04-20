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
@TableName("artisan_profile")
public class ArtisanProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

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

    private String qualificationImages;

    private BigDecimal depositAmount;

    private String auditRemark;

    private Integer auditStatus;

    private Integer shelfStatus;

    private Date applyTime;

    private Date auditTime;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}