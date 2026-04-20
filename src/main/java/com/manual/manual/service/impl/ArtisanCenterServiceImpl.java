package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.ArtisanCenterCustomRequirementMapper;
import com.manual.manual.mapper.ArtisanCenterOrderMapper;
import com.manual.manual.mapper.ArtisanCenterProductMapper;
import com.manual.manual.mapper.ArtisanCenterProfileMapper;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterCustomRequirementActionRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterOrderShipRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProductImageRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProductMaterialRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProductSaveRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProductSkuRequest;
import com.manual.manual.model.dto.artisancenter.ArtisanCenterProfileUpdateRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCategoryVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterCustomRequirementListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterDashboardVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterOrderListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductDetailVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductListItemVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProductPageVO;
import com.manual.manual.model.vo.artisancenter.ArtisanCenterProfileVO;
import com.manual.manual.service.ArtisanCenterService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.manual.manual.constant.UserConstant.ARTISAN_ROLE;

@Service
public class ArtisanCenterServiceImpl implements ArtisanCenterService {

    private static final int PRODUCT_AUDIT_DRAFT = -1;
    private static final int PRODUCT_AUDIT_PENDING = 0;
    private static final int PRODUCT_AUDIT_APPROVED = 1;
    private static final int PRODUCT_STATUS_DRAFT = 0;
    private static final int PRODUCT_STATUS_ON_SHELF = 1;
    private static final int PRODUCT_STATUS_OFF_SHELF = 2;
    private static final int CUSTOM_STATUS_PENDING = 0;
    private static final int CUSTOM_STATUS_ACCEPTED = 1;
    private static final int CUSTOM_STATUS_REJECTED = 2;
    private static final int CUSTOM_STATUS_PROCESSING = 3;
    private static final int CUSTOM_STATUS_COMPLETED = 4;

    @Resource
    private UserService userService;

    @Resource
    private ArtisanCenterProfileMapper artisanCenterProfileMapper;

    @Resource
    private ArtisanCenterProductMapper artisanCenterProductMapper;

    @Resource
    private ArtisanCenterOrderMapper artisanCenterOrderMapper;

    @Resource
    private ArtisanCenterCustomRequirementMapper artisanCenterCustomRequirementMapper;

    @Override
    public ArtisanCenterDashboardVO getDashboard(HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        ArtisanCenterDashboardVO dashboardVO = artisanCenterProfileMapper.selectDashboard(artisanId);
        if (dashboardVO == null) {
            return new ArtisanCenterDashboardVO();
        }
        if (dashboardVO.getRecentSevenDaysAmount() == null) {
            dashboardVO.setRecentSevenDaysAmount(BigDecimal.ZERO);
        }
        if (dashboardVO.getRecentSevenDaysSales() == null) {
            dashboardVO.setRecentSevenDaysSales(0);
        }
        return dashboardVO;
    }

    @Override
    public ArtisanCenterProfileVO getProfile(HttpServletRequest request) {
        User loginUser = requireArtisanUser(request);
        ArtisanCenterProfileVO profileVO = artisanCenterProfileMapper.selectProfileByUserId(loginUser.getId());
        if (profileVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Artisan profile not found");
        }
        return profileVO;
    }

    @Override
    public ArtisanCenterProfileVO updateProfile(ArtisanCenterProfileUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long artisanId = requireArtisanId(request);
        sanitizeProfileRequest(updateRequest);
        artisanCenterProfileMapper.updateProfile(artisanId, updateRequest);
        return getProfile(request);
    }

    @Override
    public ArtisanCenterProductPageVO listProducts(Integer auditStatus, Integer status, String keyword, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        ArtisanCenterProductPageVO pageVO = new ArtisanCenterProductPageVO();
        List<ArtisanCenterCategoryVO> categories = artisanCenterProductMapper.selectProductCategories();
        List<ArtisanCenterProductListItemVO> products = artisanCenterProductMapper.selectProducts(
                artisanId,
                auditStatus,
                status,
                trim(keyword)
        );
        pageVO.setCategories(defaultList(categories));
        pageVO.setProducts(defaultList(products));
        return pageVO;
    }

