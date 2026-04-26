package com.manual.manual.service;

import com.manual.manual.model.dto.customer.AdminCustomerSaveRequest;
import com.manual.manual.model.vo.customer.AdminCustomerVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminCustomerService {

    List<AdminCustomerVO> listCustomers(String keyword, Integer userStatus, HttpServletRequest request);

    AdminCustomerVO getCustomer(Long userId, HttpServletRequest request);

    Long createCustomer(AdminCustomerSaveRequest saveRequest, HttpServletRequest request);

    Boolean updateCustomer(Long userId, AdminCustomerSaveRequest saveRequest, HttpServletRequest request);

    Boolean deleteCustomer(Long userId, HttpServletRequest request);
}
