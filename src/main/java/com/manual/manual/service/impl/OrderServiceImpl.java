package com.manual.manual.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.manual.manual.common.ErrorCode;
import com.manual.manual.config.OrderProperties;
import com.manual.manual.exception.BusinessException;
import com.manual.manual.mapper.OrderMapper;
import com.manual.manual.messaging.OrderDelayMessageService;
import com.manual.manual.model.dto.order.OrderCreateRequest;
import com.manual.manual.model.dto.order.UserAddressSaveRequest;
import com.manual.manual.model.entity.User;
import com.manual.manual.model.vo.LoginUserVO;
import com.manual.manual.model.vo.order.OrderDetailVO;
import com.manual.manual.model.vo.order.OrderItemVO;
import com.manual.manual.model.vo.order.OrderSkuSnapshotVO;
import com.manual.manual.model.vo.order.UserAddressVO;
import com.manual.manual.service.OrderService;
import com.manual.manual.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final int ORDER_STATUS_PENDING_PAYMENT = 0;
    private static final int ORDER_STATUS_PENDING_SHIPMENT = 1;
    private static final int ORDER_STATUS_CANCELED = 4;
    private static final int PAY_STATUS_UNPAID = 0;
    private static final int PAY_STATUS_PAID = 1;
    private static final int ENABLED = 1;

    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private UserService userService;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDelayMessageService orderDelayMessageService;

    @Resource
    private OrderProperties orderProperties;

    @Override
    public List<UserAddressVO> listCurrentUserAddresses(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return defaultList(orderMapper.selectUserAddresses(loginUser.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressVO createCurrentUserAddress(UserAddressSaveRequest saveRequest, HttpServletRequest request) {
        if (saveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        sanitizeAddress(saveRequest);
        boolean firstAddress = defaultList(orderMapper.selectUserAddresses(loginUser.getId())).isEmpty();
        int isDefault = firstAddress || safeInteger(saveRequest.getIsDefault()) == 1 ? 1 : 0;
        if (isDefault == 1) {
            orderMapper.clearDefaultAddress(loginUser.getId());
        }
        Long addressId = IdWorker.getId();
        orderMapper.insertUserAddress(
                addressId,
                loginUser.getId(),
                saveRequest.getReceiverName(),
                saveRequest.getReceiverPhone(),
                saveRequest.getProvince(),
                saveRequest.getCity(),
                saveRequest.getDistrict(),
                saveRequest.getDetailAddress(),
                trim(saveRequest.getPostalCode()),
                trim(saveRequest.getTagName()),
                isDefault
        );
        return orderMapper.selectUserAddress(loginUser.getId(), addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressVO updateCurrentUserAddress(Long addressId,
                                                  UserAddressSaveRequest saveRequest,
                                                  HttpServletRequest request) {
        if (saveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        requireAddress(loginUser.getId(), addressId);
        sanitizeAddress(saveRequest);
        int isDefault = safeInteger(saveRequest.getIsDefault()) == 1 ? 1 : 0;
        if (isDefault == 1) {
            orderMapper.clearDefaultAddress(loginUser.getId());
        }
        orderMapper.updateUserAddress(
                addressId,
                loginUser.getId(),
                saveRequest.getReceiverName(),
                saveRequest.getReceiverPhone(),
                saveRequest.getProvince(),
                saveRequest.getCity(),
                saveRequest.getDistrict(),
                saveRequest.getDetailAddress(),
                trim(saveRequest.getPostalCode()),
                trim(saveRequest.getTagName()),
                isDefault
        );
        return orderMapper.selectUserAddress(loginUser.getId(), addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCurrentUserAddress(Long addressId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        UserAddressVO address = requireAddress(loginUser.getId(), addressId);
        orderMapper.deleteUserAddress(loginUser.getId(), addressId);
        if (safeInteger(address.getIsDefault()) == 1) {
            List<UserAddressVO> restAddresses = defaultList(orderMapper.selectUserAddresses(loginUser.getId()));
            if (!restAddresses.isEmpty()) {
                UserAddressVO nextDefault = restAddresses.get(0);
                orderMapper.clearDefaultAddress(loginUser.getId());
                orderMapper.updateUserAddress(
                        nextDefault.getId(),
                        loginUser.getId(),
                        nextDefault.getReceiverName(),
                        nextDefault.getReceiverPhone(),
                        nextDefault.getProvince(),
                        nextDefault.getCity(),
                        nextDefault.getDistrict(),
                        nextDefault.getDetailAddress(),
                        nextDefault.getPostalCode(),
                        nextDefault.getTagName(),
                        1
                );
            }
        }
        return true;
    }

    @Override
    public List<OrderDetailVO> listCurrentUserOrders(Integer orderStatus, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<OrderDetailVO> orders = defaultList(orderMapper.selectUserOrders(
                loginUser.getId(),
                orderStatus,
                orderProperties.getTimeoutMinutes()
        ));
        orders.forEach(this::attachOrderItem);
        return orders;
    }

    @Override
    public OrderDetailVO getCurrentUserOrder(Long orderId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return requireUserOrder(loginUser.getId(), orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO createOrder(OrderCreateRequest createRequest, HttpServletRequest request) {
        if (createRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long skuId = createRequest.getSkuId();
        Integer quantity = normalizeQuantity(createRequest.getQuantity());
        Long addressId = createRequest.getAddressId();
        if (skuId == null || skuId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "商品规格无效");
        }
        if (addressId == null || addressId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请选择收货地址");
        }
        OrderSkuSnapshotVO sku = orderMapper.selectSkuSnapshot(skuId);
        validateSkuCanBuy(sku, quantity);
        UserAddressVO address = orderMapper.selectUserAddress(loginUser.getId(), addressId);
        if (address == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "收货地址不存在");
        }
        if (orderMapper.lockSkuStock(skuId, quantity) <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
        }
        Long orderId = IdWorker.getId();
        Long orderItemId = IdWorker.getId();
        String orderNo = buildOrderNo(orderId);
        BigDecimal productAmount = sku.getPrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal freightAmount = BigDecimal.ZERO;
        BigDecimal payAmount = productAmount.subtract(discountAmount).add(freightAmount);
        orderMapper.insertOrder(
                orderId,
                orderNo,
                loginUser.getId(),
                addressId,
                productAmount,
                discountAmount,
                freightAmount,
                payAmount,
                trim(createRequest.getBuyerRemark()),
                address
        );
        orderMapper.insertOrderItem(
                orderItemId,
                orderId,
                orderNo,
                loginUser.getId(),
                sku,
                quantity,
                sku.getPrice(),
                productAmount
        );
        orderDelayMessageService.sendOrderCancelDelay(orderId);
        return requireUserOrder(loginUser.getId(), orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUserVO payOrder(Long orderId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        OrderDetailVO order = requireUserOrder(loginUser.getId(), orderId);
        if (safeInteger(order.getPayStatus()) == PAY_STATUS_PAID
                && safeInteger(order.getOrderStatus()) == ORDER_STATUS_PENDING_SHIPMENT) {
            return userService.getLoginUserVO(userService.getLoginUser(request));
        }
        if (safeInteger(order.getOrderStatus()) != ORDER_STATUS_PENDING_PAYMENT
                || safeInteger(order.getPayStatus()) != PAY_STATUS_UNPAID) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只有待支付订单可以支付");
        }
        OrderItemVO item = requireOrderItem(orderId);
        if (orderMapper.deductUserBalance(loginUser.getId(), order.getPayAmount()) <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账户余额不足");
        }
        if (orderMapper.finishSkuStockAfterPay(item.getSkuId(), item.getQuantity()) <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单锁定库存不足");
        }
        orderMapper.refreshProductStockAfterPay(item.getProductId(), item.getQuantity());
        if (orderMapper.markOrderPaid(loginUser.getId(), orderId) <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "订单状态已变化，请刷新后重试");
        }
        return userService.getLoginUserVO(userService.getLoginUser(request));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long orderId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        OrderDetailVO order = requireUserOrder(loginUser.getId(), orderId);
        if (safeInteger(order.getOrderStatus()) == ORDER_STATUS_CANCELED) {
            return true;
        }
        if (safeInteger(order.getOrderStatus()) != ORDER_STATUS_PENDING_PAYMENT
                || safeInteger(order.getPayStatus()) != PAY_STATUS_UNPAID) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "只有待支付订单可以取消");
        }
        if (orderMapper.cancelUserOrder(loginUser.getId(), orderId) > 0) {
            releaseOrderLockedStock(orderId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTimeoutOrder(Long orderId) {
        if (orderId == null || orderId <= 0) {
            return;
        }
        if (orderMapper.cancelTimeoutOrder(orderId) > 0) {
            releaseOrderLockedStock(orderId);
        }
    }

    private void validateSkuCanBuy(OrderSkuSnapshotVO sku, Integer quantity) {
        if (sku == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品规格不存在");
        }
        if (safeInteger(sku.getSkuStatus()) != ENABLED
                || safeInteger(sku.getProductStatus()) != ENABLED
                || safeInteger(sku.getProductAuditStatus()) != ENABLED
                || safeInteger(sku.getArtisanAuditStatus()) != ENABLED
                || safeInteger(sku.getArtisanShelfStatus()) != ENABLED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品当前不可购买");
        }
        int availableStock = safeInteger(sku.getStock()) - safeInteger(sku.getLockedStock());
        if (availableStock < quantity) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "库存不足");
        }
        if (sku.getPrice() == null || sku.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "商品价格异常");
        }
    }

    private UserAddressVO requireAddress(Long userId, Long addressId) {
        if (addressId == null || addressId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Address id is invalid");
        }
        UserAddressVO address = orderMapper.selectUserAddress(userId, addressId);
        if (address == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "Address not found");
        }
        return address;
    }

    private void sanitizeAddress(UserAddressSaveRequest request) {
        request.setReceiverName(requiredTrim(request.getReceiverName(), "Receiver name is required"));
        request.setReceiverPhone(requiredTrim(request.getReceiverPhone(), "Receiver phone is required"));
        request.setProvince(requiredTrim(request.getProvince(), "Province is required"));
        request.setCity(requiredTrim(request.getCity(), "City is required"));
        request.setDistrict(requiredTrim(request.getDistrict(), "District is required"));
        request.setDetailAddress(requiredTrim(request.getDetailAddress(), "Detail address is required"));
        request.setPostalCode(trim(request.getPostalCode()));
        request.setTagName(trim(request.getTagName()));
    }

    private String requiredTrim(String value, String message) {
        String trimmed = trim(value);
        if (trimmed == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, message);
        }
        return trimmed;
    }

    private OrderDetailVO requireUserOrder(Long userId, Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单 id 无效");
        }
        OrderDetailVO order = orderMapper.selectUserOrderDetail(userId, orderId, orderProperties.getTimeoutMinutes());
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        attachOrderItem(order);
        return order;
    }

    private void releaseOrderLockedStock(Long orderId) {
        OrderItemVO item = requireOrderItem(orderId);
        orderMapper.releaseSkuLockedStock(item.getSkuId(), item.getQuantity());
    }

    private OrderItemVO requireOrderItem(Long orderId) {
        OrderItemVO item = orderMapper.selectOrderItem(orderId);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "订单明细不存在");
        }
        return item;
    }

    private void attachOrderItem(OrderDetailVO order) {
        if (order != null && order.getOrderId() != null) {
            order.setItem(orderMapper.selectOrderItem(order.getOrderId()));
        }
    }

    private Integer normalizeQuantity(Integer quantity) {
        if (quantity == null) {
            return 1;
        }
        if (quantity <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "购买数量必须大于 0");
        }
        return quantity;
    }

    private String buildOrderNo(Long orderId) {
        String idText = String.valueOf(orderId);
        String tail = idText.substring(Math.max(0, idText.length() - 6));
        return "mo" + LocalDateTime.now().format(ORDER_NO_TIME_FORMATTER) + tail;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private <T> List<T> defaultList(List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }
}