    @Override
    public List<ArtisanCenterCategoryVO> listProductCategories(HttpServletRequest request) {
        requireArtisanId(request);
        return defaultList(artisanCenterProductMapper.selectProductCategories());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ArtisanCenterProductSaveRequest saveRequest, HttpServletRequest request) {
        if (saveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long artisanId = requireArtisanId(request);
        sanitizeProductRequest(saveRequest);
        Long productId = IdWorker.getId();
        artisanCenterProductMapper.insertProduct(
                productId,
                artisanId,
                saveRequest,
                calculateInventory(saveRequest),
                calculateMinPrice(saveRequest),
                calculateMaxPrice(saveRequest),
                PRODUCT_AUDIT_DRAFT,
                PRODUCT_STATUS_DRAFT
        );
        replaceProductChildren(productId, saveRequest);
        return productId;
    }

    @Override
    public ArtisanCenterProductDetailVO getProductDetail(Long productId, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        return buildProductDetail(artisanId, productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProduct(Long productId, ArtisanCenterProductSaveRequest saveRequest, HttpServletRequest request) {
        if (saveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long artisanId = requireArtisanId(request);
        requireOwnedProductSummary(artisanId, productId);
        sanitizeProductRequest(saveRequest);
        artisanCenterProductMapper.updateProduct(
                productId,
                artisanId,
                saveRequest,
                calculateInventory(saveRequest),
                calculateMinPrice(saveRequest),
                calculateMaxPrice(saveRequest)
        );
        artisanCenterProductMapper.updateProductAuditStatus(artisanId, productId, PRODUCT_AUDIT_PENDING);
        artisanCenterProductMapper.updateProductStatus(artisanId, productId, PRODUCT_STATUS_DRAFT);
        replaceProductChildren(productId, saveRequest);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitAudit(Long productId, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        ArtisanCenterProductListItemVO product = requireOwnedProductSummary(artisanId, productId);
        if (PRODUCT_AUDIT_PENDING == safeInteger(product.getAuditStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Product is already pending audit");
        }
        artisanCenterProductMapper.updateProductAuditStatus(artisanId, productId, PRODUCT_AUDIT_PENDING);
        if (safeInteger(product.getStatus()) == PRODUCT_STATUS_ON_SHELF) {
            artisanCenterProductMapper.updateProductStatus(artisanId, productId, PRODUCT_STATUS_DRAFT);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean onShelf(Long productId, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        ArtisanCenterProductListItemVO product = requireOwnedProductSummary(artisanId, productId);
        if (safeInteger(product.getAuditStatus()) != PRODUCT_AUDIT_APPROVED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Only approved products can be on shelf");
        }
        if (safeInteger(product.getStatus()) == PRODUCT_STATUS_ON_SHELF) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Product is already on shelf");
        }
        artisanCenterProductMapper.updateProductStatus(artisanId, productId, PRODUCT_STATUS_ON_SHELF);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean offShelf(Long productId, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        ArtisanCenterProductListItemVO product = requireOwnedProductSummary(artisanId, productId);
        if (safeInteger(product.getStatus()) != PRODUCT_STATUS_ON_SHELF) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Only on-shelf products can be off shelf");
        }
        artisanCenterProductMapper.updateProductStatus(artisanId, productId, PRODUCT_STATUS_OFF_SHELF);
        return true;
    }

    @Override
    public List<ArtisanCenterOrderListItemVO> listOrders(Integer orderStatus, String keyword, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        return defaultList(artisanCenterOrderMapper.selectOrders(artisanId, orderStatus, trim(keyword)));
    }

    @Override
    public ArtisanCenterOrderDetailVO getOrderDetail(Long orderItemId, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        return buildOrderDetail(artisanId, orderItemId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean shipOrder(Long orderItemId, ArtisanCenterOrderShipRequest shipRequest, HttpServletRequest request) {
        if (shipRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long artisanId = requireArtisanId(request);
        ArtisanCenterOrderDetailVO orderDetail = buildOrderDetail(artisanId, orderItemId);
        if (safeInteger(orderDetail.getOrderStatus()) != 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Only pending shipment orders can be shipped");
        }
        String companyName = trim(shipRequest.getCompanyName());
        String trackingNo = trim(shipRequest.getTrackingNo());
        if (companyName == null || trackingNo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Logistics company and tracking number are required");
        }
        String senderName = trim(shipRequest.getSenderName());
        if (senderName == null) {
            senderName = "匠人店铺";
        }
        Long logisticsId = artisanCenterOrderMapper.selectOrderLogisticsId(orderDetail.getOrderId());
        if (logisticsId == null) {
            artisanCenterOrderMapper.insertOrderLogistics(
                    IdWorker.getId(),
                    orderDetail.getOrderId(),
                    orderDetail.getOrderNo(),
                    companyName,
                    trackingNo,
                    senderName,
                    trim(shipRequest.getSenderPhone()),
                    orderDetail.getReceiverName(),
                    orderDetail.getReceiverPhone(),
                    trim(shipRequest.getLogisticsRemark())
            );
        } else {
            artisanCenterOrderMapper.updateOrderLogistics(
                    logisticsId,
                    companyName,
                    trackingNo,
                    senderName,
                    trim(shipRequest.getSenderPhone()),
                    orderDetail.getReceiverName(),
                    orderDetail.getReceiverPhone(),
                    trim(shipRequest.getLogisticsRemark())
            );
        }
        artisanCenterOrderMapper.shipOrder(orderDetail.getOrderId());
        return true;
    }

    @Override
    public List<ArtisanCenterCustomRequirementListItemVO> listCustomRequirements(Integer confirmStatus,
                                                                                 String keyword,
                                                                                 HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        return defaultList(artisanCenterCustomRequirementMapper.selectCustomRequirements(
                artisanId,
                confirmStatus,
                trim(keyword)
        ));
    }

    @Override
    public ArtisanCenterCustomRequirementDetailVO getCustomRequirementDetail(Long id, HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        return requireCustomRequirementDetail(artisanId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean acceptCustomRequirement(Long id,
                                           ArtisanCenterCustomRequirementActionRequest actionRequest,
                                           HttpServletRequest request) {
        return updateCustomRequirementStatus(id, request, actionRequest, CUSTOM_STATUS_PENDING, CUSTOM_STATUS_ACCEPTED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean rejectCustomRequirement(Long id,
                                           ArtisanCenterCustomRequirementActionRequest actionRequest,
                                           HttpServletRequest request) {
        return updateCustomRequirementStatus(id, request, actionRequest, CUSTOM_STATUS_PENDING, CUSTOM_STATUS_REJECTED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean markCustomRequirementProcessing(Long id,
                                                   ArtisanCenterCustomRequirementActionRequest actionRequest,
                                                   HttpServletRequest request) {
        return updateCustomRequirementStatus(id, request, actionRequest, CUSTOM_STATUS_ACCEPTED, CUSTOM_STATUS_PROCESSING);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean markCustomRequirementComplete(Long id,
                                                 ArtisanCenterCustomRequirementActionRequest actionRequest,
                                                 HttpServletRequest request) {
        Long artisanId = requireArtisanId(request);
        ArtisanCenterCustomRequirementDetailVO detailVO = requireCustomRequirementDetail(artisanId, id);
        int currentStatus = safeInteger(detailVO.getConfirmStatus());
        if (currentStatus != CUSTOM_STATUS_ACCEPTED && currentStatus != CUSTOM_STATUS_PROCESSING) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Only accepted or processing requirements can be completed");
        }
        artisanCenterCustomRequirementMapper.updateCustomRequirementStatus(
                id,
                CUSTOM_STATUS_COMPLETED,
                trim(actionRequest == null ? null : actionRequest.getConfirmRemark())
        );
        return true;
    }

    private Boolean updateCustomRequirementStatus(Long id,
                                                  HttpServletRequest request,
                                                  ArtisanCenterCustomRequirementActionRequest actionRequest,
                                                  int expectedStatus,
                                                  int nextStatus) {
        Long artisanId = requireArtisanId(request);
        ArtisanCenterCustomRequirementDetailVO detailVO = requireCustomRequirementDetail(artisanId, id);
        if (safeInteger(detailVO.getConfirmStatus()) != expectedStatus) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "Current custom requirement status does not allow this action");
        }
        artisanCenterCustomRequirementMapper.updateCustomRequirementStatus(
                id,
                nextStatus,
                trim(actionRequest == null ? null : actionRequest.getConfirmRemark())
        );
        return true;
    }

    private ArtisanCenterProductDetailVO buildProductDetail(Long artisanId, Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Product id is invalid");
        }
        ArtisanCenterProductDetailVO detailVO = artisanCenterProductMapper.selectProductDetail(artisanId, productId);
        if (detailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Product not found");
        }
        detailVO.setImages(defaultList(artisanCenterProductMapper.selectProductImages(productId)));
        detailVO.setMaterials(defaultList(artisanCenterProductMapper.selectProductMaterials(productId)));
        detailVO.setSkus(defaultList(artisanCenterProductMapper.selectProductSkus(productId)));
        detailVO.setReviews(defaultList(artisanCenterProductMapper.selectProductReviews(productId)));
        return detailVO;
    }

    private ArtisanCenterOrderDetailVO buildOrderDetail(Long artisanId, Long orderItemId) {
        if (orderItemId == null || orderItemId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Order item id is invalid");
        }
        ArtisanCenterOrderDetailVO detailVO = artisanCenterOrderMapper.selectOrderDetail(artisanId, orderItemId);
        if (detailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Order detail not found");
        }
        detailVO.setLogistics(artisanCenterOrderMapper.selectOrderLogistics(detailVO.getOrderId()));
        return detailVO;
    }

    private ArtisanCenterCustomRequirementDetailVO requireCustomRequirementDetail(Long artisanId, Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Custom requirement id is invalid");
        }
        ArtisanCenterCustomRequirementDetailVO detailVO = artisanCenterCustomRequirementMapper.selectCustomRequirementDetail(artisanId, id);
        if (detailVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Custom requirement not found");
        }
        return detailVO;
    }

    private ArtisanCenterProductListItemVO requireOwnedProductSummary(Long artisanId, Long productId) {
        if (productId == null || productId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Product id is invalid");
        }
        ArtisanCenterProductListItemVO productVO = artisanCenterProductMapper.selectOwnedProductSummary(artisanId, productId);
        if (productVO == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Product not found");
        }
        return productVO;
    }

    private void sanitizeProfileRequest(ArtisanCenterProfileUpdateRequest request) {
        request.setArtisanName(requiredTrim(request.getArtisanName(), "Artisan name is required"));
        request.setShopName(requiredTrim(request.getShopName(), "Shop name is required"));
        request.setArtisanAvatar(trim(request.getArtisanAvatar()));
        request.setCoverUrl(trim(request.getCoverUrl()));
        request.setArtisanStory(trim(request.getArtisanStory()));
        request.setCraftCategory(trim(request.getCraftCategory()));
        request.setOriginPlace(trim(request.getOriginPlace()));
        request.setExperienceYears(nonNegative(request.getExperienceYears()));
        request.setSupportCustom(normalizeBooleanNumber(request.getSupportCustom()));
        request.setContactPhone(trim(request.getContactPhone()));
    }

    private void sanitizeProductRequest(ArtisanCenterProductSaveRequest request) {
        if (request.getCategoryId() == null || request.getCategoryId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Category is required");
        }
        request.setProductName(requiredTrim(request.getProductName(), "Product name is required"));
        request.setProductSubtitle(trim(request.getProductSubtitle()));
        request.setProductCover(requiredTrim(request.getProductCover(), "Product cover is required"));
        request.setProductDesc(requiredTrim(request.getProductDesc(), "Product description is required"));
        request.setCraftType(trim(request.getCraftType()));
        request.setMaterialDesc(trim(request.getMaterialDesc()));
        request.setOriginPlace(trim(request.getOriginPlace()));
        request.setHandmadeCycleDays(nonNegative(request.getHandmadeCycleDays()));
        request.setSupportCustom(normalizeBooleanNumber(request.getSupportCustom()));
        request.setInventory(nonNegative(request.getInventory()));
        request.setSortOrder(defaultZero(request.getSortOrder()));
        List<ArtisanCenterProductImageRequest> images = defaultList(request.getImages());
        List<ArtisanCenterProductMaterialRequest> materials = defaultList(request.getMaterials());
        List<ArtisanCenterProductSkuRequest> skus = defaultList(request.getSkus());
        if (images.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "At least one product image is required");
        }
        if (skus.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "At least one sku is required");
        }
        for (int i = 0; i < images.size(); i++) {
            ArtisanCenterProductImageRequest image = images.get(i);
            image.setImageUrl(requiredTrim(image.getImageUrl(), "Product image url is required"));
            image.setImageType(trim(image.getImageType()) == null ? "detail" : trim(image.getImageType()));
            image.setSortOrder(image.getSortOrder() == null ? i + 1 : image.getSortOrder());
        }
        for (int i = 0; i < materials.size(); i++) {
            ArtisanCenterProductMaterialRequest material = materials.get(i);
            material.setMaterialName(requiredTrim(material.getMaterialName(), "Material name is required"));
            material.setMaterialOrigin(trim(material.getMaterialOrigin()));
            material.setMaterialRatio(trim(material.getMaterialRatio()));
            material.setSortOrder(material.getSortOrder() == null ? i + 1 : material.getSortOrder());
        }
        for (ArtisanCenterProductSkuRequest sku : skus) {
            sku.setSkuName(requiredTrim(sku.getSkuName(), "Sku name is required"));
            sku.setSkuCover(trim(sku.getSkuCover()) == null ? request.getProductCover() : trim(sku.getSkuCover()));
            sku.setSpecText(trim(sku.getSpecText()));
            sku.setMaterialType(trim(sku.getMaterialType()));
            sku.setWeight(defaultDecimal(sku.getWeight()));
            sku.setPrice(requiredPrice(sku.getPrice(), "Sku price is required"));
            sku.setOriginalPrice(sku.getOriginalPrice() == null ? sku.getPrice() : sku.getOriginalPrice());
            sku.setStock(nonNegative(sku.getStock()));
            sku.setStatus(sku.getStatus() == null ? 1 : sku.getStatus());
        }
        request.setImages(images);
        request.setMaterials(materials);
        request.setSkus(skus);
        request.setMinPrice(calculateMinPrice(request));
        request.setMaxPrice(calculateMaxPrice(request));
        request.setInventory(calculateInventory(request));
    }

    private void replaceProductChildren(Long productId, ArtisanCenterProductSaveRequest request) {
        artisanCenterProductMapper.markProductImagesDeleted(productId);
        artisanCenterProductMapper.markProductMaterialsDeleted(productId);
        artisanCenterProductMapper.markProductSkusDeleted(productId);
        for (ArtisanCenterProductImageRequest image : defaultList(request.getImages())) {
            artisanCenterProductMapper.insertProductImage(
                    IdWorker.getId(),
                    productId,
                    image.getImageUrl(),
                    image.getImageType(),
                    image.getSortOrder()
            );
        }
        for (ArtisanCenterProductMaterialRequest material : defaultList(request.getMaterials())) {
            artisanCenterProductMapper.insertProductMaterial(
                    IdWorker.getId(),
                    productId,
                    material.getMaterialName(),
                    material.getMaterialOrigin(),
                    material.getMaterialRatio(),
                    material.getSortOrder()
            );
        }
        for (ArtisanCenterProductSkuRequest sku : defaultList(request.getSkus())) {
            Long skuId = IdWorker.getId();
            artisanCenterProductMapper.insertProductSku(
                    skuId,
                    productId,
                    "sku-" + skuId,
                    sku.getSkuName(),
                    sku.getSkuCover(),
                    sku.getSpecText(),
                    sku.getMaterialType(),
                    defaultDecimal(sku.getWeight()),
                    sku.getPrice(),
                    sku.getOriginalPrice(),
                    sku.getStock(),
                    sku.getStatus()
            );
        }
    }

    private User requireArtisanUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null || !ARTISAN_ROLE.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "Only artisan users can access this endpoint");
        }
        return loginUser;
    }

    private Long requireArtisanId(HttpServletRequest request) {
        User loginUser = requireArtisanUser(request);
        Long artisanId = artisanCenterProfileMapper.selectArtisanIdByUserId(loginUser.getId());
        if (artisanId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Artisan profile not found");
        }
        return artisanId;
    }

    private String requiredTrim(String value, String message) {
        String trimmed = trim(value);
        if (trimmed == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
        return trimmed;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer nonNegative(Integer value) {
        if (value == null) {
            return 0;
        }
        if (value < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Numeric value must be greater than or equal to 0");
        }
        return value;
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private Integer normalizeBooleanNumber(Integer value) {
        return value != null && value == 1 ? 1 : 0;
    }

    private BigDecimal requiredPrice(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
        return value;
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int calculateInventory(ArtisanCenterProductSaveRequest request) {
        if (request.getInventory() != null && request.getInventory() > 0) {
            return request.getInventory();
        }
        int total = 0;
        for (ArtisanCenterProductSkuRequest sku : defaultList(request.getSkus())) {
            total += nonNegative(sku.getStock());
        }
        return total;
    }

    private BigDecimal calculateMinPrice(ArtisanCenterProductSaveRequest request) {
        BigDecimal minPrice = null;
        for (ArtisanCenterProductSkuRequest sku : defaultList(request.getSkus())) {
            if (minPrice == null || sku.getPrice().compareTo(minPrice) < 0) {
                minPrice = sku.getPrice();
            }
        }
        if (minPrice == null) {
            return defaultDecimal(request.getMinPrice());
        }
        return minPrice;
    }

    private BigDecimal calculateMaxPrice(ArtisanCenterProductSaveRequest request) {
        BigDecimal maxPrice = null;
        for (ArtisanCenterProductSkuRequest sku : defaultList(request.getSkus())) {
            if (maxPrice == null || sku.getPrice().compareTo(maxPrice) > 0) {
                maxPrice = sku.getPrice();
            }
        }
        if (maxPrice == null) {
            return defaultDecimal(request.getMaxPrice());
        }
        return maxPrice;
    }

    private int safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
