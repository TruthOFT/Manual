package com.manual.manual.service;

import com.manual.manual.model.dto.artisancenter.ArtisanCenterCustomRequirementActionRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterOrderShipRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProductSaveRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProfileUpdateRequest;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterDashboardVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCategoryVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductPageVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProfileVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ArtisanCenterService {

    ArtisanCenterDashboardVO getDashboard(HttpServletRequest request);

    ArtisanCenterProfileVO getProfile(HttpServletRequest request);

    ArtisanCenterProfileVO updateProfile(ArtisanCenterProfileUpdateRequest updateRequest, HttpServletRequest request);

    ArtisanCenterProductPageVO listProducts(Integer auditStatus, Integer status, String keyword, HttpServletRequest request);

    List<ArtisanCenterCategoryVO> listProductCategories(HttpServletRequest request);

    Long createProduct(ArtisanCenterProductSaveRequest saveRequest, HttpServletRequest request);

    ArtisanCenterProductDetailVO getProductDetail(Long productId, HttpServletRequest request);

    Boolean updateProduct(Long productId, ArtisanCenterProductSaveRequest saveRequest, HttpServletRequest request);

    Boolean submitAudit(Long productId, HttpServletRequest request);

    Boolean onShelf(Long productId, HttpServletRequest request);

    Boolean offShelf(Long productId, HttpServletRequest request);

    List<ArtisanCenterOrderListItemVO> listOrders(Integer orderStatus, String keyword, HttpServletRequest request);

    ArtisanCenterOrderDetailVO getOrderDetail(Long orderItemId, HttpServletRequest request);

    Boolean shipOrder(Long orderItemId, ArtisanCenterOrderShipRequest shipRequest, HttpServletRequest request);

    List<ArtisanCenterCustomRequirementListItemVO> listCustomRequirements(Integer confirmStatus,
                                                                          String keyword,
                                                                          HttpServletRequest request);

    ArtisanCenterCustomRequirementDetailVO getCustomRequirementDetail(Long id, HttpServletRequest request);

    Boolean acceptCustomRequirement(Long id,
                                    ArtisanCenterCustomRequirementActionRequest actionRequest,
                                    HttpServletRequest request);

    Boolean rejectCustomRequirement(Long id,
                                    ArtisanCenterCustomRequirementActionRequest actionRequest,
                                    HttpServletRequest request);

    Boolean markCustomRequirementProcessing(Long id,
                                            ArtisanCenterCustomRequirementActionRequest actionRequest,
                                            HttpServletRequest request);

    Boolean markCustomRequirementComplete(Long id,
                                          ArtisanCenterCustomRequirementActionRequest actionRequest,
                                          HttpServletRequest request);
}
