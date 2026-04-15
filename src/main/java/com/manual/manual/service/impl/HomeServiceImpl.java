package com.manual.manual.service.impl;

import com.manual.manual.mapper.HomeMapper;
import com.manual.manual.model.vo.home.HomePageVO;
import com.manual.manual.service.HomeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class HomeServiceImpl implements HomeService {

    @Resource
    private HomeMapper homeMapper;

    @Override
    public HomePageVO getHomePage() {
        HomePageVO homePageVO = new HomePageVO();
        homePageVO.setCategories(defaultList(homeMapper.selectCategories()));
        homePageVO.setProducts(defaultList(homeMapper.selectProducts()));
        homePageVO.setArtisans(defaultList(homeMapper.selectArtisans()));
        homePageVO.setRecentOrders(defaultList(homeMapper.selectRecentOrders()));
        return homePageVO;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
