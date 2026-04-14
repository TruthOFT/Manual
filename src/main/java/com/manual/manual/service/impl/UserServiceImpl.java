package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.UserMapper;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.util.Date;

import static com.manual.manual.constant.UserConstant.DEFAULT_ROLE;
import static com.manual.manual.constant.UserConstant.SALT;
import static com.manual.manual.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        String safeUserAccount = trim(userAccount);
        String safeUserPassword = trim(userPassword);
        String safeCheckPassword = trim(checkPassword);
        validateRegisterParams(safeUserAccount, safeUserPassword, safeCheckPassword);
        synchronized (safeUserAccount.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", safeUserAccount);
            Long count = userMapper.selectCount(queryWrapper);
            if (count != null && count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account already exists");
            }
            User user = new User();
            user.setUserAccount(safeUserAccount);
            user.setUserPassword(encryptPassword(safeUserPassword));
            user.setUserName("手作用户");
            user.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=240&q=80");
            user.setUserRole(DEFAULT_ROLE);
            user.setUserStatus(0);
            user.setBalance(BigDecimal.ZERO);
            boolean saved = this.save(user);
            if (!saved || user.getId() == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Register failed");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, Boolean rememberMe,
                                 HttpServletRequest request, HttpServletResponse response) {
        String safeUserAccount = trim(userAccount);
        String safeUserPassword = trim(userPassword);
        if (!hasText(safeUserAccount) || !hasText(safeUserPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account or password is empty");
        }
        if (safeUserAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account is too short");
        }
        if (safeUserPassword.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password is too short");
        }
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("userAccount", safeUserAccount)
                .last("limit 1"));
        if (user == null || !matchPassword(user.getUserPassword(), safeUserPassword)) {
            log.info("user login failed, account or password mismatch, userAccount={}", safeUserAccount);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account or password is incorrect");
        }
        user.setLastLoginTime(new Date());
        this.updateById(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        if (Boolean.TRUE.equals(rememberMe)) {
            int maxAgeSeconds = 7 * 24 * 60 * 60;
            request.getSession().setMaxInactiveInterval(maxAgeSeconds);
            Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(maxAgeSeconds);
            response.addCookie(cookie);
        }
        return getLoginUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "User is not logged in");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (!(userObj instanceof User sessionUser) || sessionUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User currentUser = this.getById(sessionUser.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setId(user.getId());
        loginUserVO.setUsername(user.getUserName());
        loginUserVO.setUserAccount(user.getUserAccount());
        loginUserVO.setAvatarUrl(user.getAvatarUrl());
        loginUserVO.setPhone(user.getPhone());
        loginUserVO.setEmail(user.getEmail());
        loginUserVO.setGender(user.getGender());
        loginUserVO.setUserRole(user.getUserRole());
        loginUserVO.setUserStatus(user.getUserStatus());
        loginUserVO.setBalance(user.getBalance());
        loginUserVO.setLastLoginTime(user.getLastLoginTime());
        loginUserVO.setCreateTime(user.getCreateTime());
        loginUserVO.setUpdateTime(user.getUpdateTime());
        return loginUserVO;
    }

    private void validateRegisterParams(String userAccount, String userPassword, String checkPassword) {
        if (!hasText(userAccount) || !hasText(userPassword) || !hasText(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request params cannot be empty");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account length must be at least 4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password length must be at least 8");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "The two passwords do not match");
        }
    }

    private boolean matchPassword(String storedPassword, String userPassword) {
        if (!hasText(storedPassword)) {
            return false;
        }
        String saltedPassword = encryptPassword(userPassword);
        if (storedPassword.equals(saltedPassword)) {
            return true;
        }
        String legacyPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());
        return storedPassword.equals(legacyPassword);
    }

    private String encryptPassword(String userPassword) {
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
