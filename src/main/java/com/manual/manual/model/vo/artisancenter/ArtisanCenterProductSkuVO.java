package com.manual.manual.model.vo.artisancenter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArtisanCenterProductSkuVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String skuCode;

    private String skuName;

    private String skuCover;

    private String specText;

    private String materialType;

    private BigDecimal weight;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer status;
}
