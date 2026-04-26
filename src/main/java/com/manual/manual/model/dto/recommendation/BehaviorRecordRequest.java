package com.manual.manual.model.dto.recommendation;

import lombok.Data;

@Data
public class BehaviorRecordRequest {

    private Long productId;

    private Long skuId;

    private Integer behaviorType;

    private String sourcePage;

    private Integer staySeconds;

    private String deviceType;
}
