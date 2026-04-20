package com.manual.manual.model.vo.product;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductListPageVO {

    private List<ProductListItemVO> products = new ArrayList<>();

    private ProductFilterOptionsVO filters = new ProductFilterOptionsVO();
}
