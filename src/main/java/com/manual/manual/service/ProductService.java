package com.manual.manual.service;

import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductListPageVO;

public interface ProductService {

    ProductListPageVO listProducts(Long categoryId, String originPlace, String materialName);

    ProductDetailVO getProductDetail(Long productId);
}
