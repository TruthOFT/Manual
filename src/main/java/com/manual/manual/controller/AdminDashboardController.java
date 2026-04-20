package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.dashboard.AdminDashboardOverviewVO;
import com.manual.manual.service.AdminDashboardService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Resource
    private AdminDashboardService adminDashboardService;

    @GetMapping("/overview")
    public BaseResponse<AdminDashboardOverviewVO> getOverview(HttpServletRequest request) {
        return ResultUtils.success(adminDashboardService.getOverview(request));
    }
}