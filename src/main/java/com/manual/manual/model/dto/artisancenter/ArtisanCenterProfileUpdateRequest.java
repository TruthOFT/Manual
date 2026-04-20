package com.manual.manual.model.dto.artisancenter;

import lombok.Data;

@Data
public class ArtisanCenterProfileUpdateRequest {

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
}
