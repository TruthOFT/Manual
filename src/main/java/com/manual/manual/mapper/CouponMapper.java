package com.manual.manual.mapper;

import com.manual.manual.model.vo.coupon.UserCouponVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CouponMapper {

    @Select({
            "select",
            "    id,",
            "    couponName,",
            "    couponType,",
            "    thresholdAmount,",
            "    discountAmount,",
            "    discountRate,",
            "    totalCount,",
            "    receiveCount,",
            "    greatest(totalCount - usedCount, 0) as remainingCount,",
            "    date_format(startTime, '%Y-%m-%d %H:%i:%s') as startTime,",
            "    date_format(endTime, '%Y-%m-%d %H:%i:%s') as endTime",
            "from coupon c",
            "where c.isDelete = 0",
            "  and c.couponStatus = 1",
            "  and c.startTime <= now()",
            "  and c.endTime >= now()",
            "  and c.usedCount < c.totalCount",
            "  and not exists (",
            "      select 1",
            "      from coupon_receive cr",
            "      where cr.couponId = c.id",
            "        and cr.userId = #{userId}",
            "        and cr.isDelete = 0",
            "  )",
            "order by c.endTime asc, c.updateTime desc, c.id desc"
    })
    List<UserCouponVO> selectAvailableCoupons(@Param("userId") Long userId);

    @Select({
            "select",
            "    id,",
            "    couponName,",
            "    couponType,",
            "    thresholdAmount,",
            "    discountAmount,",
            "    discountRate,",
            "    totalCount,",
            "    receiveCount,",
            "    greatest(totalCount - usedCount, 0) as remainingCount,",
            "    date_format(startTime, '%Y-%m-%d %H:%i:%s') as startTime,",
            "    date_format(endTime, '%Y-%m-%d %H:%i:%s') as endTime",
            "from coupon",
            "where id = #{couponId}",
            "  and isDelete = 0",
            "limit 1"
    })
    UserCouponVO selectCoupon(@Param("couponId") Long couponId);

    @Select({
            "select count(1)",
            "from coupon_receive",
            "where couponId = #{couponId}",
            "  and userId = #{userId}",
            "  and isDelete = 0"
    })
    int countUserReceive(@Param("couponId") Long couponId, @Param("userId") Long userId);

    @Update({
            "update coupon",
            "set receiveCount = receiveCount + 1,",
            "    updateTime = now()",
            "where id = #{couponId}",
            "  and isDelete = 0",
            "  and couponStatus = 1",
            "  and startTime <= now()",
            "  and endTime >= now()"
    })
    int increaseReceiveCount(@Param("couponId") Long couponId);

    @Insert({
            "insert into coupon_receive (",
            "    id,",
            "    couponId,",
            "    userId,",
            "    receiveTime,",
            "    useStatus,",
            "    createTime,",
            "    updateTime,",
            "    isDelete",
            ") values (",
            "    #{receiveId},",
            "    #{couponId},",
            "    #{userId},",
            "    now(),",
            "    0,",
            "    now(),",
            "    now(),",
            "    0",
            ")"
    })
    int insertCouponReceive(@Param("receiveId") Long receiveId,
                            @Param("couponId") Long couponId,
                            @Param("userId") Long userId);

    @Select({
            "<script>",
            "select",
            "    c.id,",
            "    cr.id as receiveId,",
            "    c.couponName,",
            "    c.couponType,",
            "    c.thresholdAmount,",
            "    c.discountAmount,",
            "    c.discountRate,",
            "    c.totalCount,",
            "    c.receiveCount,",
            "    greatest(c.totalCount - c.usedCount, 0) as remainingCount,",
            "    date_format(c.startTime, '%Y-%m-%d %H:%i:%s') as startTime,",
            "    date_format(c.endTime, '%Y-%m-%d %H:%i:%s') as endTime,",
            "    date_format(cr.receiveTime, '%Y-%m-%d %H:%i') as receiveTime,",
            "    cr.useStatus,",
            "    date_format(cr.useTime, '%Y-%m-%d %H:%i') as useTime,",
            "    cr.orderId",
            "from coupon_receive cr",
            "inner join coupon c on c.id = cr.couponId and c.isDelete = 0",
            "where cr.userId = #{userId}",
            "  and cr.isDelete = 0",
            "<if test='useStatus != null'>",
            "  and cr.useStatus = #{useStatus}",
            "</if>",
            "order by cr.receiveTime desc, cr.id desc",
            "</script>"
    })
    List<UserCouponVO> selectMyCoupons(@Param("userId") Long userId, @Param("useStatus") Integer useStatus);
}
