package com.manual.manual.controller;

import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import com.manual.manual.service.AdminProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProductController.class)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminProductService adminProductService;

    @Test
    void shouldReturnProductList() throws Exception {
        AdminProductListItemVO productVO = new AdminProductListItemVO();
        productVO.setId(3100000000000004001L);
        productVO.setProductName("柴烧釉陶马克杯");
        productVO.setAuditStatus(0);

        when(adminProductService.listAdminProducts(eq(0), eq(0), eq("马克"), any())).thenReturn(List.of(productVO));

        mockMvc.perform(get("/admin/products")
                        .param("auditStatus", "0")
                        .param("status", "0")
                        .param("keyword", "马克")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].productName").value("柴烧釉陶马克杯"));
    }

    @Test
    void shouldReturnProductDetail() throws Exception {
        AdminProductDetailVO detailVO = new AdminProductDetailVO();
        detailVO.setId(3100000000000004001L);
        detailVO.setProductName("柴烧釉陶马克杯");

        when(adminProductService.getAdminProductDetail(eq(3100000000000004001L), any())).thenReturn(detailVO);

        mockMvc.perform(get("/admin/products/3100000000000004001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.productName").value("柴烧釉陶马克杯"));
    }

    @Test
    void shouldApproveProduct() throws Exception {
        when(adminProductService.approveProduct(eq(3100000000000004001L), any())).thenReturn(true);

        mockMvc.perform(post("/admin/products/3100000000000004001/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));
    }
}
