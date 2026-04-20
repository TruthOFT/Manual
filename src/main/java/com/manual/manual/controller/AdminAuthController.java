package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.model.dto.user.UserLoginRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> adminLogin(@RequestBody UserLoginRequest userLoginRequest,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.adminLogin(
                userLoginRequest.getUserAccount(),
                userLoginRequest.getUserPassword(),
                userLoginRequest.getRememberMe(),
                request,
                response
        );
        return ResultUtils.success(loginUserVO);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> adminLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.adminLogout(request));
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getAdminLoginUser(HttpServletRequest request) {
        User loginUser = userService.getAdminLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }
}
