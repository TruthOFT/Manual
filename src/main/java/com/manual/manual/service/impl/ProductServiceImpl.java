package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.ProductMapper;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductFavoriteVO;
import com.manual.manual.model.vo.product.ProductFilterOptionsVO;
import com.manual.manual.model.vo.product.ProductImageVO;
import com.manual.manual.model.vo.product.ProductListItemVO;
import com.manual.manual.model.vo.product.ProductListPageVO;
import com.manual.manual.model.vo.product.ProductMaterialVO;
import com.manual.manual.model.vo.product.ProductReviewVO;
import com.manual.manual.service.ProductService;
import com.manual.manual.service.RecommendationService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private UserService userService;

    @Resource
    private RecommendationService recommendationService;

    @Override
    public ProductListPageVO listProducts(Long categoryId, String originPlace, String materialName) {
        ProductListPageVO productListPageVO = new ProductListPageVO();
        boolean publishedOnly = true;
        String safeOriginPlace = trim(originPlace);
        String safeMaterialName = trim(materialName);
        List<ProductListItemVO> products = defaultList(productMapper.selectProducts(
                categoryId,
                safeOriginPlace,
                safeMaterialName,
                publishedOnly
        ));
        if (products.isEmpty()) {
            publishedOnly = false;
            products = defaultList(productMapper.selectProducts(
                    categoryId,
                    safeOriginPlace,
                    safeMaterialName,
                    publishedOnly
            ));
        }

        ProductFilterOptionsVO filterOptionsVO = new ProductFilterOptionsVO();
        filterOptionsVO.setCategories(defaultList(productMapper.selectFilterCategories(publishedOnly)));
        filterOptionsVO.setOriginPlaces(defaultList(productMapper.selectFilterOriginPlaces(publishedOnly)));
        filterOptionsVO.setMaterials(defaultList(productMapper.selectFilterMaterials(publishedOnly)));
        productListPageVO.setFilters(filterOptionsVO);
        productListPageVO.setProducts(products);
        return productListPageVO;
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        return loadProductDetail(productId, null);
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId, HttpServletRequest request) {
        return loadProductDetail(productId, getOptionalLoginUserId(request));
    }

    @Override
    public List<ProductFavoriteVO> listCurrentUserFavorites(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return defaultList(productMapper.selectUserFavorites(loginUser.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean favoriteProduct(Long productId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        requirePublishedProduct(productId);
        if (productMapper.countUserFavorite(loginUser.getId(), productId) > 0) {
            return true;
        }
        Long favoriteId = productMapper.selectFavoriteId(loginUser.getId(), productId);
        if (favoriteId == null) {
            favoriteId = IdWorker.getId();
        }
        int changed = productMapper.countUserFavoriteRecord(loginUser.getId(), productId) > 0
                ? productMapper.restoreFavorite(loginUser.getId(), productId)
                : productMapper.insertFavorite(favoriteId, loginUser.getId(), productId);
        if (changed > 0) {
            productMapper.increaseFavoriteCount(productId);
            recommendationService.recordSystemBehavior(
                    loginUser.getId(),
                    productId,
                    null,
                    2,
                    BigDecimal.valueOf(3),
                    2,
                    favoriteId
            );
            recommendationService.refreshUserRecommendations(loginUser.getId());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean unfavoriteProduct(Long productId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        requirePublishedProduct(productId);
        Long favoriteId = productMapper.selectFavoriteId(loginUser.getId(), productId);
        if (productMapper.deleteFavorite(loginUser.getId(), productId) > 0) {
            productMapper.decreaseFavoriteCount(productId);
            recommendationService.deactivateSystemBehavior(loginUser.getId(), productId, 2, 2, favoriteId);
            recommendationService.refreshUserRecommendations(loginUser.getId());
        }
        return true;
    }

    private ProductDetailVO loadProductDetail(Long productId, Long userId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Product id is invalid");
        }
        ProductDetailVO productDetailVO = productMapper.selectProductDetail(productId, true);
        if (productDetailVO == null) {
            productDetailVO = productMapper.selectProductDetail(productId, false);
        }
        if (productDetailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Product not found");
        }
        productDetailVO.setImages(defaultList(productMapper.selectProductImages(productId)));
        productDetailVO.setMaterials(defaultList(productMapper.selectProductMaterials(productId)));
        productDetailVO.setSkus(defaultList(productMapper.selectProductSkus(productId)));
        productDetailVO.setReviews(defaultList(productMapper.selectProductReviews(productId)));
        if (userId != null) {
            productDetailVO.setFavorited(productMapper.countUserFavorite(userId, productId) > 0);
        }
        return productDetailVO;
    }

    private void requirePublishedProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Product id is invalid");
        }
        if (productMapper.countPublishedProduct(productId) <= 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Product not found");
        }
    }

    private Long getOptionalLoginUserId(HttpServletRequest request) {
        try {
            User loginUser = userService.getLoginUser(request);
            return loginUser.getId();
        } catch (BusinessException exception) {
            if (exception.getCode() == ErrorCode.NOT_LOGIN_ERROR.getCode()) {
                return null;
            }
            throw exception;
        }
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
