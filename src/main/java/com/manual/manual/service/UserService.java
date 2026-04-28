package com.manual.manual.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.manual.manual.model.dto.user.AdminUserCreateRequest;
import com.manual.manual.model.dto.user.AdminUserUpdateRequest;
import com.manual.manual.model.dto.user.UserRechargeRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.model.vo.user.AdminUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, Boolean rememberMe,
                          HttpServletRequest request, HttpServletResponse response);

    LoginUserVO adminLogin(String userAccount, String userPassword, Boolean rememberMe,
                           HttpServletRequest request, HttpServletResponse response);

    boolean userLogout(HttpServletRequest request);

    boolean adminLogout(HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    User getAdminLoginUser(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    List<AdminUserVO> listAdminUsers(String keyword, String userRole, Integer userStatus, HttpServletRequest request);

    AdminUserVO getAdminUser(Long id, HttpServletRequest request);

    long createAdminUser(AdminUserCreateRequest createRequest, HttpServletRequest request);

    boolean updateAdminUser(Long id, AdminUserUpdateRequest updateRequest, HttpServletRequest request);

    boolean deleteAdminUser(Long id, HttpServletRequest request);

    LoginUserVO rechargeBalance(UserRechargeRequest rechargeRequest, HttpServletRequest request);

    boolean updateUserRole(Long userId, String userRole);

    boolean updateUserBalance(Long userId, BigDecimal balance);

    LoginUserVO uploadAvatar(MultipartFile file, HttpServletRequest request, HttpServletResponse response);
}