package com.manual.manual.model.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class AdminProductCategoryOptionVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String parentName;

    private String categoryName;

    private Integer categoryLevel;
}
