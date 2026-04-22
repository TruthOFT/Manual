package com.manual.manual.mapper;

import com.manual.manual.model.dto.product.AdminProductSaveRequest;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductSkuVO;
import com.manual.manual.model.vo.product.AdminProductArtisanOptionVO;
import com.manual.manual.model.vo.product.AdminProductCategoryOptionVO;
import com.manual.manual.model.vo.product.AdminProductDetailVO;
import com.manual.manual.model.vo.product.AdminProductListItemVO;
import com.manual.manual.model.vo.product.ProductImageVO;
import com.manual.manual.model.vo.product.ProductMaterialVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminProductMapper {

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
            order by c.categoryLevel asc, c.sortOrder asc, c.id asc
            """)
    List<AdminProductCategoryOptionVO> selectAdminProductCategories();

    @Select("""
            select
                a.id,
                a.userId,
                a.artisanName,
                a.shopName,
                u.userAccount,
                u.userName
            from artisan_profile a
            inner join users u on u.id = a.userId and u.isDelete = 0
            where a.isDelete = 0
              and a.auditStatus = 1
            order by a.updateTime desc, a.id desc
            """)
    List<AdminProductArtisanOptionVO> selectAdminProductArtisans();

    @Select({
            "<script>",
            "select",
            "    p.id,",
            "    p.categoryId,",
            "    p.artisanId,",
            "    c.categoryName,",
            "    a.artisanName,",
            "    a.shopName,",
            "    p.productName,",
            "    p.productSubtitle,",
            "    p.productCover,",
            "    p.inventory,",
            "    p.minPrice,",
            "    p.maxPrice,",
            "    p.auditStatus,",
            "    p.status,",
            "    date_format(p.createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(p.updateTime, '%Y-%m-%d %H:%i') as updateTime",
            "from product p",
            "left join category c on c.id = p.categoryId and c.isDelete = 0",
            "left join artisan_profile a on a.id = p.artisanId and a.isDelete = 0",
            "where p.isDelete = 0",
            "<if test='auditStatus != null'>",
            "  and p.auditStatus = #{auditStatus}",
            "</if>",
            "<if test='status != null'>",
            "  and p.status = #{status}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (p.productName like concat('%', #{keyword}, '%')",
            "       or p.productSubtitle like concat('%', #{keyword}, '%')",
            "       or a.artisanName like concat('%', #{keyword}, '%')",
            "       or c.categoryName like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by case when p.auditStatus = 0 then 0 else 1 end asc, p.updateTime desc, p.id desc",
            "</script>"
    })
    List<AdminProductListItemVO> selectAdminProducts(@Param("auditStatus") Integer auditStatus,
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
            left join category c on c.id = p.categoryId and c.isDelete = 0
            left join artisan_profile a on a.id = p.artisanId and a.isDelete = 0
            where p.id = #{productId}
              and p.isDelete = 0
            limit 1
            """)
    AdminProductDetailVO selectAdminProductDetail(@Param("productId") Long productId);

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
            select count(1)
            from category
            where id = #{categoryId}
              and isDelete = 0
              and isEnable = 1
            """)
    int countEnabledCategoryById(@Param("categoryId") Long categoryId);

    @Select("""
            select count(1)
            from artisan_profile
            where id = #{artisanId}
              and isDelete = 0
              and auditStatus = 1
            """)
    int countEnabledArtisanById(@Param("artisanId") Long artisanId);

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
                #{request.artisanId},
                #{request.productName},
                #{request.productSubtitle},
                #{request.productCover},
                #{request.productDesc},
                #{request.craftType},
                #{request.materialDesc},
                #{request.originPlace},
                #{request.handmadeCycleDays},
                #{request.supportCustom},
                #{request.inventory},
                0,
                0,
                0,
                #{request.minPrice},
                #{request.maxPrice},
                #{request.auditStatus},
                #{request.status},
                #{request.sortOrder},
                now(),
                now(),
                0
            )
            """)
    int insertAdminProduct(@Param("productId") Long productId,
                           @Param("request") AdminProductSaveRequest request);

    @Update("""
            update product
            set categoryId = #{request.categoryId},
                artisanId = #{request.artisanId},
                productName = #{request.productName},
                productSubtitle = #{request.productSubtitle},
                productCover = #{request.productCover},
                productDesc = #{request.productDesc},
                craftType = #{request.craftType},
                materialDesc = #{request.materialDesc},
                originPlace = #{request.originPlace},
                handmadeCycleDays = #{request.handmadeCycleDays},
                supportCustom = #{request.supportCustom},
                inventory = #{request.inventory},
                minPrice = #{request.minPrice},
                maxPrice = #{request.maxPrice},
                auditStatus = #{request.auditStatus},
                status = #{request.status},
                sortOrder = #{request.sortOrder},
                updateTime = now()
            where id = #{productId}
              and isDelete = 0
            """)
    int updateAdminProduct(@Param("productId") Long productId,
                           @Param("request") AdminProductSaveRequest request);

    @Update("""
            update product
            set isDelete = 1,
                updateTime = now()
            where id = #{productId}
              and isDelete = 0
            """)
    int deleteAdminProduct(@Param("productId") Long productId);

    @Update("""
            update product_image
            set isDelete = 1,
                updateTime = now()
            where productId = #{productId}
              and isDelete = 0
            """)
    int deleteAdminProductImages(@Param("productId") Long productId);

    @Update("""
            update product_material
            set isDelete = 1,
                updateTime = now()
            where productId = #{productId}
              and isDelete = 0
            """)
    int deleteAdminProductMaterials(@Param("productId") Long productId);

    @Update("""
            update product_sku
            set isDelete = 1,
                updateTime = now()
            where productId = #{productId}
              and isDelete = 0
            """)
    int deleteAdminProductSkus(@Param("productId") Long productId);

    @Update("""
            update product
            set auditStatus = #{auditStatus},
                status = #{status},
                updateTime = now()
            where id = #{productId}
              and isDelete = 0
            """)
    int updateProductAudit(@Param("productId") Long productId,
                           @Param("auditStatus") Integer auditStatus,
                           @Param("status") Integer status);
}
