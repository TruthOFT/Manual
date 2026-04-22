package com.manual.manual.service;

import com.manual.manual.model.dto.order.OrderCreateRequest;
import com.manual.manual.model.dto.order.UserAddressSaveRequest;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import com.manual.manual.model.vo.order.UserAddressVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {

    List<UserAddressVO> listCurrentUserAddresses(HttpServletRequest request);

    UserAddressVO createCurrentUserAddress(UserAddressSaveRequest saveRequest, HttpServletRequest request);

    UserAddressVO updateCurrentUserAddress(Long addressId, UserAddressSaveRequest saveRequest, HttpServletRequest request);

    Boolean deleteCurrentUserAddress(Long addressId, HttpServletRequest request);

    List<OrderDetailVO> listCurrentUserOrders(Integer orderStatus, HttpServletRequest request);

    OrderDetailVO getCurrentUserOrder(Long orderId, HttpServletRequest request);

    OrderDetailVO createOrder(OrderCreateRequest createRequest, HttpServletRequest request);

    LoginUserVO payOrder(Long orderId, HttpServletRequest request);

    Boolean cancelOrder(Long orderId, HttpServletRequest request);

    void cancelTimeoutOrder(Long orderId);
}
