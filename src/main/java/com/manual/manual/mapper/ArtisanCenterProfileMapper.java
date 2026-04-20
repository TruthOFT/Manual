package com.manual.manual.mapper;

import com.manual.manual.model.dto.artisancenter.ArtisanCenterProfileUpdateRequest;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterDashboardVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProfileVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArtisanCenterProfileMapper {

    @Select("""
            select
                (select count(1)
                 from product p
                 where p.artisanId = #{artisanId}
                   and p.isDelete = 0
                   and p.auditStatus = 0) as pendingAuditCount,
                (select count(1)
                 from product p
                 where p.artisanId = #{artisanId}
                   and p.isDelete = 0
                   and p.auditStatus = 1
                   and p.status = 1) as onShelfCount,
                (select count(1)
                 from order_item oi
                 inner join orders o on o.id = oi.orderId
                 where oi.artisanId = #{artisanId}
                   and oi.isDelete = 0
                   and o.isDelete = 0
                   and o.orderStatus = 1) as pendingShipmentCount,
                (select count(1)
                 from custom_requirement cr
                 inner join product p on p.id = cr.productId
                 where p.artisanId = #{artisanId}
                   and cr.isDelete = 0
                   and p.isDelete = 0
                   and cr.confirmStatus in (0, 1, 3)) as pendingCustomCount,
                coalesce((select sum(oi.totalAmount)
                          from order_item oi
                          inner join orders o on o.id = oi.orderId
                          where oi.artisanId = #{artisanId}
                            and oi.isDelete = 0
                            and o.isDelete = 0
                            and o.payStatus = 1
                            and o.createTime >= date_sub(now(), interval 7 day)), 0.00) as recentSevenDaysAmount,
                coalesce((select sum(oi.quantity)
                          from order_item oi
                          inner join orders o on o.id = oi.orderId
                          where oi.artisanId = #{artisanId}
                            and oi.isDelete = 0
                            and o.isDelete = 0
                            and o.payStatus = 1
                            and o.createTime >= date_sub(now(), interval 7 day)), 0) as recentSevenDaysSales
            """)
    ArtisanCenterDashboardVO selectDashboard(@Param("artisanId") Long artisanId);

    @Select("""
            select
                id,
                userId,
                artisanName,
                shopName,
                artisanAvatar,
                coverUrl,
                artisanStory,
                craftCategory,
                originPlace,
                experienceYears,
                supportCustom,
                contactPhone,
                auditStatus,
                shelfStatus
            from artisan_profile
            where userId = #{userId}
              and isDelete = 0
            limit 1
            """)
    ArtisanCenterProfileVO selectProfileByUserId(@Param("userId") Long userId);

    @Select("""
            select id
            from artisan_profile
            where userId = #{userId}
              and isDelete = 0
            limit 1
            """)
    Long selectArtisanIdByUserId(@Param("userId") Long userId);

    @Update("""
            update artisan_profile
            set artisanName = #{request.artisanName},
                shopName = #{request.shopName},
                artisanAvatar = #{request.artisanAvatar},
                coverUrl = #{request.coverUrl},
                artisanStory = #{request.artisanStory},
                craftCategory = #{request.craftCategory},
                originPlace = #{request.originPlace},
                experienceYears = #{request.experienceYears},
                supportCustom = #{request.supportCustom},
                contactPhone = #{request.contactPhone},
                updateTime = now()
            where id = #{artisanId}
              and isDelete = 0
            """)
    int updateProfile(@Param("artisanId") Long artisanId,
                      @Param("request") ArtisanCenterProfileUpdateRequest request);
}
