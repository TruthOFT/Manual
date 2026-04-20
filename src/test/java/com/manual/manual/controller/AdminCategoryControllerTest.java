package com.manual.manual.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manual.manual.model.dto.category.AdminCategorySaveRequest;
import com.manual.manual.model.vo.category.AdminCategoryVO;
import com.manual.manual.service.CategoryService;
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

@WebMvcTest(AdminCategoryController.class)
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void shouldReturnCategoryList() throws Exception {
        AdminCategoryVO categoryVO = new AdminCategoryVO();
        categoryVO.setId(3100000000000003001L);
        categoryVO.setCategoryName("陶艺");
        categoryVO.setCategoryLevel(1);

        when(categoryService.listAdminCategories(any())).thenReturn(List.of(categoryVO));

        mockMvc.perform(get("/admin/categories").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].categoryName").value("陶艺"))
                .andExpect(jsonPath("$.data[0].categoryLevel").value(1));
    }

    @Test
    void shouldReturnCategoryDetail() throws Exception {
        AdminCategoryVO categoryVO = new AdminCategoryVO();
        categoryVO.setId(3100000000000003005L);
        categoryVO.setCategoryName("茶器杯具");
        categoryVO.setParentId(3100000000000003001L);

        when(categoryService.getAdminCategory(eq(3100000000000003005L), any())).thenReturn(categoryVO);

        mockMvc.perform(get("/admin/categories/3100000000000003005").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.categoryName").value("茶器杯具"));
    }

    @Test
    void shouldCreateCategory() throws Exception {
        AdminCategorySaveRequest requestBody = new AdminCategorySaveRequest();
        requestBody.setCategoryName("皮艺");
        requestBody.setCategoryLevel(1);
        requestBody.setSortOrder(50);
        requestBody.setIsEnable(1);

        when(categoryService.createAdminCategory(any(AdminCategorySaveRequest.class), any())).thenReturn(3100000000000003099L);

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("3100000000000003099"));
    }
}
