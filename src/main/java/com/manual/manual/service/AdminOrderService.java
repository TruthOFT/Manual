package com.manual.manual.service;

import com.manual.manual.model.vo.order.AdminOrderListItemVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminOrderService {

    List<AdminOrderListItemVO> listAdminOrders(String keyword, Integer orderStatus, Integer payStatus, HttpServletRequest request);

    OrderDetailVO getAdminOrder(Long orderId, HttpServletRequest request);
}
