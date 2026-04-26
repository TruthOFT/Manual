package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.AdminCustomerMapper;
import com.manual.manual.model.dto.customer.AdminCustomerSaveRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.customer.AdminCustomerVO;
import com.manual.manual.service.AdminCustomerService;
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
public class AdminCustomerServiceImpl implements AdminCustomerService {

    @Resource
    private AdminCustomerMapper adminCustomerMapper;

    @Resource
    private UserService userService;

    @Override
    public List<AdminCustomerVO> listCustomers(String keyword, Integer userStatus, HttpServletRequest request) {
        requireAdmin(request);
        List<AdminCustomerVO> customers = adminCustomerMapper.selectCustomers(trim(keyword), userStatus);
        return customers == null ? Collections.emptyList() : customers;
    }

    @Override
    public AdminCustomerVO getCustomer(Long userId, HttpServletRequest request) {
        requireAdmin(request);
        return requireCustomer(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCustomer(AdminCustomerSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        sanitizeCreate(saveRequest);
        validateUnique(null, saveRequest.getUserAccount(), saveRequest.getPhone(), saveRequest.getEmail());
        Long userId = IdWorker.getId();
        Long profileId = IdWorker.getId();
        if (adminCustomerMapper.insertCustomerUser(userId, encryptPassword(saveRequest.getUserPassword()), saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create customer failed");
        }
        if (adminCustomerMapper.insertCustomerProfile(profileId, userId, saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create customer profile failed");
        }
        return userId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCustomer(Long userId, AdminCustomerSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        requireCustomer(userId);
        sanitizeUpdate(saveRequest);
        validateUnique(userId, null, saveRequest.getPhone(), saveRequest.getEmail());
        String password = hasText(saveRequest.getUserPassword()) ? encryptPassword(saveRequest.getUserPassword()) : null;
        if (adminCustomerMapper.updateCustomerUser(userId, password, saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update customer failed");
        }
        if (adminCustomerMapper.updateCustomerProfile(userId, saveRequest) <= 0) {
            adminCustomerMapper.insertCustomerProfile(IdWorker.getId(), userId, saveRequest);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCustomer(Long userId, HttpServletRequest request) {
        requireAdmin(request);
        requireCustomer(userId);
        adminCustomerMapper.deleteCustomerProfile(userId);
        return adminCustomerMapper.deleteCustomerUser(userId) > 0;
    }

    private void sanitizeCreate(AdminCustomerSaveRequest request) {
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

    private void sanitizeUpdate(AdminCustomerSaveRequest request) {
        sanitizeBase(request);
        request.setUserPassword(trim(request.getUserPassword()));
        if (hasText(request.getUserPassword()) && request.getUserPassword().length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Password length must be at least 8");
        }
    }

    private void sanitizeBase(AdminCustomerSaveRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request body is required");
        }
        request.setUserName(defaultString(trim(request.getUserName()), "Customer"));
        request.setPhone(trim(request.getPhone()));
        request.setEmail(trim(request.getEmail()));
        request.setGender(defaultInteger(request.getGender(), 0));
        request.setUserStatus(defaultInteger(request.getUserStatus(), 0));
        request.setCustomerLevel(nonNegative(request.getCustomerLevel(), 1));
        request.setPoints(nonNegative(request.getPoints(), 0));
        request.setTotalAmount(nonNegativeAmount(request.getTotalAmount()));
        request.setOrderCount(nonNegative(request.getOrderCount(), 0));
        request.setPreferenceTags(trim(request.getPreferenceTags()));
        request.setLastPurchaseTime(trim(request.getLastPurchaseTime()));
    }

    private AdminCustomerVO requireCustomer(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Customer id is invalid");
        }
        AdminCustomerVO customer = adminCustomerMapper.selectCustomer(userId);
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Customer not found");
        }
        return customer;
    }

    private void validateUnique(Long userId, String account, String phone, String email) {
        if (hasText(account) && adminCustomerMapper.countAccount(account, userId) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Account already exists");
        }
        if (hasText(phone) && adminCustomerMapper.countPhone(phone, userId) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Phone already exists");
        }
        if (hasText(email) && adminCustomerMapper.countEmail(email, userId) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Email already exists");
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

    private Integer nonNegative(Integer value, Integer defaultValue) {
        int result = value == null ? defaultValue : value;
        if (result < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Number fields must be greater than or equal to 0");
        }
        return result;
    }

    private BigDecimal nonNegativeAmount(BigDecimal value) {
        BigDecimal result = value == null ? BigDecimal.ZERO : value;
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Amount must be greater than or equal to 0");
        }
        return result;
    }
}
