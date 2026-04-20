package com.manual.manual.model.dto.category;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AdminCategorySaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long parentId;

    private String categoryName;

    private String categoryIcon;

    private String categoryDesc;

    private Integer categoryLevel;

    private Integer sortOrder;

    private Integer isEnable;
}
