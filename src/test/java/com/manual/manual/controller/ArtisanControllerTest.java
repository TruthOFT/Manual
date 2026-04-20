package com.manual.manual.controller;

import com.manual.manual.model.vo.artisan.ArtisanDetailVO;
import com.manual.manual.model.vo.artisan.ArtisanListItemVO;
import com.manual.manual.model.vo.product.ProductListItemVO;
import com.manual.manual.service.ArtisanService;
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

@WebMvcTest(ArtisanController.class)
class ArtisanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtisanService artisanService;

    @Test
    void shouldReturnArtisanList() throws Exception {
        ArtisanListItemVO artisanVO = new ArtisanListItemVO();
        artisanVO.setId(3100000000000002001L);
        artisanVO.setArtisanName("陆青禾");
        artisanVO.setProductCount(2L);

        when(artisanService.listArtisans()).thenReturn(List.of(artisanVO));

        mockMvc.perform(get("/artisans").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].artisanName").value("陆青禾"))
                .andExpect(jsonPath("$.data[0].productCount").value(2));
    }

    @Test
    void shouldReturnArtisanDetail() throws Exception {
        ArtisanDetailVO detailVO = new ArtisanDetailVO();
        detailVO.setId(3100000000000002001L);
        detailVO.setArtisanName("陆青禾");
        detailVO.setArtisanStory("专注柴烧与日用器物创作。");

        ProductListItemVO productVO = new ProductListItemVO();
        productVO.setId(3100000000000004001L);
        productVO.setProductName("柴烧釉陶马克杯");
        detailVO.setProducts(List.of(productVO));

        when(artisanService.getArtisanDetail(3100000000000002001L)).thenReturn(detailVO);

        mockMvc.perform(get("/artisans/3100000000000002001").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.artisanName").value("陆青禾"))
                .andExpect(jsonPath("$.data.artisanStory").value("专注柴烧与日用器物创作。"))
                .andExpect(jsonPath("$.data.products[0].productName").value("柴烧釉陶马克杯"));
    }
}
