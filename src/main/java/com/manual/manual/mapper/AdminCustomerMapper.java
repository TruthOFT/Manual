package com.manual.manual.mapper;

import com.manual.manual.model.dto.customer.AdminCustomerSaveRequest;
import com.manual.manual.model.vo.customer.AdminCustomerVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AdminCustomerMapper {

    @Select({
            "<script>",
            "select",
            "    u.id as userId,",
            "    cp.id as profileId,",
            "    u.userAccount,",
            "    u.userName,",
            "    u.phone,",
            "    u.email,",
            "    u.gender,",
            "    u.userStatus,",
            "    ifnull(cp.customerLevel, 1) as customerLevel,",
            "    ifnull(cp.points, 0) as points,",
            "    ifnull(cp.totalAmount, 0.00) as totalAmount,",
            "    ifnull(cp.orderCount, 0) as orderCount,",
            "    cp.preferenceTags,",
            "    date_format(cp.lastPurchaseTime, '%Y-%m-%d %H:%i:%s') as lastPurchaseTime,",
            "    date_format(u.createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(u.updateTime, '%Y-%m-%d %H:%i') as updateTime",
            "from users u",
            "left join customer_profile cp on cp.userId = u.id and cp.isDelete = 0",
            "where u.isDelete = 0",
            "  and u.userRole = 'user'",
            "<if test='userStatus != null'>",
            "  and u.userStatus = #{userStatus}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (u.userAccount like concat('%', #{keyword}, '%')",
            "       or u.userName like concat('%', #{keyword}, '%')",
            "       or u.phone like concat('%', #{keyword}, '%')",
            "       or cp.preferenceTags like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by u.updateTime desc, u.id desc",
            "</script>"
    })
    List<AdminCustomerVO> selectCustomers(@Param("keyword") String keyword,
                                          @Param("userStatus") Integer userStatus);

    @Select("""
            select
                u.id as userId,
                cp.id as profileId,
                u.userAccount,
                u.userName,
                u.phone,
                u.email,
                u.gender,
                u.userStatus,
                ifnull(cp.customerLevel, 1) as customerLevel,
                ifnull(cp.points, 0) as points,
                ifnull(cp.totalAmount, 0.00) as totalAmount,
                ifnull(cp.orderCount, 0) as orderCount,
                cp.preferenceTags,
                date_format(cp.lastPurchaseTime, '%Y-%m-%d %H:%i:%s') as lastPurchaseTime,
                date_format(u.createTime, '%Y-%m-%d %H:%i') as createTime,
                date_format(u.updateTime, '%Y-%m-%d %H:%i') as updateTime
            from users u
            left join customer_profile cp on cp.userId = u.id and cp.isDelete = 0
            where u.id = #{userId}
              and u.isDelete = 0
              and u.userRole = 'user'
            limit 1
            """)
    AdminCustomerVO selectCustomer(@Param("userId") Long userId);

    @Select("""
            select count(1)
            from users
            where userAccount = #{userAccount}
              and isDelete = 0
              and (#{excludeUserId} is null or id != #{excludeUserId})
            """)
    int countAccount(@Param("userAccount") String userAccount, @Param("excludeUserId") Long excludeUserId);

    @Select("""
            select count(1)
            from users
            where phone = #{phone}
              and isDelete = 0
              and (#{excludeUserId} is null or id != #{excludeUserId})
            """)
    int countPhone(@Param("phone") String phone, @Param("excludeUserId") Long excludeUserId);

    @Select("""
            select count(1)
            from users
            where email = #{email}
              and isDelete = 0
              and (#{excludeUserId} is null or id != #{excludeUserId})
            """)
    int countEmail(@Param("email") String email, @Param("excludeUserId") Long excludeUserId);

    @Insert("""
            insert into users (
                id,
                userAccount,
                userPassword,
                userName,
                phone,
                email,
                gender,
                userRole,
                userStatus,
                balance,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{userId},
                #{request.userAccount},
                #{userPassword},
                #{request.userName},
                #{request.phone},
                #{request.email},
                #{request.gender},
                'user',
                #{request.userStatus},
                0.00,
                now(),
                now(),
                0
            )
            """)
    int insertCustomerUser(@Param("userId") Long userId,
                           @Param("userPassword") String userPassword,
                           @Param("request") AdminCustomerSaveRequest request);

    @Insert("""
            insert into customer_profile (
                id,
                userId,
                customerLevel,
                points,
                totalAmount,
                orderCount,
                preferenceTags,
                lastPurchaseTime,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{profileId},
                #{userId},
                #{request.customerLevel},
                #{request.points},
                #{request.totalAmount},
                #{request.orderCount},
                #{request.preferenceTags},
                #{request.lastPurchaseTime},
                now(),
                now(),
                0
            )
            """)
    int insertCustomerProfile(@Param("profileId") Long profileId,
                              @Param("userId") Long userId,
                              @Param("request") AdminCustomerSaveRequest request);

    @Update({
            "<script>",
            "update users",
            "set userName = #{request.userName},",
            "    phone = #{request.phone},",
            "    email = #{request.email},",
            "    gender = #{request.gender},",
            "    userStatus = #{request.userStatus},",
            "    updateTime = now()",
            "<if test='userPassword != null and userPassword != \"\"'>",
            "    , userPassword = #{userPassword}",
            "</if>",
            "where id = #{userId}",
            "  and isDelete = 0",
            "  and userRole = 'user'",
            "</script>"
    })
    int updateCustomerUser(@Param("userId") Long userId,
                           @Param("userPassword") String userPassword,
                           @Param("request") AdminCustomerSaveRequest request);

    @Update("""
            update customer_profile
            set customerLevel = #{request.customerLevel},
                points = #{request.points},
                totalAmount = #{request.totalAmount},
                orderCount = #{request.orderCount},
                preferenceTags = #{request.preferenceTags},
                lastPurchaseTime = #{request.lastPurchaseTime},
                updateTime = now()
            where userId = #{userId}
              and isDelete = 0
            """)
    int updateCustomerProfile(@Param("userId") Long userId,
                              @Param("request") AdminCustomerSaveRequest request);

    @Update("""
            update users
            set isDelete = 1,
                updateTime = now()
            where id = #{userId}
              and userRole = 'user'
              and isDelete = 0
            """)
    int deleteCustomerUser(@Param("userId") Long userId);

    @Update("""
            update customer_profile
            set isDelete = 1,
                updateTime = now()
            where userId = #{userId}
              and isDelete = 0
            """)
    int deleteCustomerProfile(@Param("userId") Long userId);
}
