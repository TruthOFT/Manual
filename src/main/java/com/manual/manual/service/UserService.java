package com.manual.manual.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, Boolean rememberMe,
                          HttpServletRequest request, HttpServletResponse response);

    boolean userLogout(HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);
}
