package com.manual.manual.service;

import com.manual.manual.model.vo.dashboard.AdminDashboardOverviewVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminDashboardService {

    AdminDashboardOverviewVO getOverview(HttpServletRequest request);
}