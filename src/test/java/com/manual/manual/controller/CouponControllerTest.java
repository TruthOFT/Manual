package com.manual.manual.controller;

import com.manual.manual.model.vo.coupon.UserCouponVO;
import com.manual.manual.service.CouponService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Test
    void shouldReturnAvailableCoupons() throws Exception {
        UserCouponVO coupon = new UserCouponVO();
        coupon.setId(4100000000000001001L);
        coupon.setCouponName("新人满减券");
        coupon.setCouponType(1);
        coupon.setThresholdAmount(new BigDecimal("100.00"));
        coupon.setDiscountAmount(new BigDecimal("20.00"));
        coupon.setRemainingCount(5);

        when(couponService.listAvailableCoupons(any())).thenReturn(List.of(coupon));

        mockMvc.perform(get("/coupons/available").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].id").value("4100000000000001001"))
                .andExpect(jsonPath("$.data[0].couponName").value("新人满减券"))
                .andExpect(jsonPath("$.data[0].remainingCount").value(5));
    }

    @Test
    void shouldReceiveCoupon() throws Exception {
        when(couponService.receiveCoupon(eq(4100000000000001002L), any())).thenReturn(true);

        mockMvc.perform(post("/coupons/4100000000000001002/receive").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void shouldReturnMyCoupons() throws Exception {
        UserCouponVO coupon = new UserCouponVO();
        coupon.setId(4100000000000001003L);
        coupon.setReceiveId(4200000000000001003L);
        coupon.setCouponName("会员折扣券");
        coupon.setCouponType(2);
        coupon.setDiscountRate(new BigDecimal("8.80"));
        coupon.setUseStatus(0);

        when(couponService.listMyCoupons(eq(0), any())).thenReturn(List.of(coupon));

        mockMvc.perform(get("/coupons/my")
                        .param("useStatus", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].receiveId").value("4200000000000001003"))
                .andExpect(jsonPath("$.data[0].useStatus").value(0));
    }
}
