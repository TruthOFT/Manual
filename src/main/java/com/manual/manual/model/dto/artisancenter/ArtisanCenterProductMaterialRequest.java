package com.manual.manual.model.dto.artisancenter;

import lombok.Data;

@Data
public class ArtisanCenterProductMaterialRequest {

    private String materialName;

    private String materialOrigin;

    private String materialRatio;

    private Integer sortOrder;
}
