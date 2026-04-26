package com.manual.manual.mapper;

import com.manual.manual.model.vo.recommendation.RecommendationOverviewVO;
import com.manual.manual.model.vo.recommendation.RecommendationProductVO;
import com.manual.manual.model.vo.recommendation.FavoriteBehaviorSeedVO;
import com.manual.manual.model.vo.recommendation.UserProductBehaviorScoreVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface RecommendationMapper {

    @Select("""
            select
                userId,
                productId,
                sum(behaviorWeight) as behaviorScore
            from user_product_behavior
            where isDelete = 0
            group by userId, productId
            having behaviorScore > 0
            """)
    List<UserProductBehaviorScoreVO> selectBehaviorScores();

    @Select("""
            select count(1)
            from user_product_behavior
            where isDelete = 0
            """)
    Long countBehaviors();

    @Select("""
            select
                pf.userId,
                pf.productId,
                pf.id as favoriteId
            from product_favorite pf
            inner join product p on p.id = pf.productId
            where pf.isDelete = 0
              and p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and not exists (
                  select 1
                  from user_product_behavior upb
                  where upb.userId = pf.userId
                    and upb.productId = pf.productId
                    and upb.behaviorType = 2
                    and upb.sourceType = 2
                    and upb.isDelete = 0
              )
            """)
    List<FavoriteBehaviorSeedVO> selectMissingFavoriteBehaviorSeeds();

    @Delete("""
            delete from product_similarity
            where algorithmVersion = #{algorithmVersion}
            """)
    int deactivateProductSimilarities(@Param("algorithmVersion") String algorithmVersion);

    @Delete("""
            delete from user_product_recommend
            where algorithmVersion = #{algorithmVersion}
            """)
    int deactivateUserRecommendations(@Param("algorithmVersion") String algorithmVersion);

    @Delete("""
            delete from user_product_recommend
            where userId = #{userId}
              and algorithmVersion = #{algorithmVersion}
            """)
    int deleteUserRecommendations(@Param("userId") Long userId,
                                  @Param("algorithmVersion") String algorithmVersion);

    @Insert("""
            insert into product_similarity (
                id,
                productId,
                similarProductId,
                similarityScore,
                coBehaviorCount,
                algorithmVersion,
                calculatedTime,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{productId},
                #{similarProductId},
                #{similarityScore},
                #{coBehaviorCount},
                #{algorithmVersion},
                now(),
                now(),
                now(),
                0
            )
            """)
    int insertProductSimilarity(@Param("id") Long id,
                                @Param("productId") Long productId,
                                @Param("similarProductId") Long similarProductId,
                                @Param("similarityScore") BigDecimal similarityScore,
                                @Param("coBehaviorCount") Integer coBehaviorCount,
                                @Param("algorithmVersion") String algorithmVersion);

    @Insert("""
            insert into user_product_recommend (
                id,
                userId,
                productId,
                recommendScore,
                recommendReason,
                rankNo,
                algorithmVersion,
                expireTime,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{userId},
                #{productId},
                #{recommendScore},
                #{recommendReason},
                #{rankNo},
                #{algorithmVersion},
                date_add(now(), interval 7 day),
                now(),
                now(),
                0
            )
            """)
    int insertUserRecommend(@Param("id") Long id,
                            @Param("userId") Long userId,
                            @Param("productId") Long productId,
                            @Param("recommendScore") BigDecimal recommendScore,
                            @Param("recommendReason") String recommendReason,
                            @Param("rankNo") Integer rankNo,
                            @Param("algorithmVersion") String algorithmVersion);

    @Select("""
            select
                p.id,
                p.categoryId,
                p.productName,
                p.productSubtitle,
                p.productCover,
                p.craftType,
                p.originPlace,
                p.handmadeCycleDays,
                p.supportCustom,
                p.soldQuantity,
                p.minPrice,
                p.maxPrice,
                c.categoryName,
                upr.recommendScore,
                upr.recommendReason,
                upr.rankNo,
                upr.algorithmVersion
            from user_product_recommend upr
            inner join product p on p.id = upr.productId
            inner join category c on c.id = p.categoryId
            where upr.userId = #{userId}
              and upr.isDelete = 0
              and (upr.expireTime is null or upr.expireTime > now())
              and p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
            order by upr.rankNo asc, upr.recommendScore desc, upr.updateTime desc
            limit #{limit}
            """)
    List<RecommendationProductVO> selectUserRecommendations(@Param("userId") Long userId,
                                                            @Param("limit") Integer limit);

    @Select("""
            select
                p.id,
                p.categoryId,
                p.productName,
                p.productSubtitle,
                p.productCover,
                p.craftType,
                p.originPlace,
                p.handmadeCycleDays,
                p.supportCustom,
                p.soldQuantity,
                p.minPrice,
                p.maxPrice,
                c.categoryName,
                ps.similarityScore,
                ps.coBehaviorCount,
                ps.algorithmVersion,
                date_format(ps.calculatedTime, '%Y-%m-%d %H:%i') as calculatedTime
            from product_similarity ps
            inner join product p on p.id = ps.similarProductId
            inner join category c on c.id = p.categoryId
            where ps.productId = #{productId}
              and ps.isDelete = 0
              and p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
            order by ps.similarityScore desc, ps.coBehaviorCount desc, p.soldQuantity desc
            limit #{limit}
            """)
    List<RecommendationProductVO> selectSimilarProducts(@Param("productId") Long productId,
                                                        @Param("limit") Integer limit);

    @Select("""
            select
                p.id,
                p.categoryId,
                p.productName,
                p.productSubtitle,
                p.productCover,
                p.craftType,
                p.originPlace,
                p.handmadeCycleDays,
                p.supportCustom,
                p.soldQuantity,
                p.minPrice,
                p.maxPrice,
                c.categoryName,
                cast((p.soldQuantity + p.favoriteCount + p.reviewCount) as decimal(10, 4)) as recommendScore,
                '近期热销作品' as recommendReason,
                0 as rankNo
            from product p
            inner join category c on c.id = p.categoryId
            where p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
            order by p.soldQuantity desc, p.favoriteCount desc, p.updateTime desc
            limit #{limit}
            """)
    List<RecommendationProductVO> selectHotProducts(@Param("limit") Integer limit);

    @Insert("""
            insert into user_product_behavior (
                id,
                userId,
                productId,
                skuId,
                behaviorType,
                behaviorWeight,
                behaviorTime,
                sourceType,
                sourceId,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{userId},
                #{productId},
                #{skuId},
                #{behaviorType},
                #{behaviorWeight},
                now(),
                #{sourceType},
                #{sourceId},
                now(),
                now(),
                0
            )
            """)
    int insertBehavior(@Param("id") Long id,
                       @Param("userId") Long userId,
                       @Param("productId") Long productId,
                       @Param("skuId") Long skuId,
                       @Param("behaviorType") Integer behaviorType,
                       @Param("behaviorWeight") BigDecimal behaviorWeight,
                       @Param("sourceType") Integer sourceType,
                       @Param("sourceId") Long sourceId);

    @Update("""
            update user_product_behavior
            set isDelete = 1,
                updateTime = now()
            where userId = #{userId}
              and productId = #{productId}
              and behaviorType = #{behaviorType}
              and sourceType = #{sourceType}
              and isDelete = 0
              and (
                  sourceId = #{sourceId}
                  or #{sourceId} is null
                  or (#{sourceId} is not null and sourceId is null)
              )
            """)
    int deactivateBehavior(@Param("userId") Long userId,
                           @Param("productId") Long productId,
                           @Param("behaviorType") Integer behaviorType,
                           @Param("sourceType") Integer sourceType,
                           @Param("sourceId") Long sourceId);

    @Insert("""
            insert into product_browse_log (
                id,
                userId,
                productId,
                sourcePage,
                staySeconds,
                deviceType,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{userId},
                #{productId},
                #{sourcePage},
                #{staySeconds},
                #{deviceType},
                now(),
                now(),
                0
            )
            """)
    int insertBrowseLog(@Param("id") Long id,
                        @Param("userId") Long userId,
                        @Param("productId") Long productId,
                        @Param("sourcePage") String sourcePage,
                        @Param("staySeconds") Integer staySeconds,
                        @Param("deviceType") String deviceType);

    @Select("""
            select
                (select count(1) from user_product_behavior where isDelete = 0) as behaviorCount,
                (select count(1) from user_product_behavior where isDelete = 0 and behaviorType = 1) as browseBehaviorCount,
                (select count(1) from user_product_behavior where isDelete = 0 and behaviorType = 4) as orderBehaviorCount,
                (select count(1) from product_similarity where isDelete = 0) as similarityCount,
                (select count(1) from user_product_recommend where isDelete = 0 and (expireTime is null or expireTime > now())) as recommendationCount,
                (select count(distinct userId) from user_product_recommend where isDelete = 0 and (expireTime is null or expireTime > now())) as coveredUserCount,
                (select date_format(max(calculatedTime), '%Y-%m-%d %H:%i') from product_similarity where isDelete = 0) as latestCalculatedTime
            """)
    RecommendationOverviewVO selectOverview();
}
