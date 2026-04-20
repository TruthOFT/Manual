package com.manual.manual.model.vo.dashboard;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminDashboardOverviewVO {

    private Long pendingAuditProductCount;

    private Long pendingOrderCount;

    private Long pendingShipmentOrderCount;

    private Long abnormalAddressOrderCount;

    private Long activeArtisanCount;

    private Long riskAlertCount;

    private Long lowStockRiskCount;

    private Long refundAlertCount;

    private Long negativeReviewRiskCount;

    private BigDecimal recentSevenDaysGmv;

    private Long recentSevenDaysOrderCount;

    private Long recentSevenDaysNewUserCount;
}