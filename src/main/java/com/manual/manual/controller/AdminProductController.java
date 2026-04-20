package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import com.manual.manual.service.AdminProductService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    @Resource
    private AdminProductService adminProductService;

    @GetMapping
    public BaseResponse<List<AdminProductListItemVO>> listProducts(@RequestParam(required = false) Integer auditStatus,
                                                                   @RequestParam(required = false) Integer status,
                                                                   @RequestParam(required = false) String keyword,
                                                                   HttpServletRequest request) {
        return ResultUtils.success(adminProductService.listAdminProducts(auditStatus, status, keyword, request));
    }

    @GetMapping("/{productId}")
    public BaseResponse<AdminProductDetailVO> getProductDetail(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.getAdminProductDetail(productId, request));
    }

    @PostMapping("/{productId}/approve")
    public BaseResponse<Boolean> approveProduct(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.approveProduct(productId, request));
    }

    @PostMapping("/{productId}/reject")
    public BaseResponse<Boolean> rejectProduct(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.rejectProduct(productId, request));
    }
}
