package com.manual.manual.model.vo.recommendation;

import lombok.Data;

@Data
public class RecommendationOverviewVO {

    private Long behaviorCount;

    private Long browseBehaviorCount;

    private Long orderBehaviorCount;

    private Long similarityCount;

    private Long recommendationCount;

    private Long coveredUserCount;

    private String latestCalculatedTime;
}
