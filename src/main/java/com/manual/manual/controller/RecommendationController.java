package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.recommendation.BehaviorRecordRequest;
import com.manual.manual.model.vo.recommendation.RecommendationProductVO;
import com.manual.manual.service.RecommendationService;
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
@RequestMapping("/recommendations")
public class RecommendationController {

    @Resource
    private RecommendationService recommendationService;

    @GetMapping("/me")
    public BaseResponse<List<RecommendationProductVO>> listCurrentUserRecommendations(
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) {
        return ResultUtils.success(recommendationService.listCurrentUserRecommendations(limit, request), "获取推荐成功");
    }

    @GetMapping("/similar/{productId}")
    public BaseResponse<List<RecommendationProductVO>> listSimilarProducts(@PathVariable Long productId,
                                                                           @RequestParam(required = false) Integer limit) {
        return ResultUtils.success(recommendationService.listSimilarProducts(productId, limit), "获取相似作品成功");
    }

    @PostMapping("/behaviors")
    public BaseResponse<Boolean> recordCurrentUserBehavior(@RequestBody BehaviorRecordRequest behaviorRequest,
                                                           HttpServletRequest request) {
        return ResultUtils.success(recommendationService.recordCurrentUserBehavior(behaviorRequest, request), "行为已记录");
    }
}
