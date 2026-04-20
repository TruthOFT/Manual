package com.manual.manual.model.vo.artisan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ArtisanListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String artisanName;

    private String shopName;

    private String artisanAvatar;

    private String coverUrl;

    private String craftCategory;

    private String originPlace;

    private Integer experienceYears;

    private Integer supportCustom;

    private Long productCount;
}
