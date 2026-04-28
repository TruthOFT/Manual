package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.model.dto.user.UserLoginRequest;
import com.manual.manual.model.dto.user.UserRechargeRequest;
import com.manual.manual.model.dto.user.UserRegisterRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(
                userRegisterRequest.getUserAccount(),
                userRegisterRequest.getUserPassword(),
                userRegisterRequest.getCheckPassword()
        );
        return ResultUtils.success(result, "注册成功");
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userService.userLogin(
                userLoginRequest.getUserAccount(),
                userLoginRequest.getUserPassword(),
                userLoginRequest.getRememberMe(),
                request,
                response
        );
        return ResultUtils.success(loginUserVO, "登录成功");
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogout(request), "退出登录成功");
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser), "获取登录信息成功");
    }

    @PostMapping("/recharge")
    public BaseResponse<LoginUserVO> recharge(@RequestBody UserRechargeRequest rechargeRequest, HttpServletRequest request) {
        return ResultUtils.success(userService.rechargeBalance(rechargeRequest, request), "充值成功");
    }

    @PostMapping("/upload/avatar")
    public BaseResponse<LoginUserVO> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择头像文件");
        }
        LoginUserVO updatedUser = userService.uploadAvatar(file, request, response);
        return ResultUtils.success(updatedUser, "头像上传成功");
    }
}
