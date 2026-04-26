package com.manual.manual.model.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AdminProductDetailVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    private String categoryName;

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

    private Integer soldQuantity;

    private Integer favoriteCount;

    private Integer reviewCount;

    private Integer auditStatus;

    private Integer status;

    private Integer sortOrder;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private List<ProductImageVO> images = new ArrayList<>();

    private List<ProductMaterialVO> materials = new ArrayList<>();

    private List<ProductSkuVO> skus = new ArrayList<>();
}
