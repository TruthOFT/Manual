package com.manual.manual.model.vo.home;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HomePageVO {

    private List<HomeCategoryVO> categories = new ArrayList<>();

    private List<HomeProductVO> products = new ArrayList<>();

    private List<HomeRecentOrderVO> recentOrders = new ArrayList<>();
}
