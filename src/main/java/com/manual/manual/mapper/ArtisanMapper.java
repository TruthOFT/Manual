package com.manual.manual.mapper;

import com.manual.manual.model.vo.artisan.ArtisanDetailVO;
import com.manual.manual.model.vo.artisan.ArtisanListItemVO;
import com.manual.manual.model.vo.product.ProductListItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArtisanMapper {

    @Select("""
            select
                a.id,
                a.artisanName,
                a.shopName,
                a.artisanAvatar,
                a.coverUrl,
                a.craftCategory,
                a.originPlace,
                a.experienceYears,
                a.supportCustom,
                count(p.id) as productCount
            from artisan_profile a
            left join product p on p.artisanId = a.id
                and p.isDelete = 0
                and p.auditStatus = 1
                and p.status = 1
            where a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
            group by
                a.id,
                a.artisanName,
                a.shopName,
                a.artisanAvatar,
                a.coverUrl,
                a.craftCategory,
                a.originPlace,
                a.experienceYears,
                a.supportCustom
            order by a.experienceYears desc, a.updateTime desc
            """)
    List<ArtisanListItemVO> selectArtisans();

    @Select("""
            select
                a.id,
                a.artisanName,
                a.shopName,
                a.artisanAvatar,
                a.coverUrl,
                a.artisanStory,
                a.craftCategory,
                a.originPlace,
                a.experienceYears,
                a.supportCustom,
                count(p.id) as productCount
            from artisan_profile a
            left join product p on p.artisanId = a.id
                and p.isDelete = 0
                and p.auditStatus = 1
                and p.status = 1
            where a.id = #{artisanId}
              and a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
            group by
                a.id,
                a.artisanName,
                a.shopName,
                a.artisanAvatar,
                a.coverUrl,
                a.artisanStory,
                a.craftCategory,
                a.originPlace,
                a.experienceYears,
                a.supportCustom
            limit 1
            """)
    ArtisanDetailVO selectArtisanDetail(@Param("artisanId") Long artisanId);

    @Select("""
            select
                p.id,
                p.categoryId,
                p.artisanId,
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
                a.artisanName,
                a.shopName,
                c.categoryName
            from product p
            inner join category c on c.id = p.categoryId
            inner join artisan_profile a on a.id = p.artisanId
            where p.artisanId = #{artisanId}
              and p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
              and a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
            order by p.sortOrder desc, p.updateTime desc
            """)
    List<ProductListItemVO> selectArtisanProducts(@Param("artisanId") Long artisanId);
}
