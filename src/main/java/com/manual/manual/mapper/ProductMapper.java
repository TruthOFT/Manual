package com.manual.manual.mapper;

import com.manual.manual.model.vo.product.ProductDetailVO;
import com.manual.manual.model.vo.product.ProductFilterCategoryVO;
import com.manual.manual.model.vo.product.ProductImageVO;
import com.manual.manual.model.vo.product.ProductListItemVO;
import com.manual.manual.model.vo.product.ProductMaterialVO;
import com.manual.manual.model.vo.product.ProductReviewVO;
import com.manual.manual.model.vo.product.ProductSkuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select({
            "<script>",
            "select distinct",
            "    p.id,",
            "    p.categoryId,",
            "    p.artisanId,",
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
            "    a.artisanName,",
            "    a.shopName,",
            "    c.categoryName",
            "from product p",
            "inner join category c on c.id = p.categoryId",
            "inner join artisan_profile a on a.id = p.artisanId",
            "left join product_material pm on pm.productId = p.id and pm.isDelete = 0",
            "where p.isDelete = 0",
            "  and p.auditStatus = 1",
            "  and p.status = 1",
            "  and c.isDelete = 0",
            "  and c.isEnable = 1",
            "  and a.isDelete = 0",
            "  and a.auditStatus = 1",
            "  and a.shelfStatus = 1",
            "<if test='categoryId != null'>",
            "  and p.categoryId = #{categoryId}",
            "</if>",
            "<if test='originPlace != null and originPlace != \"\"'>",
            "  and p.originPlace = #{originPlace}",
            "</if>",
            "<if test='materialName != null and materialName != \"\"'>",
            "  and pm.materialName = #{materialName}",
            "</if>",
            "order by p.sortOrder desc, p.updateTime desc",
            "</script>"
    })
    List<ProductListItemVO> selectProducts(@Param("categoryId") Long categoryId,
                                           @Param("originPlace") String originPlace,
                                           @Param("materialName") String materialName);

    @Select("""
            select distinct
                c.id,
                c.categoryName
            from product p
            inner join category c on c.id = p.categoryId
            inner join artisan_profile a on a.id = p.artisanId
            where p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
              and a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
            order by c.categoryName asc
            """)
    List<ProductFilterCategoryVO> selectFilterCategories();

    @Select("""
            select distinct p.originPlace
            from product p
            inner join category c on c.id = p.categoryId
            inner join artisan_profile a on a.id = p.artisanId
            where p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
              and a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
              and p.originPlace is not null
              and p.originPlace != ''
            order by p.originPlace asc
            """)
    List<String> selectFilterOriginPlaces();

    @Select("""
            select distinct pm.materialName
            from product_material pm
            inner join product p on p.id = pm.productId
            inner join category c on c.id = p.categoryId
            inner join artisan_profile a on a.id = p.artisanId
            where pm.isDelete = 0
              and p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
              and a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
              and pm.materialName is not null
              and pm.materialName != ''
            order by pm.materialName asc
            """)
    List<String> selectFilterMaterials();

    @Select("""
            select
                p.id,
                p.categoryId,
                p.artisanId,
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
                c.categoryName,
                a.artisanName,
                a.shopName,
                a.artisanAvatar
            from product p
            inner join category c on c.id = p.categoryId
            inner join artisan_profile a on a.id = p.artisanId
            where p.id = #{productId}
              and p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
              and a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
            limit 1
            """)
    ProductDetailVO selectProductDetail(@Param("productId") Long productId);

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
}
