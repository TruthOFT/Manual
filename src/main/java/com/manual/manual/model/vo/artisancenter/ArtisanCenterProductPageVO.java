package com.manual.manual.model.vo.artisancenter;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArtisanCenterProductPageVO {

    private List<ArtisanCenterProductListItemVO> products = new ArrayList<>();

    private List<ArtisanCenterCategoryVO> categories = new ArrayList<>();
}
