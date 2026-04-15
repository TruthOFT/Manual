package com.manual.manual.model.vo.home;

import lombok.Data;

@Data
public class HomeCategoryVO {

    private Long id;

    private Long parentId;

    private String categoryName;

    private String categoryIcon;

    private String categoryDesc;

    private Integer categoryLevel;
}
