package com.manual.manual.service;

import com.manual.manual.model.dto.coupon.AdminCouponReceiveRequest;
import com.manual.manual.model.dto.coupon.AdminCouponSaveRequest;
import com.manual.manual.model.vo.coupon.AdminCouponReceiveVO;
import com.manual.manual.model.vo.coupon.AdminCouponVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminCouponService {

    List<AdminCouponVO> listCoupons(String keyword, Integer couponStatus, HttpServletRequest request);

    AdminCouponVO getCoupon(Long couponId, HttpServletRequest request);

    Long createCoupon(AdminCouponSaveRequest saveRequest, HttpServletRequest request);

    Boolean updateCoupon(Long couponId, AdminCouponSaveRequest saveRequest, HttpServletRequest request);

    Boolean deleteCoupon(Long couponId, HttpServletRequest request);

    Boolean updateCouponStatus(Long couponId, Integer couponStatus, HttpServletRequest request);

    Boolean receiveCoupon(Long couponId, AdminCouponReceiveRequest receiveRequest, HttpServletRequest request);

    List<AdminCouponReceiveVO> listReceives(Long couponId, HttpServletRequest request);
}
