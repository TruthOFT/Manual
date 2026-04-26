package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.AdminCouponMapper;
import com.manual.manual.model.dto.coupon.AdminCouponReceiveRequest;
import com.manual.manual.model.dto.coupon.AdminCouponSaveRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.coupon.AdminCouponReceiveVO;
import com.manual.manual.model.vo.coupon.AdminCouponVO;
import com.manual.manual.service.AdminCouponService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class AdminCouponServiceImpl implements AdminCouponService {

    private static final int COUPON_STATUS_DISABLED = 0;
    private static final int COUPON_STATUS_ENABLED = 1;

    @Resource
    private AdminCouponMapper adminCouponMapper;

    @Resource
    private UserService userService;

    @Override
    public List<AdminCouponVO> listCoupons(String keyword, Integer couponStatus, HttpServletRequest request) {
        requireAdmin(request);
        List<AdminCouponVO> coupons = adminCouponMapper.selectCoupons(trim(keyword), couponStatus);
        return coupons == null ? Collections.emptyList() : coupons;
    }

    @Override
    public AdminCouponVO getCoupon(Long couponId, HttpServletRequest request) {
        requireAdmin(request);
        return requireCoupon(couponId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoupon(AdminCouponSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        sanitizeSave(saveRequest);
        Long couponId = IdWorker.getId();
        if (adminCouponMapper.insertCoupon(couponId, saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create coupon failed");
        }
        return couponId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCoupon(Long couponId, AdminCouponSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        requireCoupon(couponId);
        sanitizeSave(saveRequest);
        return adminCouponMapper.updateCoupon(couponId, saveRequest) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCoupon(Long couponId, HttpServletRequest request) {
        requireAdmin(request);
        requireCoupon(couponId);
        return adminCouponMapper.deleteCoupon(couponId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCouponStatus(Long couponId, Integer couponStatus, HttpServletRequest request) {
        requireAdmin(request);
        requireCoupon(couponId);
        int status = normalizeStatus(couponStatus);
        return adminCouponMapper.updateCouponStatus(couponId, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean receiveCoupon(Long couponId, AdminCouponReceiveRequest receiveRequest, HttpServletRequest request) {
        requireAdmin(request);
        AdminCouponVO coupon = requireCoupon(couponId);
        if (coupon.getCouponStatus() == null || coupon.getCouponStatus() != COUPON_STATUS_ENABLED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Coupon is disabled");
        }
        if (receiveRequest == null || receiveRequest.getUserId() == null || receiveRequest.getUserId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Customer is required");
        }
        if (adminCouponMapper.countCustomer(receiveRequest.getUserId()) <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Customer not found");
        }
        if (adminCouponMapper.countUserUnusedReceive(couponId, receiveRequest.getUserId()) > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Customer already has an unused coupon");
        }
        if (adminCouponMapper.increaseReceiveCount(couponId) <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Coupon is unavailable");
        }
        return adminCouponMapper.insertCouponReceive(IdWorker.getId(), couponId, receiveRequest.getUserId()) > 0;
    }

    @Override
    public List<AdminCouponReceiveVO> listReceives(Long couponId, HttpServletRequest request) {
        requireAdmin(request);
        requireCoupon(couponId);
        List<AdminCouponReceiveVO> receives = adminCouponMapper.selectReceives(couponId);
        return receives == null ? Collections.emptyList() : receives;
    }

    private void sanitizeSave(AdminCouponSaveRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request body is required");
        }
        request.setCouponName(requiredTrim(request.getCouponName(), "Coupon name is required"));
        request.setCouponType(normalizeCouponType(request.getCouponType()));
        request.setThresholdAmount(nonNegativeAmount(request.getThresholdAmount()));
        if (request.getCouponType() == 2) {
            request.setDiscountAmount(BigDecimal.ZERO);
            request.setDiscountRate(nonNegativeAmount(request.getDiscountRate()));
            if (request.getDiscountRate().compareTo(BigDecimal.ZERO) <= 0 || request.getDiscountRate().compareTo(BigDecimal.TEN) >= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Discount rate must be between 0 and 10");
            }
        } else {
            request.setDiscountAmount(nonNegativeAmount(request.getDiscountAmount()));
            request.setDiscountRate(null);
        }
        request.setTotalCount(nonNegative(request.getTotalCount(), 0));
        request.setStartTime(requiredTrim(request.getStartTime(), "Start time is required"));
        request.setEndTime(requiredTrim(request.getEndTime(), "End time is required"));
        request.setCouponStatus(normalizeStatus(request.getCouponStatus()));
    }

    private AdminCouponVO requireCoupon(Long couponId) {
        if (couponId == null || couponId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Coupon id is invalid");
        }
        AdminCouponVO coupon = adminCouponMapper.selectCoupon(couponId);
        if (coupon == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Coupon not found");
        }
        return coupon;
    }

    private Integer normalizeCouponType(Integer value) {
        int type = value == null ? 1 : value;
        if (type != 1 && type != 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Coupon type is invalid");
        }
        return type;
    }

    private Integer normalizeStatus(Integer value) {
        int status = value == null ? COUPON_STATUS_ENABLED : value;
        if (status != COUPON_STATUS_DISABLED && status != COUPON_STATUS_ENABLED) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Coupon status is invalid");
        }
        return status;
    }

    private Integer nonNegative(Integer value, Integer defaultValue) {
        int result = value == null ? defaultValue : value;
        if (result < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Number fields must be greater than or equal to 0");
        }
        return result;
    }

    private BigDecimal nonNegativeAmount(BigDecimal value) {
        BigDecimal result = value == null ? BigDecimal.ZERO : value;
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Amount must be greater than or equal to 0");
        }
        return result;
    }

    private User requireAdmin(HttpServletRequest request) {
        User loginUser = userService.getAdminLoginUser(request);
        if (loginUser == null || !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return loginUser;
    }

    private String requiredTrim(String value, String message) {
        String trimmed = trim(value);
        if (trimmed == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
        return trimmed;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
