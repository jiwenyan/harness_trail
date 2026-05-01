package com.example.dao.interfaces;

import com.example.data.entity.OrderItemEntity;

import java.util.List;
import java.util.Optional;

/**
 * 订单项数据访问接口
 */
public interface OrderItemDAO {

    /**
     * 根据ID查找订单项
     */
    Optional<OrderItemEntity> findById(Long id);

    /**
     * 保存订单项
     */
    OrderItemEntity save(OrderItemEntity orderItem);

    /**
     * 批量保存订单项
     */
    List<OrderItemEntity> saveAll(List<OrderItemEntity> orderItems);

    /**
     * 更新订单项
     */
    OrderItemEntity update(OrderItemEntity orderItem);

    /**
     * 删除订单项
     */
    void deleteById(Long id);

    /**
     * 根据订单ID获取订单项
     */
    List<OrderItemEntity> findByOrderId(Long orderId);

    /**
     * 根据菜品ID获取订单项
     */
    List<OrderItemEntity> findByFoodItemId(Long foodItemId);

    /**
     * 根据订单ID删除订单项
     */
    void deleteByOrderId(Long orderId);

    /**
     * 检查订单项是否存在
     */
    boolean existsById(Long id);
}