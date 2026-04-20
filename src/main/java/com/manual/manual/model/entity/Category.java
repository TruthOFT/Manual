package com.manual.manual.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long parentId;

    private String categoryName;

    private String categoryIcon;

    private String categoryDesc;

    private Integer categoryLevel;

    private Integer sortOrder;

    private Integer isEnable;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
