package com.manual.manual.service;

import com.manual.manual.constant.UserConstant;
import com.manual.manual.mapper.AdminStaffMapper;
import com.manual.manual.model.dto.staff.AdminStaffSaveRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.service.impl.AdminStaffServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminStaffServiceImplTest {

    @Mock
    private AdminStaffMapper adminStaffMapper;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AdminStaffServiceImpl adminStaffService;

    @Test
    void shouldCreateStaffWithGeneratedStaffNo() {
        User admin = new User();
        admin.setId(3100000000000004001L);
        admin.setUserRole(UserConstant.ADMIN_ROLE);

        AdminStaffSaveRequest saveRequest = new AdminStaffSaveRequest();
        saveRequest.setUserAccount("staff_new");
        saveRequest.setUserPassword("12345678");
        saveRequest.setUserName("新店员");
        saveRequest.setStaffName("新店员");
        saveRequest.setPhone("13800001111");
        saveRequest.setEmail("staff_new@example.com");
        saveRequest.setGender(0);
        saveRequest.setUserStatus(0);
        saveRequest.setPositionName("导购");
        saveRequest.setEntryTime("2026-04-24 10:00:00");
        saveRequest.setStaffStatus(1);

        when(userService.getAdminLoginUser(request)).thenReturn(admin);
        when(adminStaffMapper.countAccount(eq("staff_new"), eq(null))).thenReturn(0);
        when(adminStaffMapper.countPhone(eq("13800001111"), eq(null))).thenReturn(0);
        when(adminStaffMapper.countEmail(eq("staff_new@example.com"), eq(null))).thenReturn(0);
        when(adminStaffMapper.countStaffNo(anyString(), eq(null))).thenReturn(0);
        when(adminStaffMapper.insertStaffUser(any(Long.class), anyString(), any(AdminStaffSaveRequest.class))).thenReturn(1);
        when(adminStaffMapper.insertStaffProfile(any(Long.class), any(Long.class), any(AdminStaffSaveRequest.class))).thenReturn(1);

        Long userId = adminStaffService.createStaff(saveRequest, request);

        assertNotNull(userId);
        ArgumentCaptor<AdminStaffSaveRequest> requestCaptor = ArgumentCaptor.forClass(AdminStaffSaveRequest.class);
        verify(adminStaffMapper).insertStaffProfile(any(Long.class), any(Long.class), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getStaffNo()).isNotBlank().containsOnlyDigits();
        assertThat(requestCaptor.getValue().getSalary()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
