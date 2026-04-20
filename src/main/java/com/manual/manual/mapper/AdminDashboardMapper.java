package com.manual.manual.mapper;

import com.manual.manual.model.vo.dashboard.AdminDashboardOverviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminDashboardMapper {

    @Select("""
            select
                (select count(1)
                 from product
                 where isDelete = 0
                   and auditStatus = 0) as pendingAuditProductCount,
                (select count(1)
                 from orders
                 where isDelete = 0
                   and orderStatus = 1
                   and deliveryStatus = 0) as pendingShipmentOrderCount,
                (select count(1)
                 from orders
                 where isDelete = 0
                   and (
                       trim(ifnull(receiverName, '')) = ''
                       or trim(ifnull(receiverPhone, '')) = ''
                       or trim(ifnull(province, '')) = ''
                       or trim(ifnull(city, '')) = ''
                       or trim(ifnull(district, '')) = ''
                       or trim(ifnull(detailAddress, '')) = ''
                   )) as abnormalAddressOrderCount,
                (select count(1)
                 from orders
                 where isDelete = 0
                   and (
                       (orderStatus = 1 and deliveryStatus = 0)
                       or trim(ifnull(receiverName, '')) = ''
                       or trim(ifnull(receiverPhone, '')) = ''
                       or trim(ifnull(province, '')) = ''
                       or trim(ifnull(city, '')) = ''
                       or trim(ifnull(district, '')) = ''
                       or trim(ifnull(detailAddress, '')) = ''
                   )) as pendingOrderCount,
                (select count(1)
                 from artisan_profile a
                 where a.isDelete = 0
                   and (
                       exists (
                           select 1
                           from product p
                           where p.artisanId = a.id
                             and p.isDelete = 0
                             and (
                                 p.createTime >= date_sub(now(), interval 7 day)
                                 or p.updateTime >= date_sub(now(), interval 7 day)
                             )
                       )
                       or exists (
                           select 1
                           from order_item oi
                           inner join orders o on o.id = oi.orderId
                           where oi.artisanId = a.id
                             and oi.isDelete = 0
                             and o.isDelete = 0
                             and (
                                 o.createTime >= date_sub(now(), interval 7 day)
                                 or o.updateTime >= date_sub(now(), interval 7 day)
                                 or o.deliveryTime >= date_sub(now(), interval 7 day)
                             )
                       )
                   )) as activeArtisanCount,
                (select count(1)
                 from product
                 where isDelete = 0
                   and auditStatus = 1
                   and status = 1
                   and inventory <= 5) as lowStockRiskCount,
                (select count(1)
                 from order_item
                 where isDelete = 0
                   and refundStatus = 1) as refundAlertCount,
                (select count(1)
                 from product_review
                 where isDelete = 0
                   and score <= 2) as negativeReviewRiskCount,
                (
                    (select count(1)
                     from product
                     where isDelete = 0
                       and auditStatus = 1
                       and status = 1
                       and inventory <= 5)
                    +
                    (select count(1)
                     from order_item
                     where isDelete = 0
                       and refundStatus = 1)
                    +
                    (select count(1)
                     from product_review
                     where isDelete = 0
                       and score <= 2)
                ) as riskAlertCount,
                (select ifnull(sum(payAmount), 0)
                 from orders
                 where isDelete = 0
                   and payStatus = 1
                   and payTime >= date_sub(now(), interval 7 day)) as recentSevenDaysGmv,
                (select count(1)
                 from orders
                 where isDelete = 0
                   and createTime >= date_sub(now(), interval 7 day)) as recentSevenDaysOrderCount,
                (select count(1)
                 from users
                 where isDelete = 0
                   and userRole <> 'admin'
                   and createTime >= date_sub(now(), interval 7 day)) as recentSevenDaysNewUserCount
            """)
    AdminDashboardOverviewVO selectOverview();
}