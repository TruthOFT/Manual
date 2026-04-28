package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductFavoriteVO;
import com.manual.manual.model.vo.product.ProductListPageVO;
import com.manual.manual.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping
    public BaseResponse<ProductListPageVO> listProducts(@RequestParam(required = false) Long categoryId,
                                                        @RequestParam(required = false) String originPlace,
                                                        @RequestParam(required = false) String priceRange) {
        return ResultUtils.success(productService.listProducts(categoryId, originPlace, priceRange));
    }

    @GetMapping("/{productId}")
    public BaseResponse<ProductDetailVO> getProductDetail(@PathVariable("productId") Long productId) {
        return ResultUtils.success(productService.getProductDetail(productId));
    }

    @GetMapping("/favorites")
    public BaseResponse<List<ProductFavoriteVO>> listFavorites(HttpServletRequest request) {
        return ResultUtils.success(productService.listCurrentUserFavorites(request));
    }

    @PostMapping("/{productId}/favorite")
    public BaseResponse<Boolean> favoriteProduct(@PathVariable("productId") Long productId, HttpServletRequest request) {
        return ResultUtils.success(productService.favoriteProduct(productId, request));
    }

    @DeleteMapping("/{productId}/favorite")
    public BaseResponse<Boolean> unfavoriteProduct(@PathVariable("productId") Long productId, HttpServletRequest request) {
        return ResultUtils.success(productService.unfavoriteProduct(productId, request));
    }
}
