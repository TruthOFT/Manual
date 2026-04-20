package com.manual.manual.service;

import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdminProductService {

    List<AdminProductListItemVO> listAdminProducts(Integer auditStatus, Integer status, String keyword, HttpServletRequest request);

    AdminProductDetailVO getAdminProductDetail(Long productId, HttpServletRequest request);

    boolean approveProduct(Long productId, HttpServletRequest request);

    boolean rejectProduct(Long productId, HttpServletRequest request);
}
