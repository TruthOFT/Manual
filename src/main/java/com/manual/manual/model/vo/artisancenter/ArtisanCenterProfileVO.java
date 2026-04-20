package com.manual.manual.model.vo.artisancenter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ArtisanCenterProfileVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
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

    private Integer auditStatus;

    private Integer shelfStatus;
}
