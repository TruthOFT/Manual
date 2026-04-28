package com.manual.manual.service;

import com.manual.manual.model.dto.product.AdminProductSaveRequest;
import com.manual.manual.model.vo.product.AdminProductMetaVO;
import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminProductService {

    AdminProductMetaVO getAdminProductMeta(HttpServletRequest request);

    List<AdminProductListItemVO> listAdminProducts(Integer auditStatus, Integer status, String keyword, Long categoryId, HttpServletRequest request);

    AdminProductDetailVO getAdminProductDetail(Long productId, HttpServletRequest request);

    Long createAdminProduct(AdminProductSaveRequest saveRequest, HttpServletRequest request);

    boolean updateAdminProduct(Long productId, AdminProductSaveRequest saveRequest, HttpServletRequest request);

    boolean deleteAdminProduct(Long productId, HttpServletRequest request);

    boolean approveProduct(Long productId, HttpServletRequest request);

    boolean rejectProduct(Long productId, HttpServletRequest request);
}
