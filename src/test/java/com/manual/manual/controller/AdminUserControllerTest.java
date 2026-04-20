package com.manual.manual.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manual.manual.model.dto.user.AdminUserCreateRequest;
import com.manual.manual.model.vo.user.AdminUserVO;
import com.manual.manual.service.UserService;
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

@WebMvcTest(AdminUserController.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnUserList() throws Exception {
        AdminUserVO userVO = new AdminUserVO();
        userVO.setId(3100000000000001001L);
        userVO.setUserAccount("admin_manual");
        userVO.setUserName("平台管理员");
        userVO.setUserRole("admin");

        when(userService.listAdminUsers(eq("admin"), eq("admin"), eq(0), any())).thenReturn(List.of(userVO));

        mockMvc.perform(get("/admin/users")
                        .param("keyword", "admin")
                        .param("userRole", "admin")
                        .param("userStatus", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].userAccount").value("admin_manual"))
                .andExpect(jsonPath("$.data[0].userRole").value("admin"));
    }

    @Test
    void shouldReturnUserDetail() throws Exception {
        AdminUserVO userVO = new AdminUserVO();
        userVO.setId(3100000000000001002L);
        userVO.setUserAccount("artisan_lu");
        userVO.setUserName("陆青禾");

        when(userService.getAdminUser(eq(3100000000000001002L), any())).thenReturn(userVO);

        mockMvc.perform(get("/admin/users/3100000000000001002").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.userAccount").value("artisan_lu"))
                .andExpect(jsonPath("$.data.userName").value("陆青禾"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        AdminUserCreateRequest createRequest = new AdminUserCreateRequest();
        createRequest.setUserAccount("buyer_new");
        createRequest.setUserPassword("12345678");
        createRequest.setUserName("新用户");
        createRequest.setUserRole("user");
        createRequest.setUserStatus(0);

        when(userService.createAdminUser(any(AdminUserCreateRequest.class), any())).thenReturn(3100000000000001999L);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("3100000000000001999"));
    }
}
