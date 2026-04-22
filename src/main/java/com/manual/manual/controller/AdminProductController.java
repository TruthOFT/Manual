package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.product.AdminProductSaveRequest;
import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import com.manual.manual.model.vo.product.AdminProductMetaVO;
import com.manual.manual.service.AdminProductService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    @Resource
    private AdminProductService adminProductService;

    @GetMapping("/meta")
    public BaseResponse<AdminProductMetaVO> getProductMeta(HttpServletRequest request) {
        return ResultUtils.success(adminProductService.getAdminProductMeta(request), "获取商品管理元数据成功");
    }

    @GetMapping
    public BaseResponse<List<AdminProductListItemVO>> listProducts(@RequestParam(required = false) Integer auditStatus,
                                                                   @RequestParam(required = false) Integer status,
                                                                   @RequestParam(required = false) String keyword,
                                                                   HttpServletRequest request) {
        return ResultUtils.success(adminProductService.listAdminProducts(auditStatus, status, keyword, request), "获取商品列表成功");
    }

    @GetMapping("/{productId}")
    public BaseResponse<AdminProductDetailVO> getProductDetail(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.getAdminProductDetail(productId, request), "获取商品详情成功");
    }

    @PostMapping
    public BaseResponse<Long> createProduct(@RequestBody AdminProductSaveRequest saveRequest, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.createAdminProduct(saveRequest, request), "新增商品成功");
    }

    @PutMapping("/{productId}")
    public BaseResponse<Boolean> updateProduct(@PathVariable Long productId,
                                               @RequestBody AdminProductSaveRequest saveRequest,
                                               HttpServletRequest request) {
        return ResultUtils.success(adminProductService.updateAdminProduct(productId, saveRequest, request), "更新商品成功");
    }

    @DeleteMapping("/{productId}")
    public BaseResponse<Boolean> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.deleteAdminProduct(productId, request), "删除商品成功");
    }

    @PostMapping("/{productId}/approve")
    public BaseResponse<Boolean> approveProduct(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.approveProduct(productId, request), "审核通过成功");
    }

    @PostMapping("/{productId}/reject")
    public BaseResponse<Boolean> rejectProduct(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(adminProductService.rejectProduct(productId, request), "审核驳回成功");
    }
}
