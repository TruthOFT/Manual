package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.order.AdminOrderListItemVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import com.manual.manual.service.AdminOrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Resource
    private AdminOrderService adminOrderService;

    @GetMapping
    public BaseResponse<List<AdminOrderListItemVO>> listOrders(@RequestParam(required = false) String keyword,
                                                               @RequestParam(required = false) Integer orderStatus,
                                                               @RequestParam(required = false) Integer payStatus,
                                                               HttpServletRequest request) {
        return ResultUtils.success(
                adminOrderService.listAdminOrders(keyword, orderStatus, payStatus, request),
                "获取订单列表成功"
        );
    }

    @GetMapping("/{orderId}")
    public BaseResponse<OrderDetailVO> getOrder(@PathVariable Long orderId, HttpServletRequest request) {
        return ResultUtils.success(adminOrderService.getAdminOrder(orderId, request), "获取订单详情成功");
    }
}
