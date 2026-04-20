package com.manual.manual.model.dto.artisancenter;

import lombok.Data;

@Data
public class ArtisanCenterProductImageRequest {

    private String imageUrl;

    private String imageType;

    private Integer sortOrder;
}
