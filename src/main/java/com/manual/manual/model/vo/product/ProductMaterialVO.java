package com.manual.manual.model.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ProductMaterialVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String materialName;

    private String materialOrigin;

    private String materialRatio;
}
