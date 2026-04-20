package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.ArtisanApplicationMapper;
import com.manual.manual.model.dto.artisanapplication.AdminArtisanApplicationAuditRequest;
import com.manual.manual.model.dto.artisanapplication.ArtisanApplicationSubmitRequest;
import com.manual.manual.model.entity.ArtisanProfile;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationDetailVO;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationListItemVO;
import com.manual.manual.model.vo.artisanapplication.ArtisanApplicationVO;
import com.manual.manual.service.ArtisanApplicationService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ArtisanApplicationServiceImpl implements ArtisanApplicationService {

    private static final BigDecimal DEPOSIT_AMOUNT = new BigDecimal("1000.00");
    private static final int AUDIT_PENDING = 0;
    private static final int AUDIT_APPROVED = 1;
    private static final int AUDIT_REJECTED = 2;
    private static final int SHELF_DISABLED = 0;
    private static final int SHELF_ENABLED = 1;

    @Resource
    private ArtisanApplicationMapper artisanApplicationMapper;

    @Resource
    private UserService userService;

    @Override
    public ArtisanApplicationVO getCurrentApplication(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ArtisanProfile artisanProfile = getApplicationByUserId(loginUser.getId());
        if (artisanProfile == null) {
            ArtisanApplicationVO emptyVO = new ArtisanApplicationVO();
            emptyVO.setHasApplication(false);
            emptyVO.setUserId(loginUser.getId());
            emptyVO.setUserRole(loginUser.getUserRole());
            emptyVO.setQualificationImages(new ArrayList<>());
            emptyVO.setDepositAmount(DEPOSIT_AMOUNT);
            return emptyVO;
        }
        return toApplicationVO(artisanProfile, loginUser.getUserRole());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArtisanApplicationVO submitApplication(ArtisanApplicationSubmitRequest submitRequest, HttpServletRequest request) {
        if (submitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (UserConstant.ARTISAN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Current user is already an artisan");
        }
        if (!UserConstant.DEFAULT_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Only normal users can submit artisan application");
        }
        sanitizeSubmitRequest(submitRequest);
        ArtisanProfile artisanProfile = getApplicationByUserId(loginUser.getId());
        if (artisanProfile != null) {
            if (safeInteger(artisanProfile.getAuditStatus()) == AUDIT_PENDING) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Current application is pending review");
            }
            if (safeInteger(artisanProfile.getAuditStatus()) == AUDIT_APPROVED) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Current application is already approved");
            }
        }
        BigDecimal balance = defaultAmount(loginUser.getBalance());
        if (balance.compareTo(DEPOSIT_AMOUNT) < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Current balance is not enough for artisan deposit");
        }
        userService.updateUserBalance(loginUser.getId(), balance.subtract(DEPOSIT_AMOUNT));

        Date now = new Date();
        if (artisanProfile == null) {
            artisanProfile = new ArtisanProfile();
            artisanProfile.setId(IdWorker.getId());
            artisanProfile.setUserId(loginUser.getId());
            artisanProfile.setCreateTime(now);
        }
        artisanProfile.setArtisanName(submitRequest.getArtisanName());
        artisanProfile.setShopName(submitRequest.getShopName());
        artisanProfile.setArtisanAvatar(submitRequest.getArtisanAvatar());
        artisanProfile.setCoverUrl(submitRequest.getCoverUrl());
        artisanProfile.setArtisanStory(submitRequest.getArtisanStory());
        artisanProfile.setCraftCategory(submitRequest.getCraftCategory());
        artisanProfile.setOriginPlace(submitRequest.getOriginPlace());
        artisanProfile.setExperienceYears(nonNegative(submitRequest.getExperienceYears()));
        artisanProfile.setSupportCustom(normalizeBooleanNumber(submitRequest.getSupportCustom()));
        artisanProfile.setContactPhone(submitRequest.getContactPhone());
        artisanProfile.setQualificationDesc(submitRequest.getQualificationDesc());
        artisanProfile.setQualificationImages(joinImages(submitRequest.getQualificationImages()));
        artisanProfile.setDepositAmount(DEPOSIT_AMOUNT);
        artisanProfile.setAuditRemark(null);
        artisanProfile.setAuditStatus(AUDIT_PENDING);
        artisanProfile.setShelfStatus(SHELF_DISABLED);
        artisanProfile.setApplyTime(now);
        artisanProfile.setAuditTime(null);
        artisanProfile.setUpdateTime(now);
        if (getApplicationByUserId(loginUser.getId()) == null) {
            artisanApplicationMapper.insert(artisanProfile);
        } else {
            artisanApplicationMapper.updateById(artisanProfile);
        }
        User currentUser = userService.getLoginUser(request);
        return toApplicationVO(artisanProfile, currentUser.getUserRole());
    }

    @Override
    public List<AdminArtisanApplicationListItemVO> listAdminApplications(Integer auditStatus, String keyword, HttpServletRequest request) {
        requireAdmin(request);
        List<AdminArtisanApplicationListItemVO> list = artisanApplicationMapper.selectAdminApplications(auditStatus, trim(keyword));
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public AdminArtisanApplicationDetailVO getAdminApplicationDetail(Long id, HttpServletRequest request) {
        requireAdmin(request);
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Application id is invalid");
        }
        AdminArtisanApplicationDetailVO detailVO = artisanApplicationMapper.selectAdminApplicationDetail(id);
        if (detailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Artisan application not found");
        }
        detailVO.setQualificationImages(splitImages(detailVO.getQualificationImagesRaw()));
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveApplication(Long id, AdminArtisanApplicationAuditRequest auditRequest, HttpServletRequest request) {
        requireAdmin(request);
        ArtisanProfile artisanProfile = requireProfile(id);
        if (safeInteger(artisanProfile.getAuditStatus()) == AUDIT_APPROVED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Current application is already approved");
        }
        artisanProfile.setAuditStatus(AUDIT_APPROVED);
        artisanProfile.setShelfStatus(SHELF_ENABLED);
        artisanProfile.setAuditRemark(trim(auditRequest == null ? null : auditRequest.getAuditRemark()));
        artisanProfile.setAuditTime(new Date());
        artisanApplicationMapper.updateById(artisanProfile);
        userService.updateUserRole(artisanProfile.getUserId(), UserConstant.ARTISAN_ROLE);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectApplication(Long id, AdminArtisanApplicationAuditRequest auditRequest, HttpServletRequest request) {
        requireAdmin(request);
        ArtisanProfile artisanProfile = requireProfile(id);
        if (safeInteger(artisanProfile.getAuditStatus()) == AUDIT_REJECTED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Current application is already rejected");
        }
        artisanProfile.setAuditStatus(AUDIT_REJECTED);
        artisanProfile.setShelfStatus(SHELF_DISABLED);
        artisanProfile.setAuditRemark(requiredTrim(auditRequest == null ? null : auditRequest.getAuditRemark(), "Audit remark is required"));
        artisanProfile.setAuditTime(new Date());
        artisanApplicationMapper.updateById(artisanProfile);
        return true;
    }

    private ArtisanApplicationVO toApplicationVO(ArtisanProfile artisanProfile, String userRole) {
        ArtisanApplicationVO applicationVO = new ArtisanApplicationVO();
        applicationVO.setHasApplication(true);
        applicationVO.setId(artisanProfile.getId());
        applicationVO.setUserId(artisanProfile.getUserId());
        applicationVO.setUserRole(userRole);
        applicationVO.setArtisanName(artisanProfile.getArtisanName());
        applicationVO.setShopName(artisanProfile.getShopName());
        applicationVO.setArtisanAvatar(artisanProfile.getArtisanAvatar());
        applicationVO.setCoverUrl(artisanProfile.getCoverUrl());
        applicationVO.setArtisanStory(artisanProfile.getArtisanStory());
        applicationVO.setCraftCategory(artisanProfile.getCraftCategory());
        applicationVO.setOriginPlace(artisanProfile.getOriginPlace());
        applicationVO.setExperienceYears(artisanProfile.getExperienceYears());
        applicationVO.setSupportCustom(artisanProfile.getSupportCustom());
        applicationVO.setContactPhone(artisanProfile.getContactPhone());
        applicationVO.setQualificationDesc(artisanProfile.getQualificationDesc());
        applicationVO.setQualificationImages(splitImages(artisanProfile.getQualificationImages()));
        applicationVO.setDepositAmount(artisanProfile.getDepositAmount() == null ? DEPOSIT_AMOUNT : artisanProfile.getDepositAmount());
        applicationVO.setAuditRemark(artisanProfile.getAuditRemark());
        applicationVO.setAuditStatus(artisanProfile.getAuditStatus());
        applicationVO.setShelfStatus(artisanProfile.getShelfStatus());
        applicationVO.setApplyTime(formatDate(artisanProfile.getApplyTime()));
        applicationVO.setAuditTime(formatDate(artisanProfile.getAuditTime()));
        return applicationVO;
    }

    private ArtisanProfile getApplicationByUserId(Long userId) {
        return artisanApplicationMapper.selectOne(new QueryWrapper<ArtisanProfile>()
                .eq("userId", userId)
                .last("limit 1"));
    }

    private ArtisanProfile requireProfile(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Application id is invalid");
        }
        ArtisanProfile artisanProfile = artisanApplicationMapper.selectById(id);
        if (artisanProfile == null || safeInteger(artisanProfile.getIsDelete()) == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Artisan application not found");
        }
        return artisanProfile;
    }

    private User requireAdmin(HttpServletRequest request) {
        return userService.getAdminLoginUser(request);
    }

    private void sanitizeSubmitRequest(ArtisanApplicationSubmitRequest request) {
        request.setArtisanName(requiredTrim(request.getArtisanName(), "Artisan name is required"));
        request.setShopName(requiredTrim(request.getShopName(), "Shop name is required"));
        request.setArtisanAvatar(requiredTrim(request.getArtisanAvatar(), "Artisan avatar is required"));
        request.setCoverUrl(requiredTrim(request.getCoverUrl(), "Shop cover is required"));
        request.setArtisanStory(requiredTrim(request.getArtisanStory(), "Artisan story is required"));
        request.setCraftCategory(requiredTrim(request.getCraftCategory(), "Craft category is required"));
        request.setOriginPlace(requiredTrim(request.getOriginPlace(), "Origin place is required"));
        request.setExperienceYears(nonNegative(request.getExperienceYears()));
        request.setSupportCustom(normalizeBooleanNumber(request.getSupportCustom()));
        request.setContactPhone(requiredTrim(request.getContactPhone(), "Contact phone is required"));
        request.setQualificationDesc(requiredTrim(request.getQualificationDesc(), "Qualification description is required"));
        List<String> qualificationImages = new ArrayList<>();
        for (String image : request.getQualificationImages()) {
            String trimmed = trim(image);
            if (trimmed != null) {
                qualificationImages.add(trimmed);
            }
        }
        if (qualificationImages.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "At least one qualification image is required");
        }
        request.setQualificationImages(qualificationImages);
    }

    private List<String> splitImages(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        List<String> images = new ArrayList<>();
        for (String item : value.split(",")) {
            String trimmed = trim(item);
            if (trimmed != null) {
                images.add(trimmed);
            }
        }
        return images;
    }

    private String joinImages(List<String> images) {
        List<String> safeImages = new ArrayList<>();
        for (String image : images) {
            String trimmed = trim(image);
            if (trimmed != null) {
                safeImages.add(trimmed);
            }
        }
        return String.join(",", safeImages);
    }

    private String formatDate(Date value) {
        if (value == null) {
            return null;
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
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

    private Integer nonNegative(Integer value) {
        if (value == null) {
            return 0;
        }
        if (value < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Numeric value must be greater than or equal to 0");
        }
        return value;
    }

    private Integer normalizeBooleanNumber(Integer value) {
        return value != null && value == 1 ? 1 : 0;
    }

    private int safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}