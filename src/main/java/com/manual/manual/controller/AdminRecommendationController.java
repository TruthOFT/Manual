package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.recommendation.RecommendationOverviewVO;
import com.manual.manual.model.vo.recommendation.RecommendationProductVO;
import com.manual.manual.service.RecommendationService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/recommendations")
public class AdminRecommendationController {

    @Resource
    private RecommendationService recommendationService;

    @GetMapping("/overview")
    public BaseResponse<RecommendationOverviewVO> getOverview(HttpServletRequest request) {
        return ResultUtils.success(recommendationService.getAdminOverview(request), "获取推荐概览成功");
    }

    @GetMapping("/user")
    public BaseResponse<List<RecommendationProductVO>> listUserRecommendations(@RequestParam Long userId,
                                                                               @RequestParam(required = false) Integer limit,
                                                                               HttpServletRequest request) {
        return ResultUtils.success(recommendationService.listAdminUserRecommendations(userId, limit, request), "获取用户推荐成功");
    }

    @GetMapping("/similar")
    public BaseResponse<List<RecommendationProductVO>> listSimilarProducts(@RequestParam Long productId,
                                                                           @RequestParam(required = false) Integer limit,
                                                                           HttpServletRequest request) {
        return ResultUtils.success(recommendationService.listAdminSimilarProducts(productId, limit, request), "获取相似作品成功");
    }
}
