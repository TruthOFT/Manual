package com.manual.manual.model.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class ProductReviewVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Integer score;

    private String reviewContent;

    private String reviewImages;

    private Integer isAnonymous;

    private String replyContent;

    private String replyTime;

    private String createTime;
}
