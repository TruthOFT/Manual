package com.manual.manual.service;

import com.manual.manual.model.vo.coupon.UserCouponVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CouponService {

    List<UserCouponVO> listAvailableCoupons(HttpServletRequest request);

    Boolean receiveCoupon(Long couponId, HttpServletRequest request);

    List<UserCouponVO> listMyCoupons(Integer useStatus, HttpServletRequest request);
}
