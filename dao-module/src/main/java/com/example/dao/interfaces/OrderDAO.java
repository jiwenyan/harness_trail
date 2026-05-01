package com.example.dao.interfaces;

import com.example.data.entity.OrderEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
public interface OrderDAO {

    /**
     * 根据ID查找订单
     */
    Optional<OrderEntity> findById(Long id);

    /**
     * 根据订单号查找订单
     */
    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    /**
     * 保存订单
     */
    OrderEntity save(OrderEntity order);

    /**
     * 更新订单
     */
    OrderEntity update(OrderEntity order);

    /**
     * 删除订单
     */
    void deleteById(Long id);

    /**
     * 根据用户ID获取订单
     */
    List<OrderEntity> findByUserId(Long userId);

    /**
     * 根据用户ID获取订单（分页）
     */
    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据订单状态获取订单
     */
    List<OrderEntity> findByStatus(OrderStatus status);

    /**
     * 根据订单状态获取订单（分页）
     */
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * 根据支付状态获取订单
     */
    List<OrderEntity> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * 根据支付状态获取订单（分页）
     */
    Page<OrderEntity> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    /**
     * 根据用户ID和状态获取订单
     */
    List<OrderEntity> findByUserIdAndStatus(Long userId, OrderStatus status);

    /**
     * 根据创建时间范围获取订单
     */
    List<OrderEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据创建时间范围获取订单（分页）
     */
    Page<OrderEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * 获取所有订单（分页）
     */
    Page<OrderEntity> findAll(Pageable pageable);

    /**
     * 更新订单状态
     */
    void updateStatus(Long orderId, OrderStatus status);

    /**
     * 更新支付状态
     */
    void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);

    /**
     * 检查订单是否存在
     */
    boolean existsById(Long id);
}