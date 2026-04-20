package com.manual.manual.model.vo.product;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductFilterOptionsVO {

    private List<ProductFilterCategoryVO> categories = new ArrayList<>();

    private List<String> originPlaces = new ArrayList<>();

    private List<String> materials = new ArrayList<>();
}
