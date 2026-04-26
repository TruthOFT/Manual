package com.manual.manual.model.dto.product;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AdminProductSaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    private Integer auditStatus;

    private Integer status;

    private Integer sortOrder;

    private List<AdminProductSkuSaveRequest> skus = new ArrayList<>();
}
