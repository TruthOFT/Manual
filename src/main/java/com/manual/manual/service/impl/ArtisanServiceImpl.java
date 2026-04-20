package com.manual.manual.service.impl;

import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.ArtisanMapper;
import com.manual.manual.model.vo.artisan.ArtisanDetailVO;
import com.manual.manual.model.vo.artisan.ArtisanListItemVO;
import com.manual.manual.service.ArtisanService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ArtisanServiceImpl implements ArtisanService {

    @Resource
    private ArtisanMapper artisanMapper;

    @Override
    public List<ArtisanListItemVO> listArtisans() {
        return defaultList(artisanMapper.selectArtisans());
    }

    @Override
    public ArtisanDetailVO getArtisanDetail(Long artisanId) {
        if (artisanId == null || artisanId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Artisan id is invalid");
        }
        ArtisanDetailVO artisanDetailVO = artisanMapper.selectArtisanDetail(artisanId);
        if (artisanDetailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Artisan not found");
        }
        artisanDetailVO.setProducts(defaultList(artisanMapper.selectArtisanProducts(artisanId)));
        return artisanDetailVO;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
