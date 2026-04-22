package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.dto.category.AdminCategorySaveRequest;
import com.manual.manual.model.vo.category.AdminCategoryVO;
import com.manual.manual.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping
    public BaseResponse<List<AdminCategoryVO>> listCategories(HttpServletRequest request) {
        return ResultUtils.success(categoryService.listAdminCategories(request), "获取分类列表成功");
    }

    @GetMapping("/{id}")
    public BaseResponse<AdminCategoryVO> getCategory(@PathVariable Long id, HttpServletRequest request) {
        return ResultUtils.success(categoryService.getAdminCategory(id, request), "获取分类详情成功");
    }

    @PostMapping
    public BaseResponse<Long> createCategory(@RequestBody AdminCategorySaveRequest requestBody,
                                             HttpServletRequest request) {
        return ResultUtils.success(categoryService.createAdminCategory(requestBody, request), "创建分类成功");
    }

    @PutMapping("/{id}")
    public BaseResponse<Boolean> updateCategory(@PathVariable Long id,
                                                @RequestBody AdminCategorySaveRequest requestBody,
                                                HttpServletRequest request) {
        return ResultUtils.success(categoryService.updateAdminCategory(id, requestBody, request), "更新分类成功");
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        return ResultUtils.success(categoryService.deleteAdminCategory(id, request), "删除分类成功");
    }
}
