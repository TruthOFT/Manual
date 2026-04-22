package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.AdminProductMapper;
import com.manual.manual.model.dto.product.AdminProductSaveRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.product.AdminProductMetaVO;
import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import com.manual.manual.service.AdminProductService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    private static final int PRODUCT_AUDIT_DRAFT = -1;
    private static final int PRODUCT_AUDIT_PENDING = 0;
    private static final int PRODUCT_AUDIT_APPROVED = 1;
    private static final int PRODUCT_AUDIT_REJECTED = 2;
    private static final int PRODUCT_STATUS_DRAFT = 0;
    private static final int PRODUCT_STATUS_ON_SHELF = 1;
    private static final int PRODUCT_STATUS_OFF_SHELF = 2;

    @Resource
    private AdminProductMapper adminProductMapper;

    @Resource
    private UserService userService;

    @Override
    public AdminProductMetaVO getAdminProductMeta(HttpServletRequest request) {
        requireAdmin(request);
        AdminProductMetaVO metaVO = new AdminProductMetaVO();
        metaVO.setCategories(defaultList(adminProductMapper.selectAdminProductCategories()));
        metaVO.setArtisans(defaultList(adminProductMapper.selectAdminProductArtisans()));
        return metaVO;
    }

    @Override
    public List<AdminProductListItemVO> listAdminProducts(Integer auditStatus, Integer status, String keyword, HttpServletRequest request) {
        requireAdmin(request);
        List<AdminProductListItemVO> products = adminProductMapper.selectAdminProducts(auditStatus, status, trim(keyword));
        return products == null ? Collections.emptyList() : products;
    }

    @Override
    public AdminProductDetailVO getAdminProductDetail(Long productId, HttpServletRequest request) {
        requireAdmin(request);
        AdminProductDetailVO detailVO = requireProductDetail(productId);
        detailVO.setImages(defaultList(adminProductMapper.selectProductImages(productId)));
        detailVO.setMaterials(defaultList(adminProductMapper.selectProductMaterials(productId)));
        detailVO.setSkus(defaultList(adminProductMapper.selectProductSkus(productId)));
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdminProduct(AdminProductSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        sanitizeSaveRequest(saveRequest);
        Long productId = IdWorker.getId();
        if (adminProductMapper.insertAdminProduct(productId, saveRequest) <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create product failed");
        }
        return productId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdminProduct(Long productId, AdminProductSaveRequest saveRequest, HttpServletRequest request) {
        requireAdmin(request);
        requireProductDetail(productId);
        sanitizeSaveRequest(saveRequest);
        return adminProductMapper.updateAdminProduct(productId, saveRequest) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAdminProduct(Long productId, HttpServletRequest request) {
        requireAdmin(request);
        requireProductDetail(productId);
        boolean deleted = adminProductMapper.deleteAdminProduct(productId) > 0;
        if (deleted) {
            adminProductMapper.deleteAdminProductImages(productId);
            adminProductMapper.deleteAdminProductMaterials(productId);
            adminProductMapper.deleteAdminProductSkus(productId);
        }
        return deleted;
    }

    @Override
    public boolean approveProduct(Long productId, HttpServletRequest request) {
        requireAdmin(request);
        AdminProductDetailVO detailVO = requireProductDetail(productId);
        if (safeInteger(detailVO.getAuditStatus()) == PRODUCT_AUDIT_APPROVED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Product is already approved");
        }
        return adminProductMapper.updateProductAudit(productId, PRODUCT_AUDIT_APPROVED, safeInteger(detailVO.getStatus())) > 0;
    }

    @Override
    public boolean rejectProduct(Long productId, HttpServletRequest request) {
        requireAdmin(request);
        AdminProductDetailVO detailVO = requireProductDetail(productId);
        if (safeInteger(detailVO.getAuditStatus()) == PRODUCT_AUDIT_REJECTED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Product is already rejected");
        }
        return adminProductMapper.updateProductAudit(productId, PRODUCT_AUDIT_REJECTED, PRODUCT_STATUS_DRAFT) > 0;
    }

    private void sanitizeSaveRequest(AdminProductSaveRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request body is required");
        }
        request.setCategoryId(requiredPositiveId(request.getCategoryId(), "Category is required"));
        request.setArtisanId(requiredPositiveId(request.getArtisanId(), "Artisan is required"));
        request.setProductName(requiredTrim(request.getProductName(), "Product name is required"));
        request.setProductSubtitle(trim(request.getProductSubtitle()));
        request.setProductCover(trim(request.getProductCover()));
        request.setProductDesc(trim(request.getProductDesc()));
        request.setCraftType(trim(request.getCraftType()));
        request.setMaterialDesc(trim(request.getMaterialDesc()));
        request.setOriginPlace(trim(request.getOriginPlace()));
        request.setHandmadeCycleDays(nonNegative(request.getHandmadeCycleDays(), "Handmade cycle days"));
        request.setSupportCustom(normalizeBooleanNumber(request.getSupportCustom()));
        request.setInventory(nonNegative(request.getInventory(), "Inventory"));
        request.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        request.setMinPrice(nonNegativeAmount(request.getMinPrice(), "Min price"));
        request.setMaxPrice(nonNegativeAmount(request.getMaxPrice(), "Max price"));
        if (request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Min price cannot be greater than max price");
        }
        request.setAuditStatus(normalizeAuditStatus(request.getAuditStatus()));
        request.setStatus(normalizeProductStatus(request.getStatus()));
        if (request.getStatus() == PRODUCT_STATUS_ON_SHELF && request.getAuditStatus() != PRODUCT_AUDIT_APPROVED) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Only approved products can be on shelf");
        }
        if (adminProductMapper.countEnabledCategoryById(request.getCategoryId()) <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Category does not exist or is disabled");
        }
        if (adminProductMapper.countEnabledArtisanById(request.getArtisanId()) <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Artisan does not exist or is not approved");
        }
    }

    private int normalizeAuditStatus(Integer value) {
        int status = value == null ? PRODUCT_AUDIT_PENDING : value;
        if (status != PRODUCT_AUDIT_DRAFT
                && status != PRODUCT_AUDIT_PENDING
                && status != PRODUCT_AUDIT_APPROVED
                && status != PRODUCT_AUDIT_REJECTED) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Audit status is invalid");
        }
        return status;
    }

    private int normalizeProductStatus(Integer value) {
        int status = value == null ? PRODUCT_STATUS_DRAFT : value;
        if (status != PRODUCT_STATUS_DRAFT
                && status != PRODUCT_STATUS_ON_SHELF
                && status != PRODUCT_STATUS_OFF_SHELF) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Product status is invalid");
        }
        return status;
    }

    private AdminProductDetailVO requireProductDetail(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Product id is invalid");
        }
        AdminProductDetailVO detailVO = adminProductMapper.selectAdminProductDetail(productId);
        if (detailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Product not found");
        }
        return detailVO;
    }

    private User requireAdmin(HttpServletRequest request) {
        User loginUser = userService.getAdminLoginUser(request);
        if (loginUser == null || !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Only admin users can access this endpoint");
        }
        return loginUser;
    }

    private Long requiredPositiveId(Long value, String message) {
        if (value == null || value <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
        return value;
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

    private int nonNegative(Integer value, String fieldName) {
        int result = value == null ? 0 : value;
        if (result < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " must be greater than or equal to 0");
        }
        return result;
    }

    private BigDecimal nonNegativeAmount(BigDecimal value, String fieldName) {
        BigDecimal result = value == null ? BigDecimal.ZERO : value;
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " must be greater than or equal to 0");
        }
        return result;
    }

    private Integer normalizeBooleanNumber(Integer value) {
        return value != null && value == 1 ? 1 : 0;
    }

    private int safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
