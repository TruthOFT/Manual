package com.manual.manual.mapper;

import com.manual.manual.model.dto.artisancenter.ArtisanCenterProductSaveRequest;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCategoryVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductSkuVO;
import com.manual.manual.model.vo.product.ProductImageVO;
import com.manual.manual.model.vo.product.ProductMaterialVO;
import com.manual.manual.model.vo.product.ProductReviewVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ArtisanCenterProductMapper {

    @Select("""
            select
                c.id,
                c.parentId,
                p.categoryName as parentName,
                c.categoryName,
                c.categoryLevel
            from category c
            left join category p on p.id = c.parentId and p.isDelete = 0
            where c.isDelete = 0
              and c.isEnable = 1
            order by c.categoryLevel desc, c.sortOrder asc, c.id asc
            """)
    List<ArtisanCenterCategoryVO> selectProductCategories();

    @Select({
            "<script>",
            "select",
            "    p.id,",
            "    p.categoryId,",
            "    c.categoryName,",
            "    p.productName,",
            "    p.productSubtitle,",
            "    p.productCover,",
            "    p.inventory,",
            "    p.minPrice,",
            "    p.maxPrice,",
            "    p.auditStatus,",
            "    p.status,",
            "    p.reviewCount,",
            "    date_format(p.createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(p.updateTime, '%Y-%m-%d %H:%i') as updateTime",
            "from product p",
            "left join category c on c.id = p.categoryId",
            "where p.artisanId = #{artisanId}",
            "  and p.isDelete = 0",
            "<if test='auditStatus != null'>",
            "  and p.auditStatus = #{auditStatus}",
            "</if>",
            "<if test='status != null'>",
            "  and p.status = #{status}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (p.productName like concat('%', #{keyword}, '%')",
            "       or p.productSubtitle like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by p.updateTime desc, p.id desc",
            "</script>"
    })
    List<ArtisanCenterProductListItemVO> selectProducts(@Param("artisanId") Long artisanId,
                                                        @Param("auditStatus") Integer auditStatus,
                                                        @Param("status") Integer status,
                                                        @Param("keyword") String keyword);

    @Select("""
            select
                p.id,
                p.categoryId,
                p.artisanId,
                c.categoryName,
                a.artisanName,
                a.shopName,
                p.productName,
                p.productSubtitle,
                p.productCover,
                p.productDesc,
                p.craftType,
                p.materialDesc,
                p.originPlace,
                p.handmadeCycleDays,
                p.supportCustom,
                p.inventory,
                p.soldQuantity,
                p.favoriteCount,
                p.reviewCount,
                p.auditStatus,
                p.status,
                p.sortOrder,
                p.minPrice,
                p.maxPrice
            from product p
            inner join artisan_profile a on a.id = p.artisanId
            left join category c on c.id = p.categoryId
            where p.id = #{productId}
              and p.artisanId = #{artisanId}
              and p.isDelete = 0
            limit 1
            """)
    ArtisanCenterProductDetailVO selectProductDetail(@Param("artisanId") Long artisanId,
                                                     @Param("productId") Long productId);

    @Select("""
            select
                p.id,
                p.categoryId,
                c.categoryName,
                p.productName,
                p.productSubtitle,
                p.productCover,
                p.inventory,
                p.minPrice,
                p.maxPrice,
                p.auditStatus,
                p.status,
                p.reviewCount,
                date_format(p.createTime, '%Y-%m-%d %H:%i') as createTime,
                date_format(p.updateTime, '%Y-%m-%d %H:%i') as updateTime
            from product p
            left join category c on c.id = p.categoryId
            where p.id = #{productId}
              and p.artisanId = #{artisanId}
              and p.isDelete = 0
            limit 1
            """)
    ArtisanCenterProductListItemVO selectOwnedProductSummary(@Param("artisanId") Long artisanId,
                                                             @Param("productId") Long productId);

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
                status
            from product_sku
            where productId = #{productId}
              and isDelete = 0
            order by id asc
            """)
    List<ArtisanCenterProductSkuVO> selectProductSkus(@Param("productId") Long productId);

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

    @Insert("""
            insert into product (
                id,
                categoryId,
                artisanId,
                productName,
                productSubtitle,
                productCover,
                productDesc,
                craftType,
                materialDesc,
                originPlace,
                handmadeCycleDays,
                supportCustom,
                inventory,
                soldQuantity,
                favoriteCount,
                reviewCount,
                minPrice,
                maxPrice,
                auditStatus,
                status,
                sortOrder,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{productId},
                #{request.categoryId},
                #{artisanId},
                #{request.productName},
                #{request.productSubtitle},
                #{request.productCover},
                #{request.productDesc},
                #{request.craftType},
                #{request.materialDesc},
                #{request.originPlace},
                #{request.handmadeCycleDays},
                #{request.supportCustom},
                #{inventory},
                0,
                0,
                0,
                #{minPrice},
                #{maxPrice},
                #{auditStatus},
                #{status},
                #{request.sortOrder},
                now(),
                now(),
                0
            )
            """)
    int insertProduct(@Param("productId") Long productId,
                      @Param("artisanId") Long artisanId,
                      @Param("request") ArtisanCenterProductSaveRequest request,
                      @Param("inventory") Integer inventory,
                      @Param("minPrice") BigDecimal minPrice,
                      @Param("maxPrice") BigDecimal maxPrice,
                      @Param("auditStatus") Integer auditStatus,
                      @Param("status") Integer status);

    @Update("""
            update product
            set categoryId = #{request.categoryId},
                productName = #{request.productName},
                productSubtitle = #{request.productSubtitle},
                productCover = #{request.productCover},
                productDesc = #{request.productDesc},
                craftType = #{request.craftType},
                materialDesc = #{request.materialDesc},
                originPlace = #{request.originPlace},
                handmadeCycleDays = #{request.handmadeCycleDays},
                supportCustom = #{request.supportCustom},
                inventory = #{inventory},
                minPrice = #{minPrice},
                maxPrice = #{maxPrice},
                sortOrder = #{request.sortOrder},
                updateTime = now()
            where id = #{productId}
              and artisanId = #{artisanId}
              and isDelete = 0
            """)
    int updateProduct(@Param("productId") Long productId,
                      @Param("artisanId") Long artisanId,
                      @Param("request") ArtisanCenterProductSaveRequest request,
                      @Param("inventory") Integer inventory,
                      @Param("minPrice") BigDecimal minPrice,
                      @Param("maxPrice") BigDecimal maxPrice);

    @Update("""
            update product_image
            set isDelete = 1,
                updateTime = now()
            where productId = #{productId}
              and isDelete = 0
            """)
    int markProductImagesDeleted(@Param("productId") Long productId);

    @Update("""
            update product_material
            set isDelete = 1,
                updateTime = now()
            where productId = #{productId}
              and isDelete = 0
            """)
    int markProductMaterialsDeleted(@Param("productId") Long productId);

    @Update("""
            update product_sku
            set isDelete = 1,
                updateTime = now()
            where productId = #{productId}
              and isDelete = 0
            """)
    int markProductSkusDeleted(@Param("productId") Long productId);

    @Insert("""
            insert into product_image (
                id,
                productId,
                imageUrl,
                imageType,
                sortOrder,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{productId},
                #{imageUrl},
                #{imageType},
                #{sortOrder},
                now(),
                now(),
                0
            )
            """)
    int insertProductImage(@Param("id") Long id,
                           @Param("productId") Long productId,
                           @Param("imageUrl") String imageUrl,
                           @Param("imageType") String imageType,
                           @Param("sortOrder") Integer sortOrder);

    @Insert("""
            insert into product_material (
                id,
                productId,
                materialName,
                materialOrigin,
                materialRatio,
                sortOrder,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{productId},
                #{materialName},
                #{materialOrigin},
                #{materialRatio},
                #{sortOrder},
                now(),
                now(),
                0
            )
            """)
    int insertProductMaterial(@Param("id") Long id,
                              @Param("productId") Long productId,
                              @Param("materialName") String materialName,
                              @Param("materialOrigin") String materialOrigin,
                              @Param("materialRatio") String materialRatio,
                              @Param("sortOrder") Integer sortOrder);

    @Insert("""
            insert into product_sku (
                id,
                productId,
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
                status,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{productId},
                #{skuCode},
                #{skuName},
                #{skuCover},
                #{specText},
                #{materialType},
                #{weight},
                #{price},
                #{originalPrice},
                #{stock},
                0,
                #{status},
                now(),
                now(),
                0
            )
            """)
    int insertProductSku(@Param("id") Long id,
                         @Param("productId") Long productId,
                         @Param("skuCode") String skuCode,
                         @Param("skuName") String skuName,
                         @Param("skuCover") String skuCover,
                         @Param("specText") String specText,
                         @Param("materialType") String materialType,
                         @Param("weight") BigDecimal weight,
                         @Param("price") BigDecimal price,
                         @Param("originalPrice") BigDecimal originalPrice,
                         @Param("stock") Integer stock,
                         @Param("status") Integer status);

    @Update("""
            update product
            set auditStatus = #{auditStatus},
                updateTime = now()
            where id = #{productId}
              and artisanId = #{artisanId}
              and isDelete = 0
            """)
    int updateProductAuditStatus(@Param("artisanId") Long artisanId,
                                 @Param("productId") Long productId,
                                 @Param("auditStatus") Integer auditStatus);

    @Update("""
            update product
            set status = #{status},
                updateTime = now()
            where id = #{productId}
              and artisanId = #{artisanId}
              and isDelete = 0
            """)
    int updateProductStatus(@Param("artisanId") Long artisanId,
                            @Param("productId") Long productId,
                            @Param("status") Integer status);
}
