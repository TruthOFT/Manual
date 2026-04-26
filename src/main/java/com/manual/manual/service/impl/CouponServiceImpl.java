package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.CouponMapper;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.coupon.UserCouponVO;
import com.manual.manual.service.CouponService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private UserService userService;

    @Override
    public List<UserCouponVO> listAvailableCoupons(HttpServletRequest request) {
        User loginUser = requireCustomer(request);
        List<UserCouponVO> coupons = couponMapper.selectAvailableCoupons(loginUser.getId());
        return coupons == null ? Collections.emptyList() : coupons;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean receiveCoupon(Long couponId, HttpServletRequest request) {
        User loginUser = requireCustomer(request);
        if (couponId == null || couponId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Coupon id is invalid");
        }
        UserCouponVO coupon = couponMapper.selectCoupon(couponId);
        if (coupon == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Coupon not found");
        }
        if (couponMapper.countUserReceive(couponId, loginUser.getId()) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Coupon already received");
        }
        if (couponMapper.increaseReceiveCount(couponId) <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Coupon is unavailable");
        }
        return couponMapper.insertCouponReceive(IdWorker.getId(), couponId, loginUser.getId()) > 0;
    }

    @Override
    public List<UserCouponVO> listMyCoupons(Integer useStatus, HttpServletRequest request) {
        User loginUser = requireCustomer(request);
        Integer normalizedStatus = normalizeUseStatus(useStatus);
        List<UserCouponVO> coupons = couponMapper.selectMyCoupons(loginUser.getId(), normalizedStatus);
        return coupons == null ? Collections.emptyList() : coupons;
    }

    private User requireCustomer(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return loginUser;
    }

    private Integer normalizeUseStatus(Integer useStatus) {
        if (useStatus == null) {
            return null;
        }
        if (useStatus != 0 && useStatus != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Use status is invalid");
        }
        return useStatus;
    }
}
