package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductListPageVO;
import com.manual.manual.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping
    public BaseResponse<ProductListPageVO> listProducts(@RequestParam(required = false) Long categoryId,
                                                        @RequestParam(required = false) String originPlace,
                                                        @RequestParam(required = false) String materialName) {
        return ResultUtils.success(productService.listProducts(categoryId, originPlace, materialName), "获取商品列表成功");
    }

    @GetMapping("/{productId}")
    public BaseResponse<ProductDetailVO> getProductDetail(@PathVariable Long productId) {
        return ResultUtils.success(productService.getProductDetail(productId), "获取商品详情成功");
    }
}
