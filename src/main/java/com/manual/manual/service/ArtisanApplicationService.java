package com.manual.manual.service;

import com.manual.manual.model.dto.artisanapplication.AdminArtisanApplicationAuditRequest;
import com.manual.manual.model.dto.artisanapplication.ArtisanApplicationSubmitRequest;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationDetailVO;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationListItemVO;
import com.manual.manual.model.vo.artisanapplication.ArtisanApplicationVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ArtisanApplicationService {

    ArtisanApplicationVO getCurrentApplication(HttpServletRequest request);

    ArtisanApplicationVO submitApplication(ArtisanApplicationSubmitRequest submitRequest, HttpServletRequest request);

    List<AdminArtisanApplicationListItemVO> listAdminApplications(Integer auditStatus, String keyword, HttpServletRequest request);

    AdminArtisanApplicationDetailVO getAdminApplicationDetail(Long id, HttpServletRequest request);

    boolean approveApplication(Long id, AdminArtisanApplicationAuditRequest auditRequest, HttpServletRequest request);

    boolean rejectApplication(Long id, AdminArtisanApplicationAuditRequest auditRequest, HttpServletRequest request);
}