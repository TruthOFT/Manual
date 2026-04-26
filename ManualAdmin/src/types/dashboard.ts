export type AdminDashboardOverview = {
    pendingAuditProductCount: number
    pendingOrderCount: number
    pendingShipmentOrderCount: number
    abnormalAddressOrderCount: number
    activeCustomerCount: number
    riskAlertCount: number
    lowStockRiskCount: number
    refundAlertCount: number
    negativeReviewRiskCount: number
    recentSevenDaysGmv: number | string
    recentSevenDaysOrderCount: number
    recentSevenDaysNewUserCount: number
}
