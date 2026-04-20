package com.manual.manual.model.vo.artisancenter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ArtisanCenterCustomRequirementListItemVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderItemId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long productId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String orderNo;

    private String productName;

    private String buyerName;

    private String requirementTitle;

    private String requirementContent;

    private Integer confirmStatus;

    private String confirmRemark;

    private String confirmTime;

    private String createTime;
}
