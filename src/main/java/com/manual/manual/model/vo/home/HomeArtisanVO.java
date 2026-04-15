package com.manual.manual.model.vo.home;

import lombok.Data;

@Data
public class HomeArtisanVO {

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
