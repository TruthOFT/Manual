package com.manual.manual.mapper;

import com.manual.manual.model.dto.coupon.AdminCouponSaveRequest;
import com.manual.manual.model.vo.coupon.AdminCouponReceiveVO;
import com.manual.manual.model.vo.coupon.AdminCouponVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminCouponMapper {

    @Select({
            "<script>",
            "select",
            "    id,",
            "    couponName,",
            "    couponType,",
            "    thresholdAmount,",
            "    discountAmount,",
            "    discountRate,",
            "    totalCount,",
            "    receiveCount,",
            "    usedCount,",
            "    date_format(startTime, '%Y-%m-%d %H:%i:%s') as startTime,",
            "    date_format(endTime, '%Y-%m-%d %H:%i:%s') as endTime,",
            "    couponStatus,",
            "    date_format(createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(updateTime, '%Y-%m-%d %H:%i') as updateTime",
            "from coupon",
            "where isDelete = 0",
            "<if test='couponStatus != null'>",
            "  and couponStatus = #{couponStatus}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and couponName like concat('%', #{keyword}, '%')",
            "</if>",
            "order by updateTime desc, id desc",
            "</script>"
    })
    List<AdminCouponVO> selectCoupons(@Param("keyword") String keyword,
                                      @Param("couponStatus") Integer couponStatus);

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
            "    usedCount,",
            "    date_format(startTime, '%Y-%m-%d %H:%i:%s') as startTime,",
            "    date_format(endTime, '%Y-%m-%d %H:%i:%s') as endTime,",
            "    couponStatus,",
            "    date_format(createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(updateTime, '%Y-%m-%d %H:%i') as updateTime",
            "from coupon",
            "where id = #{couponId}",
            "  and isDelete = 0",
            "limit 1"
    })
    AdminCouponVO selectCoupon(@Param("couponId") Long couponId);

    @Select({
            "select count(1)",
            "from users",
            "where id = #{userId}",
            "  and userRole = 'user'",
            "  and isDelete = 0"
    })
    int countCustomer(@Param("userId") Long userId);

    @Select({
            "select count(1)",
            "from coupon_receive",
            "where couponId = #{couponId}",
            "  and userId = #{userId}",
            "  and useStatus = 0",
            "  and isDelete = 0"
    })
    int countUserUnusedReceive(@Param("couponId") Long couponId, @Param("userId") Long userId);

    @Insert({
            "insert into coupon (",
            "    id,",
            "    couponName,",
            "    couponType,",
            "    thresholdAmount,",
            "    discountAmount,",
            "    discountRate,",
            "    totalCount,",
            "    receiveCount,",
            "    usedCount,",
            "    startTime,",
            "    endTime,",
            "    couponStatus,",
            "    createTime,",
            "    updateTime,",
            "    isDelete",
            ") values (",
            "    #{couponId},",
            "    #{request.couponName},",
            "    #{request.couponType},",
            "    #{request.thresholdAmount},",
            "    #{request.discountAmount},",
            "    #{request.discountRate},",
            "    #{request.totalCount},",
            "    0,",
            "    0,",
            "    #{request.startTime},",
            "    #{request.endTime},",
            "    #{request.couponStatus},",
            "    now(),",
            "    now(),",
            "    0",
            ")"
    })
    int insertCoupon(@Param("couponId") Long couponId,
                     @Param("request") AdminCouponSaveRequest request);

    @Update({
            "update coupon",
            "set couponName = #{request.couponName},",
            "    couponType = #{request.couponType},",
            "    thresholdAmount = #{request.thresholdAmount},",
            "    discountAmount = #{request.discountAmount},",
            "    discountRate = #{request.discountRate},",
            "    totalCount = #{request.totalCount},",
            "    startTime = #{request.startTime},",
            "    endTime = #{request.endTime},",
            "    couponStatus = #{request.couponStatus},",
            "    updateTime = now()",
            "where id = #{couponId}",
            "  and isDelete = 0"
    })
    int updateCoupon(@Param("couponId") Long couponId,
                     @Param("request") AdminCouponSaveRequest request);

    @Update({
            "update coupon",
            "set isDelete = 1,",
            "    updateTime = now()",
            "where id = #{couponId}",
            "  and isDelete = 0"
    })
    int deleteCoupon(@Param("couponId") Long couponId);

    @Update({
            "update coupon",
            "set couponStatus = #{couponStatus},",
            "    updateTime = now()",
            "where id = #{couponId}",
            "  and isDelete = 0"
    })
    int updateCouponStatus(@Param("couponId") Long couponId, @Param("couponStatus") Integer couponStatus);

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

    @Update({
            "update coupon",
            "set receiveCount = receiveCount + 1,",
            "    updateTime = now()",
            "where id = #{couponId}",
            "  and isDelete = 0",
            "  and couponStatus = 1"
    })
    int increaseReceiveCount(@Param("couponId") Long couponId);

    @Select({
            "select",
            "    cr.id,",
            "    cr.couponId,",
            "    cr.userId,",
            "    u.userAccount,",
            "    u.userName,",
            "    u.phone,",
            "    date_format(cr.receiveTime, '%Y-%m-%d %H:%i') as receiveTime,",
            "    cr.useStatus,",
            "    date_format(cr.useTime, '%Y-%m-%d %H:%i') as useTime,",
            "    cr.orderId",
            "from coupon_receive cr",
            "inner join users u on u.id = cr.userId",
            "where cr.couponId = #{couponId}",
            "  and cr.isDelete = 0",
            "order by cr.receiveTime desc, cr.id desc"
    })
    List<AdminCouponReceiveVO> selectReceives(@Param("couponId") Long couponId);
}
