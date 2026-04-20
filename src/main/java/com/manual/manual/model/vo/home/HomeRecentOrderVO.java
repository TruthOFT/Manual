package com.manual.manual.model.vo.home;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeRecentOrderVO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String orderNo;

    private String productName;

    private String skuName;

    private String productCover;

    private Integer quantity;

    private BigDecimal totalAmount;

    private String finishTime;
}
