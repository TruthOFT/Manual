package com.manual.manual.model.vo.recommendation;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserProductBehaviorScoreVO {

    private Long userId;

    private Long productId;

    private BigDecimal behaviorScore;
}
