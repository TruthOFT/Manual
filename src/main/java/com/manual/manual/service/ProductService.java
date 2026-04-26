package com.manual.manual.service;

import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductFavoriteVO;
import com.manual.manual.model.vo.product.ProductListPageVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ProductService {

    ProductListPageVO listProducts(Long categoryId, String originPlace, String materialName);

    ProductDetailVO getProductDetail(Long productId);

    ProductDetailVO getProductDetail(Long productId, HttpServletRequest request);

    List<ProductFavoriteVO> listCurrentUserFavorites(HttpServletRequest request);

    Boolean favoriteProduct(Long productId, HttpServletRequest request);

    Boolean unfavoriteProduct(Long productId, HttpServletRequest request);
}
