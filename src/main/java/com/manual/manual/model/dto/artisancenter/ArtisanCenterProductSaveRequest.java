package com.manual.manual.model.dto.artisancenter;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ArtisanCenterProductSaveRequest {

    private Long categoryId;

    private String productName;

    private String productSubtitle;

    private String productCover;

    private String productDesc;

    private String craftType;

    private String materialDesc;

    private String originPlace;

    private Integer handmadeCycleDays;

    private Integer supportCustom;

    private Integer inventory;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Integer sortOrder;

    private List<ArtisanCenterProductImageRequest> images = new ArrayList<>();

    private List<ArtisanCenterProductMaterialRequest> materials = new ArrayList<>();

    private List<ArtisanCenterProductSkuRequest> skus = new ArrayList<>();
}
