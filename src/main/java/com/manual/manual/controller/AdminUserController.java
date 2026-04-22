package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.user.AdminUserCreateRequest;
import com.manual.manual.model.dto.user.AdminUserUpdateRequest;
import com.manual.manual.model.vo.user.AdminUserVO;
import com.manual.manual.service.UserService;
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
@RequestMapping("/admin/users")
public class AdminUserController {

    @Resource
    private UserService userService;

    @GetMapping
    public BaseResponse<List<AdminUserVO>> listUsers(@RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) String userRole,
                                                     @RequestParam(required = false) Integer userStatus,
                                                     HttpServletRequest request) {
        return ResultUtils.success(userService.listAdminUsers(keyword, userRole, userStatus, request), "获取用户列表成功");
    }

    @GetMapping("/{id}")
    public BaseResponse<AdminUserVO> getUserDetail(@PathVariable Long id, HttpServletRequest request) {
        return ResultUtils.success(userService.getAdminUser(id, request), "获取用户详情成功");
    }

    @PostMapping
    public BaseResponse<Long> createUser(@RequestBody AdminUserCreateRequest createRequest, HttpServletRequest request) {
        return ResultUtils.success(userService.createAdminUser(createRequest, request), "创建用户成功");
    }

    @PutMapping("/{id}")
    public BaseResponse<Boolean> updateUser(@PathVariable Long id,
                                            @RequestBody AdminUserUpdateRequest updateRequest,
                                            HttpServletRequest request) {
        return ResultUtils.success(userService.updateAdminUser(id, updateRequest, request), "更新用户成功");
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        return ResultUtils.success(userService.deleteAdminUser(id, request), "删除用户成功");
    }
}
