package com.manual.manual.mapper;

import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderLogisticsVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArtisanCenterOrderMapper {

    @Select({
            "<script>",
            "select",
            "    oi.id,",
            "    oi.orderId,",
            "    oi.productId,",
            "    oi.skuId,",
            "    oi.orderNo,",
            "    oi.productName,",
            "    oi.skuName,",
            "    oi.productCover,",
            "    oi.quantity,",
            "    oi.totalAmount,",
            "    o.orderStatus,",
            "    o.deliveryStatus,",
            "    u.userName as buyerName,",
            "    date_format(oi.createTime, '%Y-%m-%d %H:%i') as createTime",
            "from order_item oi",
            "inner join orders o on o.id = oi.orderId",
            "inner join users u on u.id = oi.userId",
            "where oi.artisanId = #{artisanId}",
            "  and oi.isDelete = 0",
            "  and o.isDelete = 0",
            "<if test='orderStatus != null'>",
            "  and o.orderStatus = #{orderStatus}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (oi.orderNo like concat('%', #{keyword}, '%')",
            "       or oi.productName like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by oi.createTime desc, oi.id desc",
            "</script>"
    })
    List<ArtisanCenterOrderListItemVO> selectOrders(@Param("artisanId") Long artisanId,
                                                    @Param("orderStatus") Integer orderStatus,
                                                    @Param("keyword") String keyword);

    @Select("""
            select
                oi.id,
                oi.orderId,
                oi.productId,
                oi.skuId,
                oi.userId,
                oi.orderNo,
                oi.productName,
                oi.skuName,
                oi.productCover,
                oi.specText,
                oi.quantity,
                oi.salePrice,
                oi.totalAmount,
                o.payAmount,
                o.orderStatus,
                o.payStatus,
                o.deliveryStatus,
                u.userName as buyerName,
                o.buyerRemark,
                o.receiverName,
                o.receiverPhone,
                o.province,
                o.city,
                o.district,
                o.detailAddress,
                date_format(oi.createTime, '%Y-%m-%d %H:%i') as createTime,
                date_format(o.payTime, '%Y-%m-%d %H:%i') as payTime,
                date_format(o.deliveryTime, '%Y-%m-%d %H:%i') as deliveryTime
            from order_item oi
            inner join orders o on o.id = oi.orderId
            inner join users u on u.id = oi.userId
            where oi.id = #{orderItemId}
              and oi.artisanId = #{artisanId}
              and oi.isDelete = 0
              and o.isDelete = 0
            limit 1
            """)
    ArtisanCenterOrderDetailVO selectOrderDetail(@Param("artisanId") Long artisanId,
                                                 @Param("orderItemId") Long orderItemId);

    @Select("""
            select
                id,
                companyName,
                trackingNo,
                senderName,
                senderPhone,
                receiverName,
                receiverPhone,
                logisticsRemark,
                date_format(shipTime, '%Y-%m-%d %H:%i') as shipTime,
                date_format(signTime, '%Y-%m-%d %H:%i') as signTime,
                status
            from order_logistics
            where orderId = #{orderId}
              and isDelete = 0
            order by id desc
            limit 1
            """)
    ArtisanCenterOrderLogisticsVO selectOrderLogistics(@Param("orderId") Long orderId);

    @Select("""
            select id
            from order_logistics
            where orderId = #{orderId}
              and isDelete = 0
            order by id desc
            limit 1
            """)
    Long selectOrderLogisticsId(@Param("orderId") Long orderId);

    @Insert("""
            insert into order_logistics (
                id,
                orderId,
                orderNo,
                companyName,
                trackingNo,
                senderName,
                senderPhone,
                receiverName,
                receiverPhone,
                logisticsRemark,
                shipTime,
                status,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{orderId},
                #{orderNo},
                #{companyName},
                #{trackingNo},
                #{senderName},
                #{senderPhone},
                #{receiverName},
                #{receiverPhone},
                #{logisticsRemark},
                now(),
                1,
                now(),
                now(),
                0
            )
            """)
    int insertOrderLogistics(@Param("id") Long id,
                             @Param("orderId") Long orderId,
                             @Param("orderNo") String orderNo,
                             @Param("companyName") String companyName,
                             @Param("trackingNo") String trackingNo,
                             @Param("senderName") String senderName,
                             @Param("senderPhone") String senderPhone,
                             @Param("receiverName") String receiverName,
                             @Param("receiverPhone") String receiverPhone,
                             @Param("logisticsRemark") String logisticsRemark);

    @Update("""
            update order_logistics
            set companyName = #{companyName},
                trackingNo = #{trackingNo},
                senderName = #{senderName},
                senderPhone = #{senderPhone},
                receiverName = #{receiverName},
                receiverPhone = #{receiverPhone},
                logisticsRemark = #{logisticsRemark},
                shipTime = now(),
                status = 1,
                updateTime = now()
            where id = #{id}
              and isDelete = 0
            """)
    int updateOrderLogistics(@Param("id") Long id,
                             @Param("companyName") String companyName,
                             @Param("trackingNo") String trackingNo,
                             @Param("senderName") String senderName,
                             @Param("senderPhone") String senderPhone,
                             @Param("receiverName") String receiverName,
                             @Param("receiverPhone") String receiverPhone,
                             @Param("logisticsRemark") String logisticsRemark);

    @Update("""
            update orders
            set orderStatus = 2,
                deliveryStatus = 1,
                deliveryTime = now(),
                updateTime = now()
            where id = #{orderId}
              and isDelete = 0
            """)
    int shipOrder(@Param("orderId") Long orderId);
}
