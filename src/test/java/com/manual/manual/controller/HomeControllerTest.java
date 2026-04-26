package com.manual.manual.controller;

import com.manual.manual.model.vo.home.HomeCategoryVO;
import com.manual.manual.model.vo.home.HomePageVO;
import com.manual.manual.model.vo.home.HomeProductVO;
import com.manual.manual.model.vo.home.HomeRecentOrderVO;
import com.manual.manual.service.HomeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeService homeService;

    @Test
    void shouldReturnHomePageData() throws Exception {
        HomePageVO homePageVO = new HomePageVO();

        HomeCategoryVO categoryVO = new HomeCategoryVO();
        categoryVO.setId(1L);
        categoryVO.setCategoryName("陶瓷器皿");
        categoryVO.setCategoryLevel(1);
        homePageVO.setCategories(List.of(categoryVO));

        HomeProductVO productVO = new HomeProductVO();
        productVO.setId(2L);
        productVO.setProductName("暮色手作茶杯");
        homePageVO.setProducts(List.of(productVO));

        HomeRecentOrderVO recentOrderVO = new HomeRecentOrderVO();
        recentOrderVO.setId(4L);
        recentOrderVO.setOrderNo("mo202604150001");
        recentOrderVO.setFinishTime("2026-04-15 20:15");
        homePageVO.setRecentOrders(List.of(recentOrderVO));

        when(homeService.getHomePage()).thenReturn(homePageVO);

        mockMvc.perform(get("/home").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.categories[0].categoryName").value("陶瓷器皿"))
                .andExpect(jsonPath("$.data.products[0].productName").value("暮色手作茶杯"))
                .andExpect(jsonPath("$.data.recentOrders[0].finishTime").value("2026-04-15 20:15"));
    }

    @Test
    void shouldReturnEmptyCollectionsWhenNoHomeData() throws Exception {
        HomePageVO homePageVO = new HomePageVO();

        when(homeService.getHomePage()).thenReturn(homePageVO);

        mockMvc.perform(get("/home").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.categories").isArray())
                .andExpect(jsonPath("$.data.categories").isEmpty())
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products").isEmpty())
                .andExpect(jsonPath("$.data.recentOrders").isArray())
                .andExpect(jsonPath("$.data.recentOrders").isEmpty());
    }
}
