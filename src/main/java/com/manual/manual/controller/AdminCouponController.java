package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.coupon.AdminCouponReceiveRequest;
import com.manual.manual.model.dto.coupon.AdminCouponSaveRequest;
import com.manual.manual.model.vo.coupon.AdminCouponReceiveVO;
import com.manual.manual.model.vo.coupon.AdminCouponVO;
import com.manual.manual.service.AdminCouponService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    @Resource
    private AdminCouponService adminCouponService;

    @GetMapping
    public BaseResponse<List<AdminCouponVO>> listCoupons(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) Integer couponStatus,
                                                         HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.listCoupons(keyword, couponStatus, request), "List coupons success");
    }

    @GetMapping("/{couponId}")
    public BaseResponse<AdminCouponVO> getCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.getCoupon(couponId, request), "Get coupon success");
    }

    @PostMapping
    public BaseResponse<Long> createCoupon(@RequestBody AdminCouponSaveRequest saveRequest, HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.createCoupon(saveRequest, request), "Create coupon success");
    }

    @PutMapping("/{couponId}")
    public BaseResponse<Boolean> updateCoupon(@PathVariable Long couponId,
                                              @RequestBody AdminCouponSaveRequest saveRequest,
                                              HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.updateCoupon(couponId, saveRequest, request), "Update coupon success");
    }

    @DeleteMapping("/{couponId}")
    public BaseResponse<Boolean> deleteCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.deleteCoupon(couponId, request), "Delete coupon success");
    }

    @PostMapping("/{couponId}/status")
    public BaseResponse<Boolean> updateCouponStatus(@PathVariable Long couponId,
                                                    @RequestParam Integer couponStatus,
                                                    HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.updateCouponStatus(couponId, couponStatus, request), "Update coupon status success");
    }

    @PostMapping("/{couponId}/receive")
    public BaseResponse<Boolean> receiveCoupon(@PathVariable Long couponId,
                                               @RequestBody AdminCouponReceiveRequest receiveRequest,
                                               HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.receiveCoupon(couponId, receiveRequest, request), "Receive coupon success");
    }

    @GetMapping("/{couponId}/receives")
    public BaseResponse<List<AdminCouponReceiveVO>> listReceives(@PathVariable Long couponId, HttpServletRequest request) {
        return ResultUtils.success(adminCouponService.listReceives(couponId, request), "List coupon receives success");
    }
}
