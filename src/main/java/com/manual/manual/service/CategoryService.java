package com.manual.manual.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.manual.manual.model.dto.category.AdminCategorySaveRequest;
import com.manual.manual.model.entity.Category;
import com.manual.manual.model.vo.category.AdminCategoryVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<AdminCategoryVO> listAdminCategories(HttpServletRequest request);

    AdminCategoryVO getAdminCategory(Long id, HttpServletRequest request);

    long createAdminCategory(AdminCategorySaveRequest requestBody, HttpServletRequest request);

    boolean updateAdminCategory(Long id, AdminCategorySaveRequest requestBody, HttpServletRequest request);

    boolean deleteAdminCategory(Long id, HttpServletRequest request);
}
