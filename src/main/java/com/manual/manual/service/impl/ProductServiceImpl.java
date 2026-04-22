package com.manual.manual.service.impl;

import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.ProductMapper;
import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductFilterOptionsVO;
import com.manual.manual.model.vo.product.ProductImageVO;
import com.manual.manual.model.vo.product.ProductListItemVO;
import com.manual.manual.model.vo.product.ProductListPageVO;
import com.manual.manual.model.vo.product.ProductMaterialVO;
import com.manual.manual.model.vo.product.ProductReviewVO;
import com.manual.manual.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Override
    public ProductListPageVO listProducts(Long categoryId, String originPlace, String materialName) {
        ProductListPageVO productListPageVO = new ProductListPageVO();
        ProductFilterOptionsVO filterOptionsVO = new ProductFilterOptionsVO();
        filterOptionsVO.setCategories(defaultList(productMapper.selectFilterCategories()));
        filterOptionsVO.setOriginPlaces(defaultList(productMapper.selectFilterOriginPlaces()));
        filterOptionsVO.setMaterials(defaultList(productMapper.selectFilterMaterials()));
        productListPageVO.setFilters(filterOptionsVO);
        productListPageVO.setProducts(defaultList(productMapper.selectProducts(categoryId, trim(originPlace), trim(materialName))));
        return productListPageVO;
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Product id is invalid");
        }
        ProductDetailVO productDetailVO = productMapper.selectProductDetail(productId);
        if (productDetailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Product not found");
        }
        productDetailVO.setImages(defaultList(productMapper.selectProductImages(productId)));
        productDetailVO.setMaterials(defaultList(productMapper.selectProductMaterials(productId)));
        productDetailVO.setSkus(defaultList(productMapper.selectProductSkus(productId)));
        productDetailVO.setReviews(defaultList(productMapper.selectProductReviews(productId)));
        return productDetailVO;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
