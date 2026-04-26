package com.manual.manual.service;

import com.manual.manual.model.dto.recommendation.BehaviorRecordRequest;
import com.manual.manual.model.vo.recommendation.RecommendationCalculateResultVO;
import com.manual.manual.model.vo.recommendation.RecommendationOverviewVO;
import com.manual.manual.model.vo.recommendation.RecommendationProductVO;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

public interface RecommendationService {

    List<RecommendationProductVO> listCurrentUserRecommendations(Integer limit, HttpServletRequest request);

    List<RecommendationProductVO> listSimilarProducts(Long productId, Integer limit);

    Boolean recordCurrentUserBehavior(BehaviorRecordRequest behaviorRequest, HttpServletRequest request);

    void recordSystemBehavior(Long userId, Long productId, Long skuId, Integer behaviorType,
                              BigDecimal behaviorWeight, Integer sourceType, Long sourceId);

    void deactivateSystemBehavior(Long userId, Long productId, Integer behaviorType, Integer sourceType, Long sourceId);

    void refreshUserRecommendations(Long userId);

    RecommendationOverviewVO getAdminOverview(HttpServletRequest request);

    RecommendationCalculateResultVO calculateRecommendations(HttpServletRequest request);

    List<RecommendationProductVO> listAdminUserRecommendations(Long userId, Integer limit, HttpServletRequest request);

    List<RecommendationProductVO> listAdminSimilarProducts(Long productId, Integer limit, HttpServletRequest request);
}
