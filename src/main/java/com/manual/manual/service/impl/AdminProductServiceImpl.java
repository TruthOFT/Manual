package com.manual.manual.service.impl;

import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.AdminProductMapper;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import com.manual.manual.service.AdminProductService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    private static final int PRODUCT_AUDIT_PENDING = 0;
    private static final int PRODUCT_AUDIT_APPROVED = 1;
    private static final int PRODUCT_AUDIT_REJECTED = 2;
    private static final int PRODUCT_STATUS_DRAFT = 0;

    @Resource
    private AdminProductMapper adminProductMapper;

    @Resource
    private UserService userService;

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

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
