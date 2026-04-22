package com.manual.manual.mapper;

import com.manual.manual.model.vo.order.AdminOrderListItemVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import com.manual.manual.model.vo.order.OrderItemVO;
import com.manual.manual.model.vo.order.OrderSkuSnapshotVO;
import com.manual.manual.model.vo.order.UserAddressVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("""
            select
                ps.id as skuId,
                ps.productId,
                p.artisanId,
                p.productName,
                p.productCover,
                ps.skuName,
                ps.specText,
                ps.price,
                ps.stock,
                ps.lockedStock,
                ps.status as skuStatus,
                p.status as productStatus,
                p.auditStatus as productAuditStatus,
                a.auditStatus as artisanAuditStatus,
                a.shelfStatus as artisanShelfStatus
            from product_sku ps
            inner join product p on p.id = ps.productId
            inner join artisan_profile a on a.id = p.artisanId
            where ps.id = #{skuId}
              and ps.isDelete = 0
              and p.isDelete = 0
              and a.isDelete = 0
            limit 1
            """)
    OrderSkuSnapshotVO selectSkuSnapshot(@Param("skuId") Long skuId);

    @Select("""
            select
                id,
                receiverName,
                receiverPhone,
                province,
                city,
                district,
                detailAddress,
                postalCode,
                tagName,
                isDefault
            from user_address
            where userId = #{userId}
              and isDelete = 0
            order by isDefault desc, updateTime desc, id desc
            """)
    List<UserAddressVO> selectUserAddresses(@Param("userId") Long userId);

    @Select("""
            select
                id,
                receiverName,
                receiverPhone,
                province,
                city,
                district,
                detailAddress,
                postalCode,
                tagName,
                isDefault
            from user_address
            where id = #{addressId}
              and userId = #{userId}
              and isDelete = 0
            limit 1
            """)
    UserAddressVO selectUserAddress(@Param("userId") Long userId, @Param("addressId") Long addressId);

    @Update("""
            update user_address
            set isDefault = 0,
                updateTime = now()
            where userId = #{userId}
              and isDelete = 0
            """)
    int clearDefaultAddress(@Param("userId") Long userId);

    @Insert("""
            insert into user_address (
                id,
                userId,
                receiverName,
                receiverPhone,
                province,
                city,
                district,
                detailAddress,
                postalCode,
                tagName,
                isDefault,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{userId},
                #{receiverName},
                #{receiverPhone},
                #{province},
                #{city},
                #{district},
                #{detailAddress},
                #{postalCode},
                #{tagName},
                #{isDefault},
                now(),
                now(),
                0
            )
            """)
    int insertUserAddress(@Param("id") Long id,
                          @Param("userId") Long userId,
                          @Param("receiverName") String receiverName,
                          @Param("receiverPhone") String receiverPhone,
                          @Param("province") String province,
                          @Param("city") String city,
                          @Param("district") String district,
                          @Param("detailAddress") String detailAddress,
                          @Param("postalCode") String postalCode,
                          @Param("tagName") String tagName,
                          @Param("isDefault") Integer isDefault);

    @Update("""
            update user_address
            set receiverName = #{receiverName},
                receiverPhone = #{receiverPhone},
                province = #{province},
                city = #{city},
                district = #{district},
                detailAddress = #{detailAddress},
                postalCode = #{postalCode},
                tagName = #{tagName},
                isDefault = #{isDefault},
                updateTime = now()
            where id = #{id}
              and userId = #{userId}
              and isDelete = 0
            """)
    int updateUserAddress(@Param("id") Long id,
                          @Param("userId") Long userId,
                          @Param("receiverName") String receiverName,
                          @Param("receiverPhone") String receiverPhone,
                          @Param("province") String province,
                          @Param("city") String city,
                          @Param("district") String district,
                          @Param("detailAddress") String detailAddress,
                          @Param("postalCode") String postalCode,
                          @Param("tagName") String tagName,
                          @Param("isDefault") Integer isDefault);

    @Update("""
            update user_address
            set isDelete = 1,
                isDefault = 0,
                updateTime = now()
            where id = #{id}
              and userId = #{userId}
              and isDelete = 0
            """)
    int deleteUserAddress(@Param("userId") Long userId, @Param("id") Long id);

    @Update("""
            update product_sku
            set lockedStock = lockedStock + #{quantity},
                updateTime = now()
            where id = #{skuId}
              and isDelete = 0
              and status = 1
              and stock - lockedStock >= #{quantity}
            """)
    int lockSkuStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Update("""
            update product_sku
            set lockedStock = case
                    when lockedStock >= #{quantity} then lockedStock - #{quantity}
                    else 0
                end,
                updateTime = now()
            where id = #{skuId}
              and isDelete = 0
            """)
    int releaseSkuLockedStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Insert("""
            insert into orders (
                id,
                orderNo,
                userId,
                addressId,
                orderStatus,
                payStatus,
                payType,
                deliveryStatus,
                productAmount,
                discountAmount,
                freightAmount,
                payAmount,
                buyerRemark,
                receiverName,
                receiverPhone,
                province,
                city,
                district,
                detailAddress,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{orderId},
                #{orderNo},
                #{userId},
                #{addressId},
                0,
                0,
                null,
                0,
                #{productAmount},
                #{discountAmount},
                #{freightAmount},
                #{payAmount},
                #{buyerRemark},
                #{address.receiverName},
                #{address.receiverPhone},
                #{address.province},
                #{address.city},
                #{address.district},
                #{address.detailAddress},
                now(),
                now(),
                0
            )
            """)
    int insertOrder(@Param("orderId") Long orderId,
                    @Param("orderNo") String orderNo,
                    @Param("userId") Long userId,
                    @Param("addressId") Long addressId,
                    @Param("productAmount") BigDecimal productAmount,
                    @Param("discountAmount") BigDecimal discountAmount,
                    @Param("freightAmount") BigDecimal freightAmount,
                    @Param("payAmount") BigDecimal payAmount,
                    @Param("buyerRemark") String buyerRemark,
                    @Param("address") UserAddressVO address);

    @Insert("""
            insert into order_item (
                id,
                orderId,
                orderNo,
                userId,
                productId,
                skuId,
                artisanId,
                productName,
                skuName,
                productCover,
                specText,
                quantity,
                salePrice,
                totalAmount,
                refundStatus,
                reviewStatus,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{id},
                #{orderId},
                #{orderNo},
                #{userId},
                #{sku.productId},
                #{sku.skuId},
                #{sku.artisanId},
                #{sku.productName},
                #{sku.skuName},
                #{sku.productCover},
                #{sku.specText},
                #{quantity},
                #{salePrice},
                #{totalAmount},
                0,
                0,
                now(),
                now(),
                0
            )
            """)
    int insertOrderItem(@Param("id") Long id,
                        @Param("orderId") Long orderId,
                        @Param("orderNo") String orderNo,
                        @Param("userId") Long userId,
                        @Param("sku") OrderSkuSnapshotVO sku,
                        @Param("quantity") Integer quantity,
                        @Param("salePrice") BigDecimal salePrice,
                        @Param("totalAmount") BigDecimal totalAmount);

    @Select("""
            select
                o.id as orderId,
                o.orderNo,
                o.userId,
                u.userName as buyerName,
                o.addressId,
                o.orderStatus,
                o.payStatus,
                o.payType,
                o.deliveryStatus,
                o.productAmount,
                o.discountAmount,
                o.freightAmount,
                o.payAmount,
                o.buyerRemark,
                o.receiverName,
                o.receiverPhone,
                o.province,
                o.city,
                o.district,
                o.detailAddress,
                date_format(o.payTime, '%Y-%m-%d %H:%i') as payTime,
                date_format(o.deliveryTime, '%Y-%m-%d %H:%i') as deliveryTime,
                date_format(o.receiveTime, '%Y-%m-%d %H:%i') as receiveTime,
                date_format(o.finishTime, '%Y-%m-%d %H:%i') as finishTime,
                date_format(o.cancelTime, '%Y-%m-%d %H:%i') as cancelTime,
                date_format(o.createTime, '%Y-%m-%d %H:%i') as createTime,
                date_format(date_add(o.createTime, interval #{timeoutMinutes} minute), '%Y-%m-%d %H:%i:%s') as expireTime
            from orders o
            inner join users u on u.id = o.userId
            where o.id = #{orderId}
              and o.userId = #{userId}
              and o.isDelete = 0
            limit 1
            """)
    OrderDetailVO selectUserOrderDetail(@Param("userId") Long userId,
                                         @Param("orderId") Long orderId,
                                         @Param("timeoutMinutes") Integer timeoutMinutes);

    @Select({
            "<script>",
            "select",
            "    o.id as orderId,",
            "    o.orderNo,",
            "    o.userId,",
            "    u.userName as buyerName,",
            "    o.addressId,",
            "    o.orderStatus,",
            "    o.payStatus,",
            "    o.payType,",
            "    o.deliveryStatus,",
            "    o.productAmount,",
            "    o.discountAmount,",
            "    o.freightAmount,",
            "    o.payAmount,",
            "    o.buyerRemark,",
            "    o.receiverName,",
            "    o.receiverPhone,",
            "    o.province,",
            "    o.city,",
            "    o.district,",
            "    o.detailAddress,",
            "    date_format(o.payTime, '%Y-%m-%d %H:%i') as payTime,",
            "    date_format(o.deliveryTime, '%Y-%m-%d %H:%i') as deliveryTime,",
            "    date_format(o.receiveTime, '%Y-%m-%d %H:%i') as receiveTime,",
            "    date_format(o.finishTime, '%Y-%m-%d %H:%i') as finishTime,",
            "    date_format(o.cancelTime, '%Y-%m-%d %H:%i') as cancelTime,",
            "    date_format(o.createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(date_add(o.createTime, interval #{timeoutMinutes} minute), '%Y-%m-%d %H:%i:%s') as expireTime",
            "from orders o",
            "inner join users u on u.id = o.userId",
            "where o.userId = #{userId}",
            "  and o.isDelete = 0",
            "<if test='orderStatus != null'>",
            "  and o.orderStatus = #{orderStatus}",
            "</if>",
            "order by o.createTime desc, o.id desc",
            "</script>"
    })
    List<OrderDetailVO> selectUserOrders(@Param("userId") Long userId,
                                         @Param("orderStatus") Integer orderStatus,
                                         @Param("timeoutMinutes") Integer timeoutMinutes);

    @Select("""
            select
                id,
                productId,
                skuId,
                productName,
                skuName,
                productCover,
                specText,
                quantity,
                salePrice,
                totalAmount
            from order_item
            where orderId = #{orderId}
              and isDelete = 0
            order by id asc
            limit 1
            """)
    OrderItemVO selectOrderItem(@Param("orderId") Long orderId);

    @Update("""
            update users
            set balance = balance - #{amount},
                updateTime = now()
            where id = #{userId}
              and isDelete = 0
              and balance >= #{amount}
            """)
    int deductUserBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    @Update("""
            update orders
            set orderStatus = 1,
                payStatus = 1,
                payType = 'balance',
                payTime = now(),
                updateTime = now()
            where id = #{orderId}
              and userId = #{userId}
              and orderStatus = 0
              and payStatus = 0
              and isDelete = 0
            """)
    int markOrderPaid(@Param("userId") Long userId, @Param("orderId") Long orderId);

    @Update("""
            update product_sku
            set stock = stock - #{quantity},
                lockedStock = lockedStock - #{quantity},
                updateTime = now()
            where id = #{skuId}
              and isDelete = 0
              and stock >= #{quantity}
              and lockedStock >= #{quantity}
            """)
    int finishSkuStockAfterPay(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);

    @Update("""
            update product
            set inventory = (
                    select ifnull(sum(stock), 0)
                    from product_sku
                    where productId = #{productId}
                      and isDelete = 0
                ),
                soldQuantity = soldQuantity + #{quantity},
                updateTime = now()
            where id = #{productId}
              and isDelete = 0
            """)
    int refreshProductStockAfterPay(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Update("""
            update orders
            set orderStatus = 4,
                cancelTime = now(),
                updateTime = now()
            where id = #{orderId}
              and userId = #{userId}
              and orderStatus = 0
              and payStatus = 0
              and isDelete = 0
            """)
    int cancelUserOrder(@Param("userId") Long userId, @Param("orderId") Long orderId);

    @Update("""
            update orders
            set orderStatus = 4,
                cancelTime = now(),
                updateTime = now()
            where id = #{orderId}
              and orderStatus = 0
              and payStatus = 0
              and isDelete = 0
            """)
    int cancelTimeoutOrder(@Param("orderId") Long orderId);

    @Select("""
            select
                o.id as orderId,
                o.orderNo,
                o.userId,
                u.userName as buyerName,
                o.addressId,
                o.orderStatus,
                o.payStatus,
                o.payType,
                o.deliveryStatus,
                o.productAmount,
                o.discountAmount,
                o.freightAmount,
                o.payAmount,
                o.buyerRemark,
                o.receiverName,
                o.receiverPhone,
                o.province,
                o.city,
                o.district,
                o.detailAddress,
                date_format(o.payTime, '%Y-%m-%d %H:%i') as payTime,
                date_format(o.deliveryTime, '%Y-%m-%d %H:%i') as deliveryTime,
                date_format(o.receiveTime, '%Y-%m-%d %H:%i') as receiveTime,
                date_format(o.finishTime, '%Y-%m-%d %H:%i') as finishTime,
                date_format(o.cancelTime, '%Y-%m-%d %H:%i') as cancelTime,
                date_format(o.createTime, '%Y-%m-%d %H:%i') as createTime,
                date_format(date_add(o.createTime, interval #{timeoutMinutes} minute), '%Y-%m-%d %H:%i:%s') as expireTime
            from orders o
            inner join users u on u.id = o.userId
            where o.id = #{orderId}
              and o.isDelete = 0
            limit 1
            """)
    OrderDetailVO selectAdminOrderDetail(@Param("orderId") Long orderId,
                                          @Param("timeoutMinutes") Integer timeoutMinutes);

    @Select({
            "<script>",
            "select",
            "    o.id as orderId,",
            "    o.orderNo,",
            "    u.userName as buyerName,",
            "    u.userAccount,",
            "    oi.productName,",
            "    oi.skuName,",
            "    oi.quantity,",
            "    o.payAmount,",
            "    o.orderStatus,",
            "    o.payStatus,",
            "    o.payType,",
            "    date_format(o.createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(o.payTime, '%Y-%m-%d %H:%i') as payTime",
            "from orders o",
            "inner join users u on u.id = o.userId",
            "left join order_item oi on oi.orderId = o.id and oi.isDelete = 0",
            "where o.isDelete = 0",
            "<if test='orderStatus != null'>",
            "  and o.orderStatus = #{orderStatus}",
            "</if>",
            "<if test='payStatus != null'>",
            "  and o.payStatus = #{payStatus}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (o.orderNo like concat('%', #{keyword}, '%')",
            "       or u.userName like concat('%', #{keyword}, '%')",
            "       or u.userAccount like concat('%', #{keyword}, '%')",
            "       or oi.productName like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by o.createTime desc, o.id desc",
            "</script>"
    })
    List<AdminOrderListItemVO> selectAdminOrders(@Param("keyword") String keyword,
                                                 @Param("orderStatus") Integer orderStatus,
                                                 @Param("payStatus") Integer payStatus);
}
