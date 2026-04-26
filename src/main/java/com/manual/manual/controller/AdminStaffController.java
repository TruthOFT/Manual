package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.staff.AdminStaffSaveRequest;
import com.manual.manual.model.vo.staff.AdminStaffVO;
import com.manual.manual.service.AdminStaffService;
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
@RequestMapping("/admin/staff")
public class AdminStaffController {

    @Resource
    private AdminStaffService adminStaffService;

    @GetMapping
    public BaseResponse<List<AdminStaffVO>> listStaff(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Integer userStatus,
                                                      @RequestParam(required = false) Integer staffStatus,
                                                      HttpServletRequest request) {
        return ResultUtils.success(adminStaffService.listStaff(keyword, userStatus, staffStatus, request), "List staff success");
    }

    @GetMapping("/{userId}")
    public BaseResponse<AdminStaffVO> getStaff(@PathVariable Long userId, HttpServletRequest request) {
        return ResultUtils.success(adminStaffService.getStaff(userId, request), "Get staff success");
    }

    @PostMapping
    public BaseResponse<Long> createStaff(@RequestBody AdminStaffSaveRequest saveRequest, HttpServletRequest request) {
        return ResultUtils.success(adminStaffService.createStaff(saveRequest, request), "Create staff success");
    }

    @PutMapping("/{userId}")
    public BaseResponse<Boolean> updateStaff(@PathVariable Long userId,
                                             @RequestBody AdminStaffSaveRequest saveRequest,
                                             HttpServletRequest request) {
        return ResultUtils.success(adminStaffService.updateStaff(userId, saveRequest, request), "Update staff success");
    }

    @DeleteMapping("/{userId}")
    public BaseResponse<Boolean> deleteStaff(@PathVariable Long userId, HttpServletRequest request) {
        return ResultUtils.success(adminStaffService.deleteStaff(userId, request), "Delete staff success");
    }
}
