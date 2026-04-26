package com.manual.manual.model.vo.recommendation;

import lombok.Data;

@Data
public class RecommendationCalculateResultVO {

    private Long behaviorCount;

    private Integer productCount;

    private Integer userCount;

    private Integer similarityCount;

    private Integer recommendationCount;

    private String algorithmVersion;
}
