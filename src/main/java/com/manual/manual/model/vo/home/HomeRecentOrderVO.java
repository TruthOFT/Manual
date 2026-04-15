package com.manual.manual.model.vo.home;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeRecentOrderVO {

    private Long id;

    private String orderNo;

    private String productName;

    private String skuName;

    private String productCover;

    private Integer quantity;

    private BigDecimal totalAmount;

    private String finishTime;
}
