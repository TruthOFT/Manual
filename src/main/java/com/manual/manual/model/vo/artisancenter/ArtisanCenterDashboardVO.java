package com.manual.manual.model.vo.artisancenter;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArtisanCenterDashboardVO {

    private Long pendingAuditCount;

    private Long onShelfCount;

    private Long pendingShipmentCount;

    private Long pendingCustomCount;

    private BigDecimal recentSevenDaysAmount;

    private Integer recentSevenDaysSales;
}
