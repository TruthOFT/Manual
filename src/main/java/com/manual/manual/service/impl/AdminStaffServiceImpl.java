package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.AdminStaffMapper;
import com.manual.manual.model.dto.staff.AdminStaffSaveRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.staff.AdminStaffVO;
import com.manual.manual.service.AdminStaffService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class AdminStaffServiceImpl implements AdminStaffService {

    @Resource
    private AdminStaffMapper adminStaffMapper;

    @Resource
    private UserService userService;

    @Override
    public List<AdminStaffVO> listStaff(String keyword, Integer userStatus, Integer staffStatus, HttpServletRequest request) {
        requireAdmin(request);
        List<AdminStaffVO> staff = adminStaffMapper.selectStaff(trim(keyword), userStatus, staffStatus);
        return staff == null ? Collections.emptyList() : staff;
    }

    @Override
    public AdminStaffVO getStaff(Long userId, HttpServletRequest request) {
        requireAdmin(request);
        return requireStaff(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStaff(AdminStaffSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        sanitizeCreate(saveRequest);
        Long userId = IdWorker.getId();
        Long profileId = IdWorker.getId();
        saveRequest.setStaffNo(String.valueOf(IdWorker.getId()));
        validateUnique(null, saveRequest.getUserAccount(), saveRequest.getPhone(), saveRequest.getEmail(), saveRequest.getStaffNo());
        if (adminStaffMapper.insertStaffUser(userId, encryptPassword(saveRequest.getUserPassword()), saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create staff failed");
        }
        if (adminStaffMapper.insertStaffProfile(profileId, userId, saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create staff profile failed");
        }
        return userId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStaff(Long userId, AdminStaffSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        AdminStaffVO staff = requireStaff(userId);
        sanitizeUpdate(saveRequest);
        saveRequest.setStaffNo(staff.getStaffNo());
        validateUnique(userId, null, saveRequest.getPhone(), saveRequest.getEmail(), saveRequest.getStaffNo());
        String password = hasText(saveRequest.getUserPassword()) ? encryptPassword(saveRequest.getUserPassword()) : null;
        if (adminStaffMapper.updateStaffUser(userId, password, saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update staff failed");
        }
        if (adminStaffMapper.updateStaffProfile(userId, saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update staff profile failed");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteStaff(Long userId, HttpServletRequest request) {
        requireAdmin(request);
        requireStaff(userId);
        adminStaffMapper.deleteStaffProfile(userId);
        return adminStaffMapper.deleteStaffUser(userId) > 0;
    }

    private void sanitizeCreate(AdminStaffSaveRequest request) {
        sanitizeBase(request);
        request.setUserAccount(requiredTrim(request.getUserAccount(), "Account is required"));
        request.setUserPassword(requiredTrim(request.getUserPassword(), "Password is required"));
        if (request.getUserAccount().length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account length must be at least 4");
        }
        if (request.getUserPassword().length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password length must be at least 8");
        }
    }

    private void sanitizeUpdate(AdminStaffSaveRequest request) {
        sanitizeBase(request);
        request.setUserPassword(trim(request.getUserPassword()));
        if (hasText(request.getUserPassword()) && request.getUserPassword().length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password length must be at least 8");
        }
    }

    private void sanitizeBase(AdminStaffSaveRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request body is required");
        }
        request.setUserName(defaultString(trim(request.getUserName()), "Staff"));
        request.setPhone(trim(request.getPhone()));
        request.setEmail(trim(request.getEmail()));
        request.setGender(defaultInteger(request.getGender(), 0));
        request.setUserStatus(defaultInteger(request.getUserStatus(), 0));
        request.setStaffName(requiredTrim(request.getStaffName(), "Staff name is required"));
        request.setStaffNo(trim(request.getStaffNo()));
        request.setPositionName(trim(request.getPositionName()));
        request.setSalary(defaultSalary(request.getSalary()));
        request.setEntryTime(trim(request.getEntryTime()));
        request.setStaffStatus(defaultInteger(request.getStaffStatus(), 1));
    }

    private AdminStaffVO requireStaff(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Staff id is invalid");
        }
        AdminStaffVO staff = adminStaffMapper.selectStaffDetail(userId);
        if (staff == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Staff not found");
        }
        return staff;
    }

    private void validateUnique(Long userId, String account, String phone, String email, String staffNo) {
        if (hasText(account) && adminStaffMapper.countAccount(account, userId) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account already exists");
        }
        if (hasText(phone) && adminStaffMapper.countPhone(phone, userId) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Phone already exists");
        }
        if (hasText(email) && adminStaffMapper.countEmail(email, userId) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Email already exists");
        }
        if (hasText(staffNo) && adminStaffMapper.countStaffNo(staffNo, userId) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Staff no already exists");
        }
    }

    private User requireAdmin(HttpServletRequest request) {
        User loginUser = userService.getAdminLoginUser(request);
        if (loginUser == null || !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return loginUser;
    }

    private String encryptPassword(String value) {
        return DigestUtils.md5DigestAsHex((UserConstant.SALT + value).getBytes());
    }

    private String requiredTrim(String value, String message) {
        String trimmed = trim(value);
        if (trimmed == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
        return trimmed;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultString(String value, String defaultValue) {
        return hasText(value) ? value : defaultValue;
    }

    private Integer defaultInteger(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    private BigDecimal defaultSalary(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Salary cannot be negative");
        }
        return value;
    }
}
