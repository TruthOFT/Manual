package com.manual.manual.mapper;

import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductFavoriteVO;
import com.manual.manual.model.vo.product.ProductFilterCategoryVO;
import com.manual.manual.model.vo.product.ProductImageVO;
import com.manual.manual.model.vo.product.ProductListItemVO;
import com.manual.manual.model.vo.product.ProductMaterialVO;
import com.manual.manual.model.vo.product.ProductReviewVO;
import com.manual.manual.model.vo.product.ProductSkuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select({
            "<script>",
            "select distinct",
            "    p.id,",
            "    p.categoryId,",
            "    p.productName,",
            "    p.productSubtitle,",
            "    p.productCover,",
            "    p.craftType,",
            "    p.originPlace,",
            "    p.handmadeCycleDays,",
            "    p.supportCustom,",
            "    p.soldQuantity,",
            "    p.minPrice,",
            "    p.maxPrice,",
            "    c.categoryName,",
            "    p.sortOrder,",
            "    p.updateTime",
            "from product p",
            "inner join category c on c.id = p.categoryId",
            "where p.isDelete = 0",
            "  and c.isDelete = 0",
            "  and c.isEnable = 1",
            "<if test='publishedOnly'>",
            "  and p.auditStatus = 1",
            "  and p.status = 1",
            "</if>",
            "<if test='categoryId != null'>",
            "  and p.categoryId = #{categoryId}",
            "</if>",
            "<if test='originPlace != null and originPlace != \"\"'>",
            "  and p.originPlace = #{originPlace}",
            "</if>",
            "<if test='minPrice != null'>",
            "  and p.minPrice >= #{minPrice}",
            "</if>",
            "<if test='maxPrice != null'>",
            "  and p.maxPrice &lt;= #{maxPrice}",
            "</if>",
            "order by p.sortOrder desc, p.updateTime desc",
            "</script>"
    })
    List<ProductListItemVO> selectProducts(@Param("categoryId") Long categoryId,
                                           @Param("originPlace") String originPlace,
                                           @Param("minPrice") Integer minPrice,
                                           @Param("maxPrice") Integer maxPrice,
                                           @Param("publishedOnly") boolean publishedOnly);

    @Select("""
            select count(1)
            from product
            where id = #{productId}
              and isDelete = 0
              and auditStatus = 1
              and status = 1
            """)
    int countPublishedProduct(@Param("productId") Long productId);

    @Select({
            "<script>",
            """
            select distinct
                c.id,
                c.categoryName
            from product p
            inner join category c on c.id = p.categoryId
            where p.isDelete = 0
              and c.isDelete = 0
              and c.isEnable = 1
            """,
            "<if test='publishedOnly'>",
            "  and p.auditStatus = 1",
            "  and p.status = 1",
            "</if>",
            """
            order by c.categoryName asc
            """,
            "</script>"
    })
    List<ProductFilterCategoryVO> selectFilterCategories(@Param("publishedOnly") boolean publishedOnly);

    @Select({
            "<script>",
            """
            select distinct p.originPlace
            from product p
            inner join category c on c.id = p.categoryId
            where p.isDelete = 0
              and c.isDelete = 0
              and c.isEnable = 1
              and p.originPlace is not null
              and p.originPlace != ''
            """,
            "<if test='publishedOnly'>",
            "  and p.auditStatus = 1",
            "  and p.status = 1",
            "</if>",
            """
            order by p.originPlace asc
            """,
            "</script>"
    })
    List<String> selectFilterOriginPlaces(@Param("publishedOnly") boolean publishedOnly);

    @Select({
            "<script>",
            """
            select distinct sku.materialType
            from product_sku sku
            inner join product p on p.id = sku.productId
            inner join category c on c.id = p.categoryId
            where sku.isDelete = 0
              and p.isDelete = 0
              and c.isDelete = 0
              and c.isEnable = 1
              and sku.status = 1
              and sku.materialType is not null
              and sku.materialType != ''
            """,
            "<if test='publishedOnly'>",
            "  and p.auditStatus = 1",
            "  and p.status = 1",
            "</if>",
            """
            order by sku.materialType asc
            """,
            "</script>"
    })
    List<String> selectFilterMaterials(@Param("publishedOnly") boolean publishedOnly);

    @Select({
            "<script>",
            """
            select
                p.id,
                p.categoryId,
                p.productName,
                p.productSubtitle,
                p.productCover,
                p.productDesc,
                p.craftType,
                p.materialDesc,
                p.originPlace,
                p.handmadeCycleDays,
                p.supportCustom,
                p.soldQuantity,
                p.favoriteCount,
                p.reviewCount,
                p.minPrice,
                p.maxPrice,
                c.categoryName
            from product p
            inner join category c on c.id = p.categoryId
            where p.id = #{productId}
              and p.isDelete = 0
              and c.isDelete = 0
              and c.isEnable = 1
            """,
            "<if test='publishedOnly'>",
            "  and p.auditStatus = 1",
            "  and p.status = 1",
            "</if>",
            """
            limit 1
            """,
            "</script>"
    })
    ProductDetailVO selectProductDetail(@Param("productId") Long productId,
                                        @Param("publishedOnly") boolean publishedOnly);

    @Select("""
            select
                id,
                imageUrl,
                imageType
            from product_image
            where productId = #{productId}
              and isDelete = 0
            order by sortOrder asc, id asc
            """)
    List<ProductImageVO> selectProductImages(@Param("productId") Long productId);

    @Select("""
            select
                id,
                materialName,
                materialOrigin,
                materialRatio
            from product_material
            where productId = #{productId}
              and isDelete = 0
            order by sortOrder asc, id asc
            """)
    List<ProductMaterialVO> selectProductMaterials(@Param("productId") Long productId);

    @Select("""
            select
                id,
                skuCode,
                skuName,
                skuCover,
                specText,
                materialType,
                weight,
                price,
                originalPrice,
                stock,
                lockedStock,
                status
            from product_sku
            where productId = #{productId}
              and isDelete = 0
              and status = 1
            order by id asc
            """)
    List<ProductSkuVO> selectProductSkus(@Param("productId") Long productId);

    @Select("""
            select
                id,
                score,
                reviewContent,
                reviewImages,
                isAnonymous,
                replyContent,
                date_format(replyTime, '%Y-%m-%d %H:%i') as replyTime,
                date_format(createTime, '%Y-%m-%d %H:%i') as createTime
            from product_review
            where productId = #{productId}
              and isDelete = 0
              and status = 1
            order by createTime desc, id desc
            """)
    List<ProductReviewVO> selectProductReviews(@Param("productId") Long productId);

    @Select("""
            select count(1)
            from product_favorite
            where userId = #{userId}
              and productId = #{productId}
              and isDelete = 0
            """)
    int countUserFavorite(@Param("userId") Long userId,
                          @Param("productId") Long productId);

    @Select("""
            select count(1)
            from product_favorite
            where userId = #{userId}
              and productId = #{productId}
            """)
    int countUserFavoriteRecord(@Param("userId") Long userId,
                                @Param("productId") Long productId);

    @Select("""
            select id
            from product_favorite
            where userId = #{userId}
              and productId = #{productId}
            limit 1
            """)
    Long selectFavoriteId(@Param("userId") Long userId,
                          @Param("productId") Long productId);

    @Insert("""
            insert into product_favorite (
                id,
                userId,
                productId,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{userId},
                #{productId},
                now(),
                now(),
                0
            )
            """)
    int insertFavorite(@Param("id") Long id,
                       @Param("userId") Long userId,
                       @Param("productId") Long productId);

    @Update("""
            update product_favorite
            set isDelete = 0,
                updateTime = now()
            where userId = #{userId}
              and productId = #{productId}
            """)
    int restoreFavorite(@Param("userId") Long userId,
                        @Param("productId") Long productId);

    @Update("""
            update product_favorite
            set isDelete = 1,
                updateTime = now()
            where userId = #{userId}
              and productId = #{productId}
              and isDelete = 0
            """)
    int deleteFavorite(@Param("userId") Long userId,
                       @Param("productId") Long productId);

    @Update("""
            update product
            set favoriteCount = favoriteCount + 1,
                updateTime = now()
            where id = #{productId}
              and isDelete = 0
            """)
    int increaseFavoriteCount(@Param("productId") Long productId);

    @Update("""
            update product
            set favoriteCount = case
                    when favoriteCount > 0 then favoriteCount - 1
                    else 0
                end,
                updateTime = now()
            where id = #{productId}
              and isDelete = 0
            """)
    int decreaseFavoriteCount(@Param("productId") Long productId);

    @Select("""
            select
                pf.id,
                pf.productId,
                p.productName,
                p.productSubtitle,
                p.productCover,
                p.craftType,
                p.originPlace,
                p.minPrice,
                p.maxPrice,
                c.categoryName,
                date_format(pf.createTime, '%Y-%m-%d %H:%i') as createTime
            from product_favorite pf
            inner join product p on p.id = pf.productId
            inner join category c on c.id = p.categoryId
            where pf.userId = #{userId}
              and pf.isDelete = 0
              and p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
            order by pf.updateTime desc, pf.id desc
            """)
    List<ProductFavoriteVO> selectUserFavorites(@Param("userId") Long userId);
}
