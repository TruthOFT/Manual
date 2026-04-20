package com.manual.manual.service.impl;

import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.AdminDashboardMapper;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.dashboard.AdminDashboardOverviewVO;
import com.manual.manual.service.AdminDashboardService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    @Resource
    private AdminDashboardMapper adminDashboardMapper;

    @Resource
    private UserService userService;

    @Override
    public AdminDashboardOverviewVO getOverview(HttpServletRequest request) {
        requireAdmin(request);
        AdminDashboardOverviewVO overviewVO = adminDashboardMapper.selectOverview();
        if (overviewVO == null) {
            return createEmptyOverview();
        }
        overviewVO.setPendingAuditProductCount(defaultLong(overviewVO.getPendingAuditProductCount()));
        overviewVO.setPendingOrderCount(defaultLong(overviewVO.getPendingOrderCount()));
        overviewVO.setPendingShipmentOrderCount(defaultLong(overviewVO.getPendingShipmentOrderCount()));
        overviewVO.setAbnormalAddressOrderCount(defaultLong(overviewVO.getAbnormalAddressOrderCount()));
        overviewVO.setActiveArtisanCount(defaultLong(overviewVO.getActiveArtisanCount()));
        overviewVO.setRiskAlertCount(defaultLong(overviewVO.getRiskAlertCount()));
        overviewVO.setLowStockRiskCount(defaultLong(overviewVO.getLowStockRiskCount()));
        overviewVO.setRefundAlertCount(defaultLong(overviewVO.getRefundAlertCount()));
        overviewVO.setNegativeReviewRiskCount(defaultLong(overviewVO.getNegativeReviewRiskCount()));
        overviewVO.setRecentSevenDaysGmv(defaultDecimal(overviewVO.getRecentSevenDaysGmv()));
        overviewVO.setRecentSevenDaysOrderCount(defaultLong(overviewVO.getRecentSevenDaysOrderCount()));
        overviewVO.setRecentSevenDaysNewUserCount(defaultLong(overviewVO.getRecentSevenDaysNewUserCount()));
        return overviewVO;
    }

    private User requireAdmin(HttpServletRequest request) {
        User loginUser = userService.getAdminLoginUser(request);
        if (loginUser == null || !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Only admin users can access this endpoint");
        }
        return loginUser;
    }

    private AdminDashboardOverviewVO createEmptyOverview() {
        AdminDashboardOverviewVO overviewVO = new AdminDashboardOverviewVO();
        overviewVO.setPendingAuditProductCount(0L);
        overviewVO.setPendingOrderCount(0L);
        overviewVO.setPendingShipmentOrderCount(0L);
        overviewVO.setAbnormalAddressOrderCount(0L);
        overviewVO.setActiveArtisanCount(0L);
        overviewVO.setRiskAlertCount(0L);
        overviewVO.setLowStockRiskCount(0L);
        overviewVO.setRefundAlertCount(0L);
        overviewVO.setNegativeReviewRiskCount(0L);
        overviewVO.setRecentSevenDaysGmv(BigDecimal.ZERO);
        overviewVO.setRecentSevenDaysOrderCount(0L);
        overviewVO.setRecentSevenDaysNewUserCount(0L);
        return overviewVO;
    }

    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}