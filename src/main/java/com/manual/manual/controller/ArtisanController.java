package com.manual.manual.controller;

import com.manual.manual.common.BaseResponse;
import com.manual.manual.common.ResultUtils;
import com.manual.manual.model.vo.artisan.ArtisanDetailVO;
import com.manual.manual.model.vo.artisan.ArtisanListItemVO;
import com.manual.manual.service.ArtisanService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/artisans")
public class ArtisanController {

    @Resource
    private ArtisanService artisanService;

    @GetMapping
    public BaseResponse<List<ArtisanListItemVO>> listArtisans() {
        return ResultUtils.success(artisanService.listArtisans(), "获取匠人列表成功");
    }

    @GetMapping("/{artisanId}")
    public BaseResponse<ArtisanDetailVO> getArtisanDetail(@PathVariable Long artisanId) {
        return ResultUtils.success(artisanService.getArtisanDetail(artisanId), "获取匠人详情成功");
    }
}
