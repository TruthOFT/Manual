package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.UserMapper;
import com.manual.manual.model.dto.user.AdminUserCreateRequest;
import com.manual.manual.model.dto.user.AdminUserUpdateRequest;
import com.manual.manual.model.dto.user.UserRechargeRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.model.vo.user.AdminUserVO;
import com.manual.manual.service.RecommendationService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.manual.manual.constant.UserConstant.ADMIN_LOGIN_STATE;
import static com.manual.manual.constant.UserConstant.ADMIN_ROLE;
import static com.manual.manual.constant.UserConstant.DEFAULT_ROLE;
import static com.manual.manual.constant.UserConstant.SALT;
import static com.manual.manual.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    @Lazy
    private RecommendationService recommendationService;

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
        User user = validateLoginCredentials(userAccount, userPassword);
        persistLoginState(user, rememberMe, request, response, USER_LOGIN_STATE);
        refreshRecommendationsAfterLogin(user);
        return getLoginUserVO(user);
    }

    @Override
    public LoginUserVO adminLogin(String userAccount, String userPassword, Boolean rememberMe,
                                  HttpServletRequest request, HttpServletResponse response) {
        User user = validateLoginCredentials(userAccount, userPassword);
        if (!ADMIN_ROLE.equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Only admin users can log in to admin console");
        }
        persistLoginState(user, rememberMe, request, response, ADMIN_LOGIN_STATE);
        return getLoginUserVO(user);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        return clearLoginState(request, USER_LOGIN_STATE, "User is not logged in");
    }

    @Override
    public boolean adminLogout(HttpServletRequest request) {
        return clearLoginState(request, ADMIN_LOGIN_STATE, "Admin is not logged in");
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        return getSessionUser(request, USER_LOGIN_STATE);
    }

    @Override
    public User getAdminLoginUser(HttpServletRequest request) {
        User adminUser = getSessionUser(request, ADMIN_LOGIN_STATE);
        if (!ADMIN_ROLE.equals(adminUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Only admin users can access this endpoint");
        }
        return adminUser;
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

    @Override
    public List<AdminUserVO> listAdminUsers(String keyword, String userRole, Integer userStatus, HttpServletRequest request) {
        requireAdmin(request);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        String safeKeyword = trim(keyword);
        String safeUserRole = trim(userRole);
        if (hasText(safeKeyword)) {
            queryWrapper.and(wrapper -> wrapper.like("userAccount", safeKeyword).or().like("userName", safeKeyword));
        }
        if (hasText(safeUserRole)) {
            queryWrapper.eq("userRole", safeUserRole);
        }
        if (userStatus != null) {
            queryWrapper.eq("userStatus", userStatus);
        }
        queryWrapper.orderByDesc("updateTime");
        return this.list(queryWrapper).stream().map(this::getAdminUserVO).collect(Collectors.toList());
    }

    @Override
    public AdminUserVO getAdminUser(Long id, HttpServletRequest request) {
        requireAdmin(request);
        User user = getExistingUser(id);
        return getAdminUserVO(user);
    }

    @Override
    public long createAdminUser(AdminUserCreateRequest createRequest, HttpServletRequest request) {
        requireAdmin(request);
        if (createRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = trim(createRequest.getUserAccount());
        String userPassword = trim(createRequest.getUserPassword());
        if (!hasText(userAccount) || userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account length must be at least 4");
        }
        if (!hasText(userPassword) || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password length must be at least 8");
        }
        validateUserUniqueness(null, userAccount, trim(createRequest.getPhone()), trim(createRequest.getEmail()));
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword(userPassword));
        user.setUserName(defaultString(trim(createRequest.getUserName()), "平台用户"));
        user.setAvatarUrl(trim(createRequest.getAvatarUrl()));
        user.setPhone(trim(createRequest.getPhone()));
        user.setEmail(trim(createRequest.getEmail()));
        user.setGender(defaultInteger(createRequest.getGender(), 0));
        user.setUserRole(defaultString(trim(createRequest.getUserRole()), DEFAULT_ROLE));
        user.setUserStatus(defaultInteger(createRequest.getUserStatus(), 0));
        user.setBalance(createRequest.getBalance() == null ? BigDecimal.ZERO : createRequest.getBalance());
        boolean saved = this.save(user);
        if (!saved || user.getId() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create user failed");
        }
        return user.getId();
    }

    @Override
    public boolean updateAdminUser(Long id, AdminUserUpdateRequest updateRequest, HttpServletRequest request) {
        requireAdmin(request);
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = getExistingUser(id);
        String phone = trim(updateRequest.getPhone());
        String email = trim(updateRequest.getEmail());
        validateUserUniqueness(id, user.getUserAccount(), phone, email);
        String nextPassword = trim(updateRequest.getUserPassword());
        if (hasText(nextPassword)) {
            if (nextPassword.length() < 8) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password length must be at least 8");
            }
            user.setUserPassword(encryptPassword(nextPassword));
        }
        user.setUserName(defaultString(trim(updateRequest.getUserName()), user.getUserName()));
        if (updateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(updateRequest.getAvatarUrl().trim());
        }
        user.setPhone(phone);
        user.setEmail(email);
        user.setGender(defaultInteger(updateRequest.getGender(), user.getGender()));
        user.setUserRole(defaultString(trim(updateRequest.getUserRole()), user.getUserRole()));
        user.setUserStatus(defaultInteger(updateRequest.getUserStatus(), user.getUserStatus()));
        user.setBalance(updateRequest.getBalance() == null ? user.getBalance() : updateRequest.getBalance());
        return this.updateById(user);
    }

    @Override
    public boolean deleteAdminUser(Long id, HttpServletRequest request) {
        User adminUser = requireAdmin(request);
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User id is invalid");
        }
        if (id.equals(adminUser.getId())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Admin cannot delete current login user");
        }
        getExistingUser(id);
        return this.removeById(id);
    }

    @Override
    public LoginUserVO rechargeBalance(UserRechargeRequest rechargeRequest, HttpServletRequest request) {
        if (rechargeRequest == null || rechargeRequest.getAmount() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入充值金额");
        }
        if (rechargeRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "充值金额必须大于 0");
        }
        User loginUser = getLoginUser(request);
        loginUser.setBalance(defaultAmount(loginUser.getBalance()).add(rechargeRequest.getAmount()));
        if (!this.updateById(loginUser)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "充值失败");
        }
        return getLoginUserVO(this.getById(loginUser.getId()));
    }

    @Override
    public boolean updateUserRole(Long userId, String userRole) {
        User user = getExistingUser(userId);
        user.setUserRole(userRole);
        return this.updateById(user);
    }

    @Override
    public boolean updateUserBalance(Long userId, BigDecimal balance) {
        User user = getExistingUser(userId);
        user.setBalance(defaultAmount(balance));
        return this.updateById(user);
    }

    private User validateLoginCredentials(String userAccount, String userPassword) {
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
        return user;
    }

    private void persistLoginState(User user,
                                   Boolean rememberMe,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   String loginStateKey) {
        request.getSession().setAttribute(loginStateKey, user);
        if (Boolean.TRUE.equals(rememberMe)) {
            int maxAgeSeconds = 7 * 24 * 60 * 60;
            request.getSession().setMaxInactiveInterval(maxAgeSeconds);
            Cookie cookie = new Cookie("JSESSIONID", request.getSession().getId());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(maxAgeSeconds);
            response.addCookie(cookie);
        }
    }

    private void refreshRecommendationsAfterLogin(User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        try {
            recommendationService.refreshUserRecommendations(user.getId());
        } catch (Exception exception) {
            log.warn("refresh user recommendations failed, userId={}", user.getId(), exception);
        }
    }

    private boolean clearLoginState(HttpServletRequest request, String loginStateKey, String errorMessage) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (request.getSession().getAttribute(loginStateKey) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, errorMessage);
        }
        request.getSession().removeAttribute(loginStateKey);
        return true;
    }

    private User getSessionUser(HttpServletRequest request, String loginStateKey) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userObj = request.getSession().getAttribute(loginStateKey);
        if (!(userObj instanceof User sessionUser) || sessionUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        User currentUser = this.getById(sessionUser.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
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

    private User requireAdmin(HttpServletRequest request) {
        return getAdminLoginUser(request);
    }

    private User getExistingUser(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "User id is invalid");
        }
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "User not found");
        }
        return user;
    }

    private AdminUserVO getAdminUserVO(User user) {
        if (user == null) {
            return null;
        }
        AdminUserVO adminUserVO = new AdminUserVO();
        adminUserVO.setId(user.getId());
        adminUserVO.setUserAccount(user.getUserAccount());
        adminUserVO.setUserName(user.getUserName());
        adminUserVO.setAvatarUrl(user.getAvatarUrl());
        adminUserVO.setPhone(user.getPhone());
        adminUserVO.setEmail(user.getEmail());
        adminUserVO.setGender(user.getGender());
        adminUserVO.setUserRole(user.getUserRole());
        adminUserVO.setUserStatus(user.getUserStatus());
        adminUserVO.setBalance(user.getBalance());
        adminUserVO.setLastLoginTime(user.getLastLoginTime());
        adminUserVO.setCreateTime(user.getCreateTime());
        adminUserVO.setUpdateTime(user.getUpdateTime());
        return adminUserVO;
    }

    private void validateUserUniqueness(Long currentUserId, String userAccount, String phone, String email) {
        assertUniqueField("userAccount", userAccount, currentUserId, "Account already exists");
        if (hasText(phone)) {
            assertUniqueField("phone", phone, currentUserId, "Phone already exists");
        }
        if (hasText(email)) {
            assertUniqueField("email", email, currentUserId, "Email already exists");
        }
    }

    private void assertUniqueField(String fieldName, String fieldValue, Long currentUserId, String message) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, fieldValue);
        if (currentUserId != null) {
            queryWrapper.ne("id", currentUserId);
        }
        Long count = userMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String defaultString(String value, String defaultValue) {
        return hasText(value) ? value : defaultValue;
    }

    private Integer defaultInteger(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
