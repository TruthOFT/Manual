package com.manual.manual.mapper;

import com.manual.manual.model.dto.staff.AdminStaffSaveRequest;
import com.manual.manual.model.vo.staff.AdminStaffVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdminStaffMapper {

    @Select({
            "<script>",
            "select",
            "    u.id as userId,",
            "    sp.id as profileId,",
            "    u.userAccount,",
            "    u.userName,",
            "    u.phone,",
            "    u.email,",
            "    u.gender,",
            "    u.userStatus,",
            "    sp.staffName,",
            "    sp.staffNo,",
            "    sp.positionName,",
            "    sp.salary,",
            "    date_format(sp.entryTime, '%Y-%m-%d %H:%i:%s') as entryTime,",
            "    sp.staffStatus,",
            "    date_format(u.createTime, '%Y-%m-%d %H:%i') as createTime,",
            "    date_format(u.updateTime, '%Y-%m-%d %H:%i') as updateTime",
            "from users u",
            "inner join staff_profile sp on sp.userId = u.id and sp.isDelete = 0",
            "where u.isDelete = 0",
            "  and u.userRole = 'staff'",
            "<if test='userStatus != null'>",
            "  and u.userStatus = #{userStatus}",
            "</if>",
            "<if test='staffStatus != null'>",
            "  and sp.staffStatus = #{staffStatus}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (u.userAccount like concat('%', #{keyword}, '%')",
            "       or u.userName like concat('%', #{keyword}, '%')",
            "       or u.phone like concat('%', #{keyword}, '%')",
            "       or sp.staffName like concat('%', #{keyword}, '%')",
            "       or sp.staffNo like concat('%', #{keyword}, '%')",
            "       or sp.positionName like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by u.updateTime desc, u.id desc",
            "</script>"
    })
    List<AdminStaffVO> selectStaff(@Param("keyword") String keyword,
                                   @Param("userStatus") Integer userStatus,
                                   @Param("staffStatus") Integer staffStatus);

    @Select("""
            select
                u.id as userId,
                sp.id as profileId,
                u.userAccount,
                u.userName,
                u.phone,
                u.email,
                u.gender,
                u.userStatus,
                sp.staffName,
                sp.staffNo,
                sp.positionName,
                sp.salary,
                date_format(sp.entryTime, '%Y-%m-%d %H:%i:%s') as entryTime,
                sp.staffStatus,
                date_format(u.createTime, '%Y-%m-%d %H:%i') as createTime,
                date_format(u.updateTime, '%Y-%m-%d %H:%i') as updateTime
            from users u
            inner join staff_profile sp on sp.userId = u.id and sp.isDelete = 0
            where u.id = #{userId}
              and u.isDelete = 0
              and u.userRole = 'staff'
            limit 1
            """)
    AdminStaffVO selectStaffDetail(@Param("userId") Long userId);

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

    @Select("""
            select count(1)
            from staff_profile
            where staffNo = #{staffNo}
              and isDelete = 0
              and (#{excludeUserId} is null or userId != #{excludeUserId})
            """)
    int countStaffNo(@Param("staffNo") String staffNo, @Param("excludeUserId") Long excludeUserId);

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
                'staff',
                #{request.userStatus},
                0.00,
                now(),
                now(),
                0
            )
            """)
    int insertStaffUser(@Param("userId") Long userId,
                        @Param("userPassword") String userPassword,
                        @Param("request") AdminStaffSaveRequest request);

    @Insert("""
            insert into staff_profile (
                id,
                userId,
                staffName,
                staffNo,
                positionName,
                salary,
                entryTime,
                staffStatus,
                createTime,
                updateTime,
                isDelete
            ) values (
                #{profileId},
                #{userId},
                #{request.staffName},
                #{request.staffNo},
                #{request.positionName},
                #{request.salary},
                #{request.entryTime},
                #{request.staffStatus},
                now(),
                now(),
                0
            )
            """)
    int insertStaffProfile(@Param("profileId") Long profileId,
                           @Param("userId") Long userId,
                           @Param("request") AdminStaffSaveRequest request);

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
            "  and userRole = 'staff'",
            "</script>"
    })
    int updateStaffUser(@Param("userId") Long userId,
                        @Param("userPassword") String userPassword,
                        @Param("request") AdminStaffSaveRequest request);

    @Update("""
            update staff_profile
            set staffName = #{request.staffName},
                staffNo = #{request.staffNo},
                positionName = #{request.positionName},
                salary = #{request.salary},
                entryTime = #{request.entryTime},
                staffStatus = #{request.staffStatus},
                updateTime = now()
            where userId = #{userId}
              and isDelete = 0
            """)
    int updateStaffProfile(@Param("userId") Long userId,
                           @Param("request") AdminStaffSaveRequest request);

    @Update("""
            update users
            set isDelete = 1,
                updateTime = now()
            where id = #{userId}
              and userRole = 'staff'
              and isDelete = 0
            """)
    int deleteStaffUser(@Param("userId") Long userId);

    @Update("""
            update staff_profile
            set isDelete = 1,
                updateTime = now()
            where userId = #{userId}
              and isDelete = 0
            """)
    int deleteStaffProfile(@Param("userId") Long userId);
}
