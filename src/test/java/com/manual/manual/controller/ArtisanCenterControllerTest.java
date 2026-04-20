package com.manual.manual.controller;

import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterDashboardVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductPageVO;
import com.manual.manual.service.ArtisanCenterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtisanCenterController.class)
class ArtisanCenterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtisanCenterService artisanCenterService;

    @Test
    void shouldReturnDashboard() throws Exception {
        ArtisanCenterDashboardVO dashboardVO = new ArtisanCenterDashboardVO();
        dashboardVO.setPendingAuditCount(2L);
        dashboardVO.setOnShelfCount(4L);
        dashboardVO.setRecentSevenDaysAmount(new BigDecimal("1288.00"));

        when(artisanCenterService.getDashboard(any())).thenReturn(dashboardVO);

        mockMvc.perform(get("/artisan-center/dashboard").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.pendingAuditCount").value(2))
                .andExpect(jsonPath("$.data.onShelfCount").value(4))
                .andExpect(jsonPath("$.data.recentSevenDaysAmount").value(1288.00));
    }

    @Test
    void shouldReturnProductPage() throws Exception {
        ArtisanCenterProductPageVO pageVO = new ArtisanCenterProductPageVO();
        ArtisanCenterProductListItemVO itemVO = new ArtisanCenterProductListItemVO();
        itemVO.setId(3100000000000004001L);
        itemVO.setProductName("柴烧釉陶马克杯");
        itemVO.setAuditStatus(0);
        pageVO.setProducts(List.of(itemVO));

        when(artisanCenterService.listProducts(eq(0), eq(1), eq("柴烧"), any())).thenReturn(pageVO);

        mockMvc.perform(get("/artisan-center/products")
                        .param("auditStatus", "0")
                        .param("status", "1")
                        .param("keyword", "柴烧")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.products[0].productName").value("柴烧釉陶马克杯"))
                .andExpect(jsonPath("$.data.products[0].auditStatus").value(0));
    }

    @Test
    void shouldReturnOrderDetail() throws Exception {
        ArtisanCenterOrderDetailVO detailVO = new ArtisanCenterOrderDetailVO();
        detailVO.setId(3100000000000009001L);
        detailVO.setOrderNo("mo202604130001");
        detailVO.setProductName("柴烧釉陶马克杯");
        detailVO.setReceiverName("林知夏");

        when(artisanCenterService.getOrderDetail(eq(3100000000000009001L), any())).thenReturn(detailVO);

        mockMvc.perform(get("/artisan-center/orders/3100000000000009001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderNo").value("mo202604130001"))
                .andExpect(jsonPath("$.data.productName").value("柴烧釉陶马克杯"))
                .andExpect(jsonPath("$.data.receiverName").value("林知夏"));
    }

    @Test
    void shouldReturnCustomRequirementDetail() throws Exception {
        ArtisanCenterCustomRequirementDetailVO detailVO = new ArtisanCenterCustomRequirementDetailVO();
        detailVO.setId(3100000000000010001L);
        detailVO.setRequirementTitle("刻字定制");
        detailVO.setProductName("黑胡桃榫卯首饰盒");

        when(artisanCenterService.getCustomRequirementDetail(eq(3100000000000010001L), any())).thenReturn(detailVO);

        mockMvc.perform(get("/artisan-center/custom-requirements/3100000000000010001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.requirementTitle").value("刻字定制"))
                .andExpect(jsonPath("$.data.productName").value("黑胡桃榫卯首饰盒"));
    }
}
