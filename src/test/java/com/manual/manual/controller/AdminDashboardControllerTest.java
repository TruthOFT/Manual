package com.manual.manual.controller;

import com.manual.manual.model.vo.dashboard.AdminDashboardOverviewVO;
import com.manual.manual.service.AdminDashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminDashboardController.class)
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminDashboardService adminDashboardService;

    @Test
    void shouldReturnOverview() throws Exception {
        AdminDashboardOverviewVO overviewVO = new AdminDashboardOverviewVO();
        overviewVO.setPendingAuditProductCount(18L);
        overviewVO.setPendingOrderCount(27L);
        overviewVO.setPendingShipmentOrderCount(21L);
        overviewVO.setAbnormalAddressOrderCount(6L);
        overviewVO.setActiveCustomerCount(46L);
        overviewVO.setRiskAlertCount(5L);
        overviewVO.setLowStockRiskCount(2L);
        overviewVO.setRefundAlertCount(1L);
        overviewVO.setNegativeReviewRiskCount(2L);
        overviewVO.setRecentSevenDaysGmv(new BigDecimal("86420.00"));
        overviewVO.setRecentSevenDaysOrderCount(324L);
        overviewVO.setRecentSevenDaysNewUserCount(91L);

        when(adminDashboardService.getOverview(any())).thenReturn(overviewVO);

        mockMvc.perform(get("/admin/dashboard/overview").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.pendingAuditProductCount").value(18))
                .andExpect(jsonPath("$.data.pendingOrderCount").value(27))
                .andExpect(jsonPath("$.data.recentSevenDaysGmv").value(86420.00));
    }
}
