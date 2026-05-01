package com.example.service.impl;

import com.example.dao.interfaces.OrderDAO;
import com.example.dao.interfaces.OrderItemDAO;
import com.example.data.entity.OrderEntity;
import com.example.data.entity.OrderItemEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import com.example.service.interfaces.FoodItemService;
import com.example.service.interfaces.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 订单服务实现
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final FoodItemService foodItemService;

    @Autowired
    public OrderServiceImpl(OrderDAO orderDAO, OrderItemDAO orderItemDAO, FoodItemService foodItemService) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.foodItemService = foodItemService;
    }

    @Override
    public Optional<OrderEntity> getOrderById(Long id) {
        return orderDAO.findById(id);
    }

    @Override
    public Optional<OrderEntity> getOrderByOrderNumber(String orderNumber) {
        return orderDAO.findByOrderNumber(orderNumber);
    }

    @Override
    public OrderEntity createOrder(OrderEntity order) {
        validateOrderData(order);

        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // 生成订单号
        if (order.getOrderNumber() == null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            int random = (int)(Math.random() * 1000);
            order.setOrderNumber("ORD" + timestamp + random);
        }

        // 计算订单总金额
        BigDecimal calculatedTotal = BigDecimal.ZERO;
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItemEntity orderItem : order.getOrderItems()) {
                if (orderItem.getTotalPrice() == null && orderItem.getUnitPrice() != null && orderItem.getQuantity() != null) {
                    orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
                }
                if (orderItem.getTotalPrice() != null) {
                    calculatedTotal = calculatedTotal.add(orderItem.getTotalPrice());
                }
            }
        }
        order.setTotalAmount(calculatedTotal);

        OrderEntity savedOrder = orderDAO.save(order);

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            for (OrderItemEntity orderItem : order.getOrderItems()) {
                orderItem.setOrderId(savedOrder.getId());
                orderItemDAO.save(orderItem);

                // 扣减库存
                foodItemService.reduceStock(orderItem.getFoodItemId(), orderItem.getQuantity());
            }
        }

        return savedOrder;
    }

    @Override
    public OrderEntity updateOrder(Long id, OrderEntity order) {
        OrderEntity existingOrder = orderDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        existingOrder.setDeliveryAddress(order.getDeliveryAddress());
        existingOrder.setDeliveryInstructions(order.getDeliveryInstructions());

        return orderDAO.update(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderDAO.existsById(id)) {
            throw new IllegalArgumentException("订单不存在");
        }

        orderItemDAO.deleteByOrderId(id);
        orderDAO.deleteById(id);
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(Long userId) {
        return orderDAO.findByUserId(userId);
    }

    @Override
    public Page<OrderEntity> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderDAO.findByUserId(userId, pageable);
    }

    @Override
    public List<OrderEntity> getOrdersByStatus(OrderStatus status) {
        return orderDAO.findByStatus(status);
    }

    @Override
    public Page<OrderEntity> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderDAO.findByStatus(status, pageable);
    }

    @Override
    public List<OrderEntity> getOrdersByPaymentStatus(PaymentStatus paymentStatus) {
        return orderDAO.findByPaymentStatus(paymentStatus);
    }

    @Override
    public Page<OrderEntity> getOrdersByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable) {
        return orderDAO.findByPaymentStatus(paymentStatus, pageable);
    }

    @Override
    public Page<OrderEntity> getAllOrders(Pageable pageable) {
        return orderDAO.findAll(pageable);
    }

    @Override
    public OrderEntity updateOrderStatus(Long orderId, OrderStatus status) {
        OrderEntity order = orderDAO.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 验证状态流转
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        return orderDAO.update(order);
    }

    @Override
    public OrderEntity updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        OrderEntity order = orderDAO.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        order.setPaymentStatus(paymentStatus);
        return orderDAO.update(order);
    }

    @Override
    public OrderEntity cancelOrder(Long orderId) {
        OrderEntity order = orderDAO.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("只能取消待处理的订单");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderDAO.update(order);
    }

    @Override
    public boolean confirmOrder(Long orderId) {
        Optional<OrderEntity> order = orderDAO.findById(orderId);
        if (order.isPresent() && order.get().getStatus() == OrderStatus.PENDING) {
            orderDAO.updateStatus(orderId, OrderStatus.CONFIRMED);
            return true;
        }
        return false;
    }

    @Override
    public boolean startDelivery(Long orderId) {
        Optional<OrderEntity> order = orderDAO.findById(orderId);
        if (order.isPresent() && order.get().getStatus() == OrderStatus.CONFIRMED) {
            orderDAO.updateStatus(orderId, OrderStatus.DELIVERING);
            return true;
        }
        return false;
    }

    @Override
    public boolean completeDelivery(Long orderId) {
        Optional<OrderEntity> order = orderDAO.findById(orderId);
        if (order.isPresent() && order.get().getStatus() == OrderStatus.DELIVERING) {
            orderDAO.updateStatus(orderId, OrderStatus.DELIVERED);
            return true;
        }
        return false;
    }

    @Override
    public List<OrderEntity> getOrdersByTimeRange(LocalDateTime start, LocalDateTime end) {
        return orderDAO.findByCreatedAtBetween(start, end);
    }

    @Override
    public Page<OrderEntity> getOrdersByTimeRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return orderDAO.findByCreatedAtBetween(start, end, pageable);
    }

    @Override
    public double calculateOrderTotal(Long orderId) {
        return calculateTotalAmount(orderId).doubleValue();
    }

    @Override
    public BigDecimal calculateTotalAmount(Long orderId) {
        OrderEntity order = orderDAO.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));

        // 从订单项重新计算总金额
        List<OrderItemEntity> items = orderItemDAO.findByOrderId(orderId);
        if (items == null || items.isEmpty()) {
            return order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        }

        return items.stream()
                .map(OrderItemEntity::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 验证状态流转是否合法
     */
    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        if (current == target) {
            return;
        }

        switch (current) {
            case PENDING:
                if (target != OrderStatus.CONFIRMED && target != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("待处理订单只能确认为已确认或取消");
                }
                break;
            case CONFIRMED:
                if (target != OrderStatus.PREPARING) {
                    throw new IllegalStateException("已确认订单只能进入准备中");
                }
                break;
            case PREPARING:
                if (target != OrderStatus.READY) {
                    throw new IllegalStateException("准备中订单只能进入已就绪");
                }
                break;
            case READY:
                if (target != OrderStatus.DELIVERING) {
                    throw new IllegalStateException("已就绪订单只能进入配送中");
                }
                break;
            case DELIVERING:
                if (target != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("配送中订单只能进入已送达");
                }
                break;
            case DELIVERED:
                if (target != OrderStatus.COMPLETED) {
                    throw new IllegalStateException("已送达订单只能进入已完成");
                }
                break;
            case COMPLETED:
                throw new IllegalStateException("已完成订单不能修改状态");
            case CANCELLED:
                throw new IllegalStateException("已取消订单不能修改状态");
        }
    }

    /**
     * 验证订单数据
     */
    private void validateOrderData(OrderEntity order) {
        if (order.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }
        if (order.getDeliveryAddress() == null || order.getDeliveryAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("配送地址不能为空");
        }
    }
}
