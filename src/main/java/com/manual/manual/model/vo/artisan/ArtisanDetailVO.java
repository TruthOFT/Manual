package com.manual.manual.model.vo.artisan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.manual.manual.model.vo.product.ProductListItemVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArtisanDetailVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String artisanName;

    private String shopName;

    private String artisanAvatar;

    private String coverUrl;

    private String artisanStory;

    private String craftCategory;

    private String originPlace;

    private Integer experienceYears;

    private Integer supportCustom;

    private Long productCount;

    private List<ProductListItemVO> products = new ArrayList<>();
}
