package com.manual.manual.model.vo.recommendation;

import lombok.Data;

@Data
public class FavoriteBehaviorSeedVO {

    private Long userId;

    private Long productId;

    private Long favoriteId;
}
