package com.manual.manual.service;

import com.manual.manual.config.OrderProperties;
import com.manual.manual.mapper.OrderMapper;
import com.manual.manual.messaging.OrderDelayMessageService;
import com.manual.manual.model.dto.order.OrderCreateRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.coupon.UserCouponVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import com.manual.manual.model.vo.order.OrderSkuSnapshotVO;
import com.manual.manual.model.vo.order.UserAddressVO;
import com.manual.manual.service.impl.OrderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderDelayMessageService orderDelayMessageService;

    @Mock
    private OrderProperties orderProperties;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldApplyCouponWhenCreatingOrder() {
        User user = new User();
        user.setId(3100000000000005001L);

        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setSkuId(3200000000000005001L);
        createRequest.setQuantity(2);
        createRequest.setAddressId(3300000000000005001L);
        createRequest.setCouponReceiveId(3400000000000005001L);

        OrderSkuSnapshotVO sku = new OrderSkuSnapshotVO();
        sku.setSkuId(3200000000000005001L);
        sku.setProductId(3500000000000005001L);
        sku.setProductName("手作杯");
        sku.setSkuName("标准款");
        sku.setPrice(new BigDecimal("100.00"));
        sku.setStock(10);
        sku.setLockedStock(0);
        sku.setSkuStatus(1);

        UserAddressVO address = new UserAddressVO();
        address.setId(3300000000000005001L);
        address.setReceiverName("买家");
        address.setReceiverPhone("13800002222");
        address.setProvince("浙江省");
        address.setCity("杭州市");
        address.setDistrict("西湖区");
        address.setDetailAddress("文一路 1 号");

        UserCouponVO coupon = new UserCouponVO();
        coupon.setId(3600000000000005001L);
        coupon.setReceiveId(3400000000000005001L);
        coupon.setCouponType(1);
        coupon.setThresholdAmount(new BigDecimal("100.00"));
        coupon.setDiscountAmount(new BigDecimal("20.00"));

        OrderDetailVO detail = new OrderDetailVO();
        detail.setOrderId(3700000000000005001L);

        when(userService.getLoginUser(request)).thenReturn(user);
        when(orderMapper.selectSkuSnapshot(3200000000000005001L)).thenReturn(sku);
        when(orderMapper.selectUserAddress(user.getId(), 3300000000000005001L)).thenReturn(address);
        when(orderMapper.lockSkuStock(3200000000000005001L, 2)).thenReturn(1);
        when(orderMapper.selectUsableCouponReceive(user.getId(), 3400000000000005001L)).thenReturn(coupon);
        when(orderMapper.markCouponReceiveUsed(eq(user.getId()), eq(3400000000000005001L), anyLong())).thenReturn(1);
        when(orderMapper.increaseCouponUsedCount(3600000000000005001L)).thenReturn(1);
        when(orderMapper.selectUserOrderDetail(eq(user.getId()), anyLong(), eq(15))).thenReturn(detail);
        when(orderProperties.getTimeoutMinutes()).thenReturn(15);

        OrderDetailVO result = orderService.createOrder(createRequest, request);

        assertNotNull(result);
        verify(orderMapper).insertOrder(
                anyLong(),
                anyString(),
                eq(user.getId()),
                eq(3300000000000005001L),
                eq(new BigDecimal("200.00")),
                eq(new BigDecimal("20.00")),
                eq(BigDecimal.ZERO),
                eq(new BigDecimal("180.00")),
                eq(null),
                eq(address)
        );
        verify(orderMapper).markCouponReceiveUsed(eq(user.getId()), eq(3400000000000005001L), anyLong());
        verify(orderMapper).increaseCouponUsedCount(3600000000000005001L);
    }
}
