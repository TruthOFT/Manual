package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.coupon.UserCouponVO;
import com.manual.manual.service.CouponService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Resource
    private CouponService couponService;

    @GetMapping("/available")
    public BaseResponse<List<UserCouponVO>> listAvailableCoupons(HttpServletRequest request) {
        return ResultUtils.success(couponService.listAvailableCoupons(request), "获取可领取优惠券成功");
    }

    @PostMapping("/{couponId}/receive")
    public BaseResponse<Boolean> receiveCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        return ResultUtils.success(couponService.receiveCoupon(couponId, request), "领取优惠券成功");
    }

    @GetMapping("/my")
    public BaseResponse<List<UserCouponVO>> listMyCoupons(@RequestParam(required = false) Integer useStatus,
                                                          HttpServletRequest request) {
        return ResultUtils.success(couponService.listMyCoupons(useStatus, request), "获取我的优惠券成功");
    }
}
