package com.manual.manual.mapper;

import com.manual.manual.model.vo.home.HomeArtisanVO;
import com.manual.manual.model.vo.home.HomeCategoryVO;
import com.manual.manual.model.vo.home.HomeProductVO;
import com.manual.manual.model.vo.home.HomeRecentOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HomeMapper {

    @Select("""
            select
                id,
                parentId,
                categoryName,
                categoryIcon,
                categoryDesc,
                categoryLevel
            from category
            where isDelete = 0
              and isEnable = 1
              and categoryLevel = 1
            order by sortOrder asc, createTime asc
            """)
    List<HomeCategoryVO> selectCategories();

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
            where p.isDelete = 0
              and p.auditStatus = 1
              and p.status = 1
              and c.isDelete = 0
              and c.isEnable = 1
              and a.isDelete = 0
              and a.auditStatus = 1
              and a.shelfStatus = 1
            order by p.sortOrder desc, p.updateTime desc
            """)
    List<HomeProductVO> selectProducts();

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
    List<HomeArtisanVO> selectArtisans();

    @Select("""
            select
                oi.id,
                o.orderNo,
                oi.productName,
                oi.skuName,
                oi.productCover,
                oi.quantity,
                oi.totalAmount,
                date_format(coalesce(o.finishTime, o.payTime, o.createTime), '%Y-%m-%d %H:%i') as finishTime
            from orders o
            inner join order_item oi on oi.orderId = o.id
            where o.isDelete = 0
              and oi.isDelete = 0
              and o.payStatus = 1
            order by coalesce(o.finishTime, o.payTime, o.createTime) desc
            limit 3
            """)
    List<HomeRecentOrderVO> selectRecentOrders();
}
