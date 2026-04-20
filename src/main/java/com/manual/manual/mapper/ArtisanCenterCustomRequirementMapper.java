package com.manual.manual.mapper;

import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementListItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArtisanCenterCustomRequirementMapper {

    @Select({
            "<script>",
            "select",
            "    cr.id,",
            "    cr.orderId,",
            "    cr.orderItemId,",
            "    cr.productId,",
            "    cr.userId,",
            "    oi.orderNo,",
            "    oi.productName,",
            "    u.userName as buyerName,",
            "    cr.requirementTitle,",
            "    cr.requirementContent,",
            "    cr.confirmStatus,",
            "    cr.confirmRemark,",
            "    date_format(cr.confirmTime, '%Y-%m-%d %H:%i') as confirmTime,",
            "    date_format(cr.createTime, '%Y-%m-%d %H:%i') as createTime",
            "from custom_requirement cr",
            "inner join product p on p.id = cr.productId",
            "inner join order_item oi on oi.id = cr.orderItemId",
            "inner join users u on u.id = cr.userId",
            "where p.artisanId = #{artisanId}",
            "  and cr.isDelete = 0",
            "  and p.isDelete = 0",
            "<if test='confirmStatus != null'>",
            "  and cr.confirmStatus = #{confirmStatus}",
            "</if>",
            "<if test='keyword != null and keyword != \"\"'>",
            "  and (cr.requirementTitle like concat('%', #{keyword}, '%')",
            "       or oi.orderNo like concat('%', #{keyword}, '%')",
            "       or oi.productName like concat('%', #{keyword}, '%'))",
            "</if>",
            "order by cr.createTime desc, cr.id desc",
            "</script>"
    })
    List<ArtisanCenterCustomRequirementListItemVO> selectCustomRequirements(@Param("artisanId") Long artisanId,
                                                                            @Param("confirmStatus") Integer confirmStatus,
                                                                            @Param("keyword") String keyword);

    @Select("""
            select
                cr.id,
                cr.orderId,
                cr.orderItemId,
                cr.productId,
                cr.userId,
                oi.orderNo,
                oi.productName,
                u.userName as buyerName,
                cr.requirementTitle,
                cr.requirementContent,
                cr.referenceImages,
                cr.confirmStatus,
                cr.confirmRemark,
                date_format(cr.confirmTime, '%Y-%m-%d %H:%i') as confirmTime,
                date_format(cr.createTime, '%Y-%m-%d %H:%i') as createTime
            from custom_requirement cr
            inner join product p on p.id = cr.productId
            inner join order_item oi on oi.id = cr.orderItemId
            inner join users u on u.id = cr.userId
            where cr.id = #{id}
              and p.artisanId = #{artisanId}
              and cr.isDelete = 0
              and p.isDelete = 0
            limit 1
            """)
    ArtisanCenterCustomRequirementDetailVO selectCustomRequirementDetail(@Param("artisanId") Long artisanId,
                                                                         @Param("id") Long id);

    @Update("""
            update custom_requirement
            set confirmStatus = #{confirmStatus},
                confirmRemark = #{confirmRemark},
                confirmTime = now(),
                updateTime = now()
            where id = #{id}
              and isDelete = 0
            """)
    int updateCustomRequirementStatus(@Param("id") Long id,
                                      @Param("confirmStatus") Integer confirmStatus,
                                      @Param("confirmRemark") String confirmRemark);
}
