package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.home.HomePageVO;
import com.manual.manual.service.HomeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Resource
    private HomeService homeService;

    @GetMapping
    public BaseResponse<HomePageVO> getHomePage() {
        return ResultUtils.success(homeService.getHomePage(), "获取首页数据成功");
    }
}
