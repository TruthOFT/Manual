package com.manual.manual.service;

import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.CouponMapper;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.coupon.UserCouponVO;
import com.manual.manual.service.impl.CouponServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponMapper couponMapper;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    void shouldRejectGuestWhenListingAvailableCoupons() {
        when(userService.getLoginUser(request)).thenReturn(null);

        assertThatThrownBy(() -> couponService.listAvailableCoupons(request))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.NO_AUTH_ERROR.getCode());
    }

    @Test
    void shouldRejectWhenUserAlreadyReceived() {
        User user = customer();
        UserCouponVO coupon = new UserCouponVO();
        coupon.setId(4100000000000002001L);
        when(userService.getLoginUser(request)).thenReturn(user);
        when(couponMapper.selectCoupon(4100000000000002001L)).thenReturn(coupon);
        when(couponMapper.countUserReceive(4100000000000002001L, user.getId())).thenReturn(1);

        assertThatThrownBy(() -> couponService.receiveCoupon(4100000000000002001L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Coupon already received");
    }

    @Test
    void shouldRejectWhenCouponIsUnavailable() {
        User user = customer();
        UserCouponVO coupon = new UserCouponVO();
        coupon.setId(4100000000000002002L);
        when(userService.getLoginUser(request)).thenReturn(user);
        when(couponMapper.selectCoupon(4100000000000002002L)).thenReturn(coupon);
        when(couponMapper.countUserReceive(4100000000000002002L, user.getId())).thenReturn(0);
        when(couponMapper.increaseReceiveCount(4100000000000002002L)).thenReturn(0);

        assertThatThrownBy(() -> couponService.receiveCoupon(4100000000000002002L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Coupon is unavailable");
    }

    @Test
    void shouldInsertReceiveAndIncreaseCount() {
        User user = customer();
        UserCouponVO coupon = new UserCouponVO();
        coupon.setId(4100000000000002003L);
        when(userService.getLoginUser(request)).thenReturn(user);
        when(couponMapper.selectCoupon(4100000000000002003L)).thenReturn(coupon);
        when(couponMapper.countUserReceive(4100000000000002003L, user.getId())).thenReturn(0);
        when(couponMapper.increaseReceiveCount(4100000000000002003L)).thenReturn(1);
        when(couponMapper.insertCouponReceive(any(Long.class), eq(4100000000000002003L), eq(user.getId()))).thenReturn(1);

        assertTrue(couponService.receiveCoupon(4100000000000002003L, request));
        verify(couponMapper).increaseReceiveCount(4100000000000002003L);
        verify(couponMapper).insertCouponReceive(any(Long.class), eq(4100000000000002003L), eq(user.getId()));
    }

    private User customer() {
        User user = new User();
        user.setId(3100000000000003001L);
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        return user;
    }
}
