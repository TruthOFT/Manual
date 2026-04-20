package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterCustomRequirementActionRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterOrderShipRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProductSaveRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProfileUpdateRequest;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCategoryVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterDashboardVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductPageVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProfileVO;
import com.manual.manual.service.ArtisanCenterService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/artisan-center")
public class ArtisanCenterController {

    @Resource
    private ArtisanCenterService artisanCenterService;

    @GetMapping("/dashboard")
    public BaseResponse<ArtisanCenterDashboardVO> getDashboard(HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.getDashboard(request));
    }

    @GetMapping("/profile")
    public BaseResponse<ArtisanCenterProfileVO> getProfile(HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.getProfile(request));
    }

    @PutMapping("/profile")
    public BaseResponse<ArtisanCenterProfileVO> updateProfile(@RequestBody ArtisanCenterProfileUpdateRequest updateRequest,
                                                              HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.updateProfile(updateRequest, request));
    }

    @GetMapping("/products")
    public BaseResponse<ArtisanCenterProductPageVO> listProducts(@RequestParam(required = false) Integer auditStatus,
                                                                 @RequestParam(required = false) Integer status,
                                                                 @RequestParam(required = false) String keyword,
                                                                 HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.listProducts(auditStatus, status, keyword, request));
    }

    @GetMapping("/product-categories")
    public BaseResponse<List<ArtisanCenterCategoryVO>> listProductCategories(HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.listProductCategories(request));
    }

    @PostMapping("/products")
    public BaseResponse<Long> createProduct(@RequestBody ArtisanCenterProductSaveRequest saveRequest,
                                            HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.createProduct(saveRequest, request));
    }

    @GetMapping("/products/{productId}")
    public BaseResponse<ArtisanCenterProductDetailVO> getProductDetail(@PathVariable Long productId,
                                                                       HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.getProductDetail(productId, request));
    }

    @PutMapping("/products/{productId}")
    public BaseResponse<Boolean> updateProduct(@PathVariable Long productId,
                                               @RequestBody ArtisanCenterProductSaveRequest saveRequest,
                                               HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.updateProduct(productId, saveRequest, request));
    }

    @PostMapping("/products/{productId}/submit-audit")
    public BaseResponse<Boolean> submitAudit(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.submitAudit(productId, request));
    }

    @PostMapping("/products/{productId}/on-shelf")
    public BaseResponse<Boolean> onShelf(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.onShelf(productId, request));
    }

    @PostMapping("/products/{productId}/off-shelf")
    public BaseResponse<Boolean> offShelf(@PathVariable Long productId, HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.offShelf(productId, request));
    }

    @GetMapping("/orders")
    public BaseResponse<List<ArtisanCenterOrderListItemVO>> listOrders(@RequestParam(required = false) Integer orderStatus,
                                                                       @RequestParam(required = false) String keyword,
                                                                       HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.listOrders(orderStatus, keyword, request));
    }

    @GetMapping("/orders/{orderItemId}")
    public BaseResponse<ArtisanCenterOrderDetailVO> getOrderDetail(@PathVariable Long orderItemId,
                                                                   HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.getOrderDetail(orderItemId, request));
    }

    @PostMapping("/orders/{orderItemId}/ship")
    public BaseResponse<Boolean> shipOrder(@PathVariable Long orderItemId,
                                           @RequestBody ArtisanCenterOrderShipRequest shipRequest,
                                           HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.shipOrder(orderItemId, shipRequest, request));
    }

    @GetMapping("/custom-requirements")
    public BaseResponse<List<ArtisanCenterCustomRequirementListItemVO>> listCustomRequirements(
            @RequestParam(required = false) Integer confirmStatus,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request
    ) {
        return ResultUtils.success(artisanCenterService.listCustomRequirements(confirmStatus, keyword, request));
    }

    @GetMapping("/custom-requirements/{id}")
    public BaseResponse<ArtisanCenterCustomRequirementDetailVO> getCustomRequirementDetail(@PathVariable Long id,
                                                                                            HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.getCustomRequirementDetail(id, request));
    }

    @PostMapping("/custom-requirements/{id}/accept")
    public BaseResponse<Boolean> acceptCustomRequirement(@PathVariable Long id,
                                                         @RequestBody(required = false) ArtisanCenterCustomRequirementActionRequest actionRequest,
                                                         HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.acceptCustomRequirement(id, actionRequest, request));
    }

    @PostMapping("/custom-requirements/{id}/reject")
    public BaseResponse<Boolean> rejectCustomRequirement(@PathVariable Long id,
                                                         @RequestBody(required = false) ArtisanCenterCustomRequirementActionRequest actionRequest,
                                                         HttpServletRequest request) {
        return ResultUtils.success(artisanCenterService.rejectCustomRequirement(id, actionRequest, request));
    }

    @PostMapping("/custom-requirements/{id}/processing")
    public BaseResponse<Boolean> markCustomRequirementProcessing(
            @PathVariable Long id,
            @RequestBody(required = false) ArtisanCenterCustomRequirementActionRequest actionRequest,
            HttpServletRequest request
    ) {
        return ResultUtils.success(artisanCenterService.markCustomRequirementProcessing(id, actionRequest, request));
    }

    @PostMapping("/custom-requirements/{id}/complete")
    public BaseResponse<Boolean> markCustomRequirementComplete(
            @PathVariable Long id,
            @RequestBody(required = false) ArtisanCenterCustomRequirementActionRequest actionRequest,
            HttpServletRequest request
    ) {
        return ResultUtils.success(artisanCenterService.markCustomRequirementComplete(id, actionRequest, request));
    }
}
