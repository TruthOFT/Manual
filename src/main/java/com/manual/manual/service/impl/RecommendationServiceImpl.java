package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.RecommendationMapper;
import com.manual.manual.model.dto.recommendation.BehaviorRecordRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.recommendation.ProductSimilarityScoreVO;
import com.manual.manual.model.vo.recommendation.FavoriteBehaviorSeedVO;
import com.manual.manual.model.vo.recommendation.RecommendationCalculateResultVO;
import com.manual.manual.model.vo.recommendation.RecommendationOverviewVO;
import com.manual.manual.model.vo.recommendation.RecommendationProductVO;
import com.manual.manual.model.vo.recommendation.UserProductBehaviorScoreVO;
import com.manual.manual.service.RecommendationService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final String ALGORITHM_VERSION = "item_cf_v1";
    private static final int DEFAULT_LIMIT = 8;
    private static final int MAX_LIMIT = 50;
    private static final int SIMILARITY_KEEP_SIZE = 30;
    private static final int USER_RECOMMEND_KEEP_SIZE = 20;

    @Resource
    private RecommendationMapper recommendationMapper;

    @Resource
    private UserService userService;

    @Override
    public List<RecommendationProductVO> listCurrentUserRecommendations(Integer limit, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        int safeLimit = normalizeLimit(limit);
        List<RecommendationProductVO> recommendations = defaultList(
                recommendationMapper.selectUserRecommendations(loginUser.getId(), safeLimit)
        );
        if (!recommendations.isEmpty()) {
            return recommendations;
        }
        return defaultList(recommendationMapper.selectHotProducts(safeLimit));
    }

    @Override
    public List<RecommendationProductVO> listSimilarProducts(Long productId, Integer limit) {
        requireId(productId, "Product id is invalid");
        int safeLimit = normalizeLimit(limit);
        List<RecommendationProductVO> similarProducts = defaultList(
                recommendationMapper.selectSimilarProducts(productId, safeLimit)
        );
        if (!similarProducts.isEmpty()) {
            return similarProducts;
        }
        return defaultList(recommendationMapper.selectHotProducts(safeLimit)).stream()
                .filter(item -> !productId.equals(item.getId()))
                .limit(safeLimit)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean recordCurrentUserBehavior(BehaviorRecordRequest behaviorRequest, HttpServletRequest request) {
        if (behaviorRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long productId = requireId(behaviorRequest.getProductId(), "Product id is invalid");
        Integer behaviorType = normalizeBehaviorType(behaviorRequest.getBehaviorType());
        BigDecimal behaviorWeight = getBehaviorWeight(behaviorType, behaviorRequest.getStaySeconds());
        if (behaviorType == 1) {
            recommendationMapper.insertBrowseLog(
                    IdWorker.getId(),
                    loginUser.getId(),
                    productId,
                    trim(behaviorRequest.getSourcePage()),
                    defaultInteger(behaviorRequest.getStaySeconds(), 0),
                    trim(behaviorRequest.getDeviceType())
            );
        }
        recordSystemBehavior(
                loginUser.getId(),
                productId,
                behaviorRequest.getSkuId(),
                behaviorType,
                behaviorWeight,
                behaviorType,
                null
        );
        return true;
    }

    @Override
    public void recordSystemBehavior(Long userId, Long productId, Long skuId, Integer behaviorType,
                                     BigDecimal behaviorWeight, Integer sourceType, Long sourceId) {
        if (userId == null || productId == null || behaviorType == null) {
            return;
        }
        recommendationMapper.insertBehavior(
                IdWorker.getId(),
                userId,
                productId,
                skuId,
                behaviorType,
                behaviorWeight == null ? BigDecimal.ONE : behaviorWeight,
                sourceType == null ? 0 : sourceType,
                sourceId
        );
    }

    @Override
    public void deactivateSystemBehavior(Long userId, Long productId, Integer behaviorType, Integer sourceType, Long sourceId) {
        if (userId == null || productId == null || behaviorType == null || sourceType == null) {
            return;
        }
        recommendationMapper.deactivateBehavior(userId, productId, behaviorType, sourceType, sourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanupProductRecommendations(Long productId) {
        if (productId == null || productId <= 0) {
            return;
        }
        recommendationMapper.deactivateProductBehaviors(productId);
        recommendationMapper.deactivateProductSimilaritiesByProduct(productId);
        recommendationMapper.deactivateUserRecommendationsByProduct(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshUserRecommendations(Long userId) {
        if (userId == null || userId <= 0) {
            return;
        }
        syncFavoriteBehaviors();
        List<UserProductBehaviorScoreVO> behaviorScores = defaultList(recommendationMapper.selectBehaviorScores());
        Map<Long, Map<Long, BigDecimal>> userProductScores = buildUserProductScores(behaviorScores);
        Map<Long, Double> productNorms = new HashMap<>();
        Map<Long, Map<Long, PairScore>> pairScores = new HashMap<>();
        for (Map<Long, BigDecimal> productScores : userProductScores.values()) {
            List<Map.Entry<Long, BigDecimal>> entries = new ArrayList<>(productScores.entrySet());
            for (Map.Entry<Long, BigDecimal> entry : entries) {
                double score = entry.getValue().doubleValue();
                productNorms.merge(entry.getKey(), score * score, Double::sum);
            }
            for (int i = 0; i < entries.size(); i++) {
                for (int j = i + 1; j < entries.size(); j++) {
                    Long leftProductId = entries.get(i).getKey();
                    Long rightProductId = entries.get(j).getKey();
                    double dotValue = entries.get(i).getValue().doubleValue() * entries.get(j).getValue().doubleValue();
                    addPairScore(pairScores, leftProductId, rightProductId, dotValue);
                    addPairScore(pairScores, rightProductId, leftProductId, dotValue);
                }
            }
        }
        recommendationMapper.deactivateProductSimilarities(ALGORITHM_VERSION);
        Map<Long, List<ProductSimilarityScoreVO>> similarities = persistSimilarities(pairScores, productNorms);
        recommendationMapper.deleteUserRecommendations(userId, ALGORITHM_VERSION);
        Map<Long, BigDecimal> currentUserScores = userProductScores.get(userId);
        if (currentUserScores == null || currentUserScores.isEmpty()) {
            return;
        }
        persistUserRecommendations(Collections.singletonMap(userId, currentUserScores), similarities);
    }

    @Override
    public RecommendationOverviewVO getAdminOverview(HttpServletRequest request) {
        userService.getAdminLoginUser(request);
        RecommendationOverviewVO overview = recommendationMapper.selectOverview();
        return overview == null ? new RecommendationOverviewVO() : overview;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecommendationCalculateResultVO calculateRecommendations(HttpServletRequest request) {
        userService.getAdminLoginUser(request);
        syncFavoriteBehaviors();
        List<UserProductBehaviorScoreVO> behaviorScores = defaultList(recommendationMapper.selectBehaviorScores());
        recommendationMapper.deactivateProductSimilarities(ALGORITHM_VERSION);
        recommendationMapper.deactivateUserRecommendations(ALGORITHM_VERSION);

        Map<Long, Map<Long, BigDecimal>> userProductScores = buildUserProductScores(behaviorScores);
        Map<Long, Double> productNorms = new HashMap<>();
        Map<Long, Map<Long, PairScore>> pairScores = new HashMap<>();
        for (Map<Long, BigDecimal> productScores : userProductScores.values()) {
            List<Map.Entry<Long, BigDecimal>> entries = new ArrayList<>(productScores.entrySet());
            for (Map.Entry<Long, BigDecimal> entry : entries) {
                double score = entry.getValue().doubleValue();
                productNorms.merge(entry.getKey(), score * score, Double::sum);
            }
            for (int i = 0; i < entries.size(); i++) {
                for (int j = i + 1; j < entries.size(); j++) {
                    Long leftProductId = entries.get(i).getKey();
                    Long rightProductId = entries.get(j).getKey();
                    double dotValue = entries.get(i).getValue().doubleValue() * entries.get(j).getValue().doubleValue();
                    addPairScore(pairScores, leftProductId, rightProductId, dotValue);
                    addPairScore(pairScores, rightProductId, leftProductId, dotValue);
                }
            }
        }

        Map<Long, List<ProductSimilarityScoreVO>> similarities = persistSimilarities(pairScores, productNorms);
        int recommendationCount = persistUserRecommendations(userProductScores, similarities);

        RecommendationCalculateResultVO result = new RecommendationCalculateResultVO();
        result.setBehaviorCount(recommendationMapper.countBehaviors());
        result.setProductCount(productNorms.size());
        result.setUserCount(userProductScores.size());
        result.setSimilarityCount(similarities.values().stream().mapToInt(List::size).sum());
        result.setRecommendationCount(recommendationCount);
        result.setAlgorithmVersion(ALGORITHM_VERSION);
        return result;
    }

    @Override
    public List<RecommendationProductVO> listAdminUserRecommendations(Long userId, Integer limit, HttpServletRequest request) {
        userService.getAdminLoginUser(request);
        requireId(userId, "User id is invalid");
        return defaultList(recommendationMapper.selectUserRecommendations(userId, normalizeLimit(limit)));
    }

    @Override
    public List<RecommendationProductVO> listAdminSimilarProducts(Long productId, Integer limit, HttpServletRequest request) {
        userService.getAdminLoginUser(request);
        return listSimilarProducts(productId, limit);
    }

    private void syncFavoriteBehaviors() {
        List<FavoriteBehaviorSeedVO> favoriteSeeds = defaultList(recommendationMapper.selectMissingFavoriteBehaviorSeeds());
        for (FavoriteBehaviorSeedVO seed : favoriteSeeds) {
            if (seed.getUserId() == null || seed.getProductId() == null || seed.getFavoriteId() == null) {
                continue;
            }
            recommendationMapper.insertBehavior(
                    IdWorker.getId(),
                    seed.getUserId(),
                    seed.getProductId(),
                    null,
                    2,
                    BigDecimal.valueOf(3),
                    2,
                    seed.getFavoriteId()
            );
        }
    }

    private Map<Long, Map<Long, BigDecimal>> buildUserProductScores(List<UserProductBehaviorScoreVO> behaviorScores) {
        Map<Long, Map<Long, BigDecimal>> userProductScores = new LinkedHashMap<>();
        for (UserProductBehaviorScoreVO score : behaviorScores) {
            if (score.getUserId() == null || score.getProductId() == null || score.getBehaviorScore() == null) {
                continue;
            }
            userProductScores
                    .computeIfAbsent(score.getUserId(), key -> new LinkedHashMap<>())
                    .put(score.getProductId(), score.getBehaviorScore());
        }
        return userProductScores;
    }

    private void addPairScore(Map<Long, Map<Long, PairScore>> pairScores,
                              Long productId,
                              Long similarProductId,
                              double dotValue) {
        PairScore pairScore = pairScores
                .computeIfAbsent(productId, key -> new HashMap<>())
                .computeIfAbsent(similarProductId, key -> new PairScore());
        pairScore.dotValue += dotValue;
        pairScore.coBehaviorCount++;
    }

    private Map<Long, List<ProductSimilarityScoreVO>> buildInMemorySimilarities(Map<Long, Map<Long, PairScore>> pairScores,
                                                                                Map<Long, Double> productNorms) {
        Map<Long, List<ProductSimilarityScoreVO>> result = new HashMap<>();
        for (Map.Entry<Long, Map<Long, PairScore>> entry : pairScores.entrySet()) {
            Long productId = entry.getKey();
            List<ProductSimilarityScoreVO> productSimilarities = entry.getValue().entrySet().stream()
                    .map(item -> toSimilarity(productId, item.getKey(), item.getValue(), productNorms))
                    .filter(item -> item.getSimilarityScore().compareTo(BigDecimal.ZERO) > 0)
                    .sorted(Comparator
                            .comparing(ProductSimilarityScoreVO::getSimilarityScore).reversed()
                            .thenComparing(ProductSimilarityScoreVO::getCoBehaviorCount, Comparator.reverseOrder()))
                    .limit(SIMILARITY_KEEP_SIZE)
                    .collect(Collectors.toList());
            result.put(productId, productSimilarities);
        }
        return result;
    }

    private Map<Long, List<ProductSimilarityScoreVO>> persistSimilarities(Map<Long, Map<Long, PairScore>> pairScores,
                                                                          Map<Long, Double> productNorms) {
        Map<Long, List<ProductSimilarityScoreVO>> result = new HashMap<>();
        for (Map.Entry<Long, Map<Long, PairScore>> entry : pairScores.entrySet()) {
            Long productId = entry.getKey();
            List<ProductSimilarityScoreVO> productSimilarities = entry.getValue().entrySet().stream()
                    .map(item -> toSimilarity(productId, item.getKey(), item.getValue(), productNorms))
                    .filter(item -> item.getSimilarityScore().compareTo(BigDecimal.ZERO) > 0)
                    .sorted(Comparator
                            .comparing(ProductSimilarityScoreVO::getSimilarityScore).reversed()
                            .thenComparing(ProductSimilarityScoreVO::getCoBehaviorCount, Comparator.reverseOrder()))
                    .limit(SIMILARITY_KEEP_SIZE)
                    .collect(Collectors.toList());
            for (ProductSimilarityScoreVO similarity : productSimilarities) {
                recommendationMapper.insertProductSimilarity(
                        IdWorker.getId(),
                        similarity.getProductId(),
                        similarity.getSimilarProductId(),
                        similarity.getSimilarityScore(),
                        similarity.getCoBehaviorCount(),
                        ALGORITHM_VERSION
                );
            }
            result.put(productId, productSimilarities);
        }
        return result;
    }

    private ProductSimilarityScoreVO toSimilarity(Long productId,
                                                  Long similarProductId,
                                                  PairScore pairScore,
                                                  Map<Long, Double> productNorms) {
        double leftNorm = Math.sqrt(productNorms.getOrDefault(productId, 0D));
        double rightNorm = Math.sqrt(productNorms.getOrDefault(similarProductId, 0D));
        double similarity = leftNorm == 0D || rightNorm == 0D ? 0D : pairScore.dotValue / (leftNorm * rightNorm);
        ProductSimilarityScoreVO scoreVO = new ProductSimilarityScoreVO();
        scoreVO.setProductId(productId);
        scoreVO.setSimilarProductId(similarProductId);
        scoreVO.setSimilarityScore(BigDecimal.valueOf(similarity).setScale(6, RoundingMode.HALF_UP));
        scoreVO.setCoBehaviorCount(pairScore.coBehaviorCount);
        return scoreVO;
    }

    private int persistUserRecommendations(Map<Long, Map<Long, BigDecimal>> userProductScores,
                                           Map<Long, List<ProductSimilarityScoreVO>> similarities) {
        int count = 0;
        for (Map.Entry<Long, Map<Long, BigDecimal>> userEntry : userProductScores.entrySet()) {
            Long userId = userEntry.getKey();
            Map<Long, BigDecimal> interactedProducts = userEntry.getValue();
            Set<Long> interactedProductIds = new HashSet<>(interactedProducts.keySet());
            Map<Long, BigDecimal> candidateScores = new HashMap<>();
            for (Map.Entry<Long, BigDecimal> productEntry : interactedProducts.entrySet()) {
                List<ProductSimilarityScoreVO> productSimilarities = similarities.getOrDefault(productEntry.getKey(), Collections.emptyList());
                for (ProductSimilarityScoreVO similarity : productSimilarities) {
                    Long candidateProductId = similarity.getSimilarProductId();
                    if (interactedProductIds.contains(candidateProductId)) {
                        continue;
                    }
                    BigDecimal score = productEntry.getValue().multiply(similarity.getSimilarityScore());
                    candidateScores.merge(candidateProductId, score, BigDecimal::add);
                }
            }
            List<Map.Entry<Long, BigDecimal>> rankedCandidates = candidateScores.entrySet().stream()
                    .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                    .limit(USER_RECOMMEND_KEEP_SIZE)
                    .collect(Collectors.toList());
            int rankNo = 1;
            for (Map.Entry<Long, BigDecimal> candidate : rankedCandidates) {
                recommendationMapper.insertUserRecommend(
                        IdWorker.getId(),
                        userId,
                        candidate.getKey(),
                        candidate.getValue().setScale(4, RoundingMode.HALF_UP),
                        "与你近期浏览和购买的作品相似",
                        rankNo++,
                        ALGORITHM_VERSION
                );
                count++;
            }
        }
        return count;
    }

    private Long requireId(Long id, String message) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
        return id;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private Integer normalizeBehaviorType(Integer behaviorType) {
        if (behaviorType == null) {
            return 1;
        }
        if (behaviorType < 1 || behaviorType > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Behavior type is invalid");
        }
        return behaviorType;
    }

    private BigDecimal getBehaviorWeight(Integer behaviorType, Integer staySeconds) {
        return switch (behaviorType) {
            case 2 -> BigDecimal.valueOf(3);
            case 3 -> BigDecimal.valueOf(3.5);
            case 4 -> BigDecimal.valueOf(5);
            case 5 -> BigDecimal.valueOf(4);
            default -> BigDecimal.valueOf(defaultInteger(staySeconds, 0) >= 30 ? 1.5 : 1);
        };
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int defaultInteger(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }

    private static class PairScore {
        private double dotValue;
        private int coBehaviorCount;
    }
}
