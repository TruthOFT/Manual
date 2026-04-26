package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.customer.AdminCustomerSaveRequest;
import com.manual.manual.model.vo.customer.AdminCustomerVO;
import com.manual.manual.service.AdminCustomerService;
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
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    @Resource
    private AdminCustomerService adminCustomerService;

    @GetMapping
    public BaseResponse<List<AdminCustomerVO>> listCustomers(@RequestParam(required = false) String keyword,
                                                             @RequestParam(required = false) Integer userStatus,
                                                             HttpServletRequest request) {
        return ResultUtils.success(adminCustomerService.listCustomers(keyword, userStatus, request), "List customers success");
    }

    @GetMapping("/{userId}")
    public BaseResponse<AdminCustomerVO> getCustomer(@PathVariable Long userId, HttpServletRequest request) {
        return ResultUtils.success(adminCustomerService.getCustomer(userId, request), "Get customer success");
    }

    @PostMapping
    public BaseResponse<Long> createCustomer(@RequestBody AdminCustomerSaveRequest saveRequest, HttpServletRequest request) {
        return ResultUtils.success(adminCustomerService.createCustomer(saveRequest, request), "Create customer success");
    }

    @PutMapping("/{userId}")
    public BaseResponse<Boolean> updateCustomer(@PathVariable Long userId,
                                                @RequestBody AdminCustomerSaveRequest saveRequest,
                                                HttpServletRequest request) {
        return ResultUtils.success(adminCustomerService.updateCustomer(userId, saveRequest, request), "Update customer success");
    }

    @DeleteMapping("/{userId}")
    public BaseResponse<Boolean> deleteCustomer(@PathVariable Long userId, HttpServletRequest request) {
        return ResultUtils.success(adminCustomerService.deleteCustomer(userId, request), "Delete customer success");
    }
}
