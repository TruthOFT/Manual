package com.manual.manual.service;

import com.manual.manual.model.vo.artisan.ArtisanDetailVO;
import com.manual.manual.model.vo.artisan.ArtisanListItemVO;

import java.util.List;

public interface ArtisanService {

    List<ArtisanListItemVO> listArtisans();

    ArtisanDetailVO getArtisanDetail(Long artisanId);
}
