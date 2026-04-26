package com.manual.manual.model.vo.recommendation;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSimilarityScoreVO {

    private Long productId;

    private Long similarProductId;

    private BigDecimal similarityScore;

    private Integer coBehaviorCount;
}
