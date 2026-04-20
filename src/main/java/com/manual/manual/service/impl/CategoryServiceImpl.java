package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.constant.UserConstant;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.CategoryMapper;
import com.manual.manual.model.dto.category.AdminCategorySaveRequest;
import com.manual.manual.model.entity.Category;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.category.AdminCategoryVO;
import com.manual.manual.service.CategoryService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private UserService userService;

    @Override
    public List<AdminCategoryVO> listAdminCategories(HttpServletRequest request) {
        requireAdmin(request);
        return categoryMapper.selectAdminCategories();
    }

    @Override
    public AdminCategoryVO getAdminCategory(Long id, HttpServletRequest request) {
        requireAdmin(request);
        getExistingCategory(id);
        return categoryMapper.selectAdminCategories().stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Category not found"));
    }

    @Override
    public long createAdminCategory(AdminCategorySaveRequest requestBody, HttpServletRequest request) {
        requireAdmin(request);
        if (requestBody == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Category category = new Category();
        applyCategoryChanges(category, requestBody, null);
        boolean saved = this.save(category);
        if (!saved || category.getId() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Create category failed");
        }
        return category.getId();
    }

    @Override
    public boolean updateAdminCategory(Long id, AdminCategorySaveRequest requestBody, HttpServletRequest request) {
        requireAdmin(request);
        if (requestBody == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Category category = getExistingCategory(id);
        applyCategoryChanges(category, requestBody, id);
        return this.updateById(category);
    }

    @Override
    public boolean deleteAdminCategory(Long id, HttpServletRequest request) {
        requireAdmin(request);
        Category category = getExistingCategory(id);
        Long childCount = categoryMapper.countChildren(id);
        if (childCount != null && childCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Please delete child categories first");
        }
        Long productCount = categoryMapper.countProducts(id);
        if (productCount != null && productCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Category already contains products and cannot be deleted");
        }
        return this.removeById(category.getId());
    }

    private void applyCategoryChanges(Category category, AdminCategorySaveRequest requestBody, Long currentId) {
        String categoryName = trim(requestBody.getCategoryName());
        if (!hasText(categoryName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Category name is required");
        }
        if (categoryName.length() > 64) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Category name is too long");
        }
        Integer categoryLevel = requestBody.getCategoryLevel() == null ? 1 : requestBody.getCategoryLevel();
        if (categoryLevel < 1 || categoryLevel > 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Only level 1 and level 2 categories are supported");
        }
        if (currentId != null && categoryLevel == 2) {
            Long childCount = categoryMapper.countChildren(currentId);
            if (childCount != null && childCount > 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "Category with child categories cannot be changed to level 2");
            }
        }
        Long parentId = requestBody.getParentId();
        if (categoryLevel == 1) {
            parentId = null;
        } else {
            if (parentId == null || parentId <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Level 2 category must select a parent category");
            }
            if (currentId != null && currentId.equals(parentId)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parent category cannot be itself");
            }
            Category parentCategory = getExistingCategory(parentId);
            if (parentCategory.getCategoryLevel() == null || parentCategory.getCategoryLevel() != 1) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Parent category must be a level 1 category");
            }
        }
        validateCategoryNameUnique(currentId, parentId, categoryName);
        category.setParentId(parentId);
        category.setCategoryName(categoryName);
        category.setCategoryIcon(trim(requestBody.getCategoryIcon()));
        category.setCategoryDesc(trim(requestBody.getCategoryDesc()));
        category.setCategoryLevel(categoryLevel);
        category.setSortOrder(requestBody.getSortOrder() == null ? 0 : requestBody.getSortOrder());
        category.setIsEnable(requestBody.getIsEnable() == null ? 1 : requestBody.getIsEnable());
    }

    private void validateCategoryNameUnique(Long currentId, Long parentId, String categoryName) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("categoryName", categoryName);
        if (parentId == null) {
            queryWrapper.isNull("parentId");
        } else {
            queryWrapper.eq("parentId", parentId);
        }
        if (currentId != null) {
            queryWrapper.ne("id", currentId);
        }
        Long count = categoryMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Category name already exists under the same parent");
        }
    }

    private Category getExistingCategory(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Category id is invalid");
        }
        Category category = this.getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Category not found");
        }
        return category;
    }

    private User requireAdmin(HttpServletRequest request) {
        User loginUser = userService.getAdminLoginUser(request);
        if (loginUser == null || !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Only admin users can access this endpoint");
        }
        return loginUser;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
