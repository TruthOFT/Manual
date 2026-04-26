package com.manual.manual.service;

import com.manual.manual.model.dto.staff.AdminStaffSaveRequest;
import com.manual.manual.model.vo.staff.AdminStaffVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminStaffService {

    List<AdminStaffVO> listStaff(String keyword, Integer userStatus, Integer staffStatus, HttpServletRequest request);

    AdminStaffVO getStaff(Long userId, HttpServletRequest request);

    Long createStaff(AdminStaffSaveRequest saveRequest, HttpServletRequest request);

    Boolean updateStaff(Long userId, AdminStaffSaveRequest saveRequest, HttpServletRequest request);

    Boolean deleteStaff(Long userId, HttpServletRequest request);
}
