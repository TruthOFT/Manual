package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.order.OrderCreateRequest;
import com.manual.manual.model.dto.order.UserAddressSaveRequest;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import com.manual.manual.model.vo.order.UserAddressVO;
import com.manual.manual.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/addresses")
    public BaseResponse<List<UserAddressVO>> listAddresses(HttpServletRequest request) {
        return ResultUtils.success(orderService.listCurrentUserAddresses(request), "获取收货地址成功");
    }

    @PostMapping("/addresses")
    public BaseResponse<UserAddressVO> createAddress(@RequestBody UserAddressSaveRequest saveRequest,
                                                     HttpServletRequest request) {
        return ResultUtils.success(orderService.createCurrentUserAddress(saveRequest, request), "收货地址已新增");
    }

    @PutMapping("/addresses/{addressId}")
    public BaseResponse<UserAddressVO> updateAddress(@PathVariable Long addressId,
                                                     @RequestBody UserAddressSaveRequest saveRequest,
                                                     HttpServletRequest request) {
        return ResultUtils.success(orderService.updateCurrentUserAddress(addressId, saveRequest, request), "收货地址已更新");
    }

    @DeleteMapping("/addresses/{addressId}")
    public BaseResponse<Boolean> deleteAddress(@PathVariable Long addressId, HttpServletRequest request) {
        return ResultUtils.success(orderService.deleteCurrentUserAddress(addressId, request), "收货地址已删除");
    }

    @GetMapping
    public BaseResponse<List<OrderDetailVO>> listOrders(@RequestParam(required = false) Integer orderStatus,
                                                        HttpServletRequest request) {
        return ResultUtils.success(orderService.listCurrentUserOrders(orderStatus, request), "获取订单列表成功");
    }

    @GetMapping("/{orderId}")
    public BaseResponse<OrderDetailVO> getOrder(@PathVariable Long orderId, HttpServletRequest request) {
        return ResultUtils.success(orderService.getCurrentUserOrder(orderId, request), "获取订单详情成功");
    }

    @PostMapping
    public BaseResponse<OrderDetailVO> createOrder(@RequestBody OrderCreateRequest createRequest,
                                                   HttpServletRequest request) {
        return ResultUtils.success(orderService.createOrder(createRequest, request), "订单创建成功");
    }

    @PostMapping("/{orderId}/pay")
    public BaseResponse<LoginUserVO> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        return ResultUtils.success(orderService.payOrder(orderId, request), "支付成功");
    }

    @PostMapping("/{orderId}/cancel")
    public BaseResponse<Boolean> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        return ResultUtils.success(orderService.cancelOrder(orderId, request), "订单已取消");
    }
}
