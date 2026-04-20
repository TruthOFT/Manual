package com.manual.manual.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manual.manual.model.entity.Category;
import com.manual.manual.model.vo.category.AdminCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("""
            select
                c.id,
                c.parentId,
                p.categoryName as parentName,
                c.categoryName,
                c.categoryIcon,
                c.categoryDesc,
                c.categoryLevel,
                c.sortOrder,
                c.isEnable,
                c.createTime,
                c.updateTime,
                (
                    select count(1)
                    from product pr
                    where pr.categoryId = c.id
                      and pr.isDelete = 0
                ) as productCount
            from category c
            left join category p on p.id = c.parentId and p.isDelete = 0
            where c.isDelete = 0
            order by c.categoryLevel asc, c.sortOrder asc, c.id asc
            """)
    List<AdminCategoryVO> selectAdminCategories();

    @Select("""
            select count(1)
            from category
            where parentId = #{categoryId}
              and isDelete = 0
            """)
    Long countChildren(@Param("categoryId") Long categoryId);

    @Select("""
            select count(1)
            from product
            where categoryId = #{categoryId}
              and isDelete = 0
            """)
    Long countProducts(@Param("categoryId") Long categoryId);
}
