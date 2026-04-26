package com.manual.manual.controller;

import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductFilterCategoryVO;
import com.manual.manual.model.vo.product.ProductFilterOptionsVO;
import com.manual.manual.model.vo.product.ProductListItemVO;
import com.manual.manual.model.vo.product.ProductListPageVO;
import com.manual.manual.model.vo.product.ProductMaterialVO;
import com.manual.manual.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void shouldReturnFilteredProducts() throws Exception {
        ProductListPageVO pageVO = new ProductListPageVO();
        ProductFilterOptionsVO filtersVO = new ProductFilterOptionsVO();
        ProductFilterCategoryVO categoryVO = new ProductFilterCategoryVO();
        categoryVO.setId(3100000000000003005L);
        categoryVO.setCategoryName("陶瓷器皿");
        filtersVO.setCategories(List.of(categoryVO));
        filtersVO.setOriginPlaces(List.of("景德镇"));
        filtersVO.setMaterials(List.of("陶土"));
        pageVO.setFilters(filtersVO);

        ProductListItemVO productVO = new ProductListItemVO();
        productVO.setId(3100000000000004001L);
        productVO.setCategoryId(3100000000000003005L);
        productVO.setProductName("暮色手作茶杯");
        productVO.setCategoryName("陶瓷器皿");
        pageVO.setProducts(List.of(productVO));

        when(productService.listProducts(eq(3100000000000003005L), eq("景德镇"), eq("陶土"))).thenReturn(pageVO);

        mockMvc.perform(get("/products")
                        .param("categoryId", "3100000000000003005")
                        .param("originPlace", "景德镇")
                        .param("materialName", "陶土")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.filters.categories[0].categoryName").value("陶瓷器皿"))
                .andExpect(jsonPath("$.data.filters.originPlaces[0]").value("景德镇"))
                .andExpect(jsonPath("$.data.filters.materials[0]").value("陶土"))
                .andExpect(jsonPath("$.data.products[0].productName").value("暮色手作茶杯"));
    }

    @Test
    void shouldReturnEmptyProductList() throws Exception {
        when(productService.listProducts(null, null, null)).thenReturn(new ProductListPageVO());

        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products").isEmpty())
                .andExpect(jsonPath("$.data.filters.categories").isArray())
                .andExpect(jsonPath("$.data.filters.categories").isEmpty());
    }

    @Test
    void shouldReturnProductDetail() throws Exception {
        ProductDetailVO detailVO = new ProductDetailVO();
        detailVO.setId(3100000000000004001L);
        detailVO.setProductName("暮色手作茶杯");
        detailVO.setCategoryName("陶瓷器皿");
        detailVO.setMinPrice(new BigDecimal("89.00"));

        ProductMaterialVO materialVO = new ProductMaterialVO();
        materialVO.setId(1L);
        materialVO.setMaterialName("陶土");
        detailVO.setMaterials(List.of(materialVO));

        when(productService.getProductDetail(3100000000000004001L)).thenReturn(detailVO);

        mockMvc.perform(get("/products/3100000000000004001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.productName").value("暮色手作茶杯"))
                .andExpect(jsonPath("$.data.categoryName").value("陶瓷器皿"))
                .andExpect(jsonPath("$.data.materials[0].materialName").value("陶土"));
    }
}
