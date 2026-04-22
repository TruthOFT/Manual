package com.manual.manual.model.vo.category;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class AdminCategoryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private String parentName;

    private String categoryName;

    private String categoryIcon;

    private String categoryDesc;

    private Integer categoryLevel;

    private Integer sortOrder;

    private Integer isEnable;

    private Long productCount;

    private Date createTime;

    private Date updateTime;
}
