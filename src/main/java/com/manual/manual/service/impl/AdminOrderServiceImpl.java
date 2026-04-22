package com.manual.manual.service.impl;

import com.manual.manual.common.ErrorCode;
import com.manual.manual.config.OrderProperties;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.OrderMapper;
import com.manual.manual.model.vo.order.AdminOrderListItemVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import com.manual.manual.service.AdminOrderService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Resource
    private UserService userService;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderProperties orderProperties;

    @Override
    public List<AdminOrderListItemVO> listAdminOrders(String keyword,
                                                      Integer orderStatus,
                                                      Integer payStatus,
                                                      HttpServletRequest request) {
        userService.getAdminLoginUser(request);
        return defaultList(orderMapper.selectAdminOrders(trim(keyword), orderStatus, payStatus));
    }

    @Override
    public OrderDetailVO getAdminOrder(Long orderId, HttpServletRequest request) {
        userService.getAdminLoginUser(request);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Order id is invalid");
        }
        OrderDetailVO detailVO = orderMapper.selectAdminOrderDetail(orderId, orderProperties.getTimeoutMinutes());
        if (detailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Order not found");
        }
        detailVO.setItem(orderMapper.selectOrderItem(orderId));
        return detailVO;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
