package com.manual.manual.model.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDetailVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long artisanId;

    private String productName;

    private String productSubtitle;

    private String productCover;

    private String productDesc;

    private String craftType;

    private String materialDesc;

    private String originPlace;

    private Integer handmadeCycleDays;

    private Integer supportCustom;

    private Integer soldQuantity;

    private Integer favoriteCount;

    private Integer reviewCount;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private String categoryName;

    private String artisanName;

    private String shopName;

    private String artisanAvatar;

    private List<ProductImageVO> images = new ArrayList<>();

    private List<ProductMaterialVO> materials = new ArrayList<>();

    private List<ProductSkuVO> skus = new ArrayList<>();

    private List<ProductReviewVO> reviews = new ArrayList<>();
}
