package com.manual.manual.model.vo.product;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminProductMetaVO {

    private List<AdminProductCategoryOptionVO> categories = new ArrayList<>();

}
