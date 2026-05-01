package com.example.service.interfaces;

import com.example.data.entity.OrderEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 根据ID获取订单
     */
    Optional<OrderEntity> getOrderById(Long id);

    /**
     * 根据订单号获取订单
     */
    Optional<OrderEntity> getOrderByOrderNumber(String orderNumber);

    /**
     * 创建订单
     */
    OrderEntity createOrder(OrderEntity order);

    /**
     * 更新订单
     */
    OrderEntity updateOrder(Long id, OrderEntity order);

    /**
     * 删除订单
     */
    void deleteOrder(Long id);

    /**
     * 根据用户ID获取订单
     */
    List<OrderEntity> getOrdersByUserId(Long userId);

    /**
     * 根据用户ID获取订单（分页）
     */
    Page<OrderEntity> getOrdersByUserId(Long userId, Pageable pageable);

    /**
     * 根据订单状态获取订单
     */
    List<OrderEntity> getOrdersByStatus(OrderStatus status);

    /**
     * 根据订单状态获取订单（分页）
     */
    Page<OrderEntity> getOrdersByStatus(OrderStatus status, Pageable pageable);

    /**
     * 根据支付状态获取订单
     */
    List<OrderEntity> getOrdersByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * 根据支付状态获取订单（分页）
     */
    Page<OrderEntity> getOrdersByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    /**
     * 获取所有订单（分页）
     */
    Page<OrderEntity> getAllOrders(Pageable pageable);

    /**
     * 更新订单状态
     */
    OrderEntity updateOrderStatus(Long orderId, OrderStatus status);

    /**
     * 更新支付状态
     */
    OrderEntity updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    /**
     * 取消订单
     */
    OrderEntity cancelOrder(Long orderId);

    /**
     * 计算订单总金额
     */
    BigDecimal calculateTotalAmount(Long orderId);

    /**
     * 确认订单
     */
    boolean confirmOrder(Long orderId);

    /**
     * 开始配送订单
     */
    boolean startDelivery(Long orderId);

    /**
     * 完成订单配送
     */
    boolean completeDelivery(Long orderId);

    /**
     * 根据时间范围获取订单
     */
    List<OrderEntity> getOrdersByTimeRange(LocalDateTime start, LocalDateTime end);

    /**
     * 根据时间范围获取订单（分页）
     */
    Page<OrderEntity> getOrdersByTimeRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * 计算订单总金额（旧方法，兼容性）
     */
    double calculateOrderTotal(Long orderId);
}