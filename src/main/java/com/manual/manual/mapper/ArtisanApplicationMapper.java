package com.manual.manual.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manual.manual.model.entity.ArtisanProfile;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationDetailVO;
import com.manual.manual.model.vo.artisanapplication.AdminArtisanApplicationListItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArtisanApplicationMapper extends BaseMapper<ArtisanProfile> {

    @Select({
            "<script>",
            "select",
            "    ap.id,",
            "    ap.userId,",
            "    u.userAccount,",
            "    u.userName,",
            "    ap.artisanName,",
            "    ap.shopName,",
            "    ap.contactPhone,",
            "    ap.depositAmount,",
            "    ap.auditStatus,",
            "    date_format(ap.applyTime, '%Y-%m-%d %H:%i:%s') as applyTime,",
            "    date_format(ap.auditTime, '%Y-%m-%d %H:%i:%s') as auditTime",
            "from artisan_profile ap",
            "inner join users u on u.id = ap.userId and u.isDelete = 0",
            "where ap.isDelete = 0",
            "<if test='auditStatus != null'>",
            "  and ap.auditStatus = #{auditStatus}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (u.userAccount like concat('%', #{keyword}, '%')",
            "       or u.userName like concat('%', #{keyword}, '%')",
            "       or ap.artisanName like concat('%', #{keyword}, '%')",
            "       or ap.shopName like concat('%', #{keyword}, '%')",
            "       or ap.contactPhone like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by case when ap.auditStatus = 0 then 0 else 1 end asc, ap.applyTime desc, ap.id desc",
            "</script>"
    })
    List<AdminArtisanApplicationListItemVO> selectAdminApplications(@Param("auditStatus") Integer auditStatus,
                                                                    @Param("keyword") String keyword);

    @Select("""
            select
                ap.id,
                ap.userId,
                u.userAccount,
                u.userName,
                u.avatarUrl as userAvatarUrl,
                u.phone,
                u.email,
                ap.artisanName,
                ap.shopName,
                ap.artisanAvatar,
                ap.coverUrl,
                ap.artisanStory,
                ap.craftCategory,
                ap.originPlace,
                ap.experienceYears,
                ap.supportCustom,
                ap.contactPhone,
                ap.qualificationDesc,
                ap.qualificationImages as qualificationImagesRaw,
                ap.depositAmount,
                ap.auditRemark,
                ap.auditStatus,
                ap.shelfStatus,
                date_format(ap.applyTime, '%Y-%m-%d %H:%i:%s') as applyTime,
                date_format(ap.auditTime, '%Y-%m-%d %H:%i:%s') as auditTime,
                date_format(ap.createTime, '%Y-%m-%d %H:%i:%s') as createTime,
                date_format(ap.updateTime, '%Y-%m-%d %H:%i:%s') as updateTime
            from artisan_profile ap
            inner join users u on u.id = ap.userId and u.isDelete = 0
            where ap.id = #{id}
              and ap.isDelete = 0
            limit 1
            """)
    AdminArtisanApplicationDetailVO selectAdminApplicationDetail(@Param("id") Long id);
}