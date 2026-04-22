package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.artisanapplication.AdminArtisanApplicationAuditRequest;
import com.manual.manual.model.dto.artisanapplication.ArtisanApplicationSubmitRequest;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationDetailVO;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationListItemVO;
import com.manual.manual.model.vo.artisanapplication.ArtisanApplicationVO;
import com.manual.manual.service.ArtisanApplicationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArtisanApplicationController {

    @Resource
    private ArtisanApplicationService artisanApplicationService;

    @GetMapping("/artisan-application")
    public BaseResponse<ArtisanApplicationVO> getCurrentApplication(HttpServletRequest request) {
        return ResultUtils.success(artisanApplicationService.getCurrentApplication(request), "获取匠人申请信息成功");
    }

    @PostMapping("/artisan-application")
    public BaseResponse<ArtisanApplicationVO> submitApplication(@RequestBody ArtisanApplicationSubmitRequest submitRequest,
                                                                HttpServletRequest request) {
        return ResultUtils.success(artisanApplicationService.submitApplication(submitRequest, request), "提交匠人申请成功");
    }

    @GetMapping("/admin/artisan-applications")
    public BaseResponse<List<AdminArtisanApplicationListItemVO>> listAdminApplications(@RequestParam(required = false) Integer auditStatus,
                                                                                       @RequestParam(required = false) String keyword,
                                                                                       HttpServletRequest request) {
        return ResultUtils.success(artisanApplicationService.listAdminApplications(auditStatus, keyword, request), "获取匠人申请列表成功");
    }

    @GetMapping("/admin/artisan-applications/{id}")
    public BaseResponse<AdminArtisanApplicationDetailVO> getAdminApplicationDetail(@PathVariable Long id,
                                                                                    HttpServletRequest request) {
        return ResultUtils.success(artisanApplicationService.getAdminApplicationDetail(id, request), "获取匠人申请详情成功");
    }

    @PostMapping("/admin/artisan-applications/{id}/approve")
    public BaseResponse<Boolean> approveApplication(@PathVariable Long id,
                                                    @RequestBody(required = false) AdminArtisanApplicationAuditRequest auditRequest,
                                                    HttpServletRequest request) {
        return ResultUtils.success(artisanApplicationService.approveApplication(id, auditRequest, request), "审核通过成功");
    }

    @PostMapping("/admin/artisan-applications/{id}/reject")
    public BaseResponse<Boolean> rejectApplication(@PathVariable Long id,
                                                   @RequestBody AdminArtisanApplicationAuditRequest auditRequest,
                                                   HttpServletRequest request) {
        return ResultUtils.success(artisanApplicationService.rejectApplication(id, auditRequest, request), "审核驳回成功");
    }
}
