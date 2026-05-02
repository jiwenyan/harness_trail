package com.example.service.impl;

import com.example.dao.interfaces.OrderDAO;
import com.example.dao.interfaces.OrderItemDAO;
import com.example.data.entity.OrderEntity;
import com.example.data.entity.OrderItemEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import com.example.service.interfaces.FoodItemService;
import com.example.service.interfaces.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private OrderItemDAO orderItemDAO;

    @Mock
    private FoodItemService foodItemService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderDAO, orderItemDAO, foodItemService);
    }

    private OrderEntity createTestOrder() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setDeliveryAddress("测试地址");
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        return order;
    }

    private List<OrderItemEntity> createTestOrderItems(Long orderId) {
        OrderItemEntity item1 = new OrderItemEntity();
        item1.setId(1L);
        item1.setOrderId(orderId);
        item1.setFoodItemId(1L);
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("50.00"));
        item1.setTotalPrice(new BigDecimal("100.00"));

        OrderItemEntity item2 = new OrderItemEntity();
        item2.setId(2L);
        item2.setOrderId(orderId);
        item2.setFoodItemId(2L);
        item2.setQuantity(1);
        item2.setUnitPrice(new BigDecimal("30.00"));
        item2.setTotalPrice(new BigDecimal("30.00"));

        return Arrays.asList(item1, item2);
    }

    @Test
    void createOrder_shouldReduceStockForEachItem() {
        // Arrange
        OrderEntity order = createTestOrder();
        List<OrderItemEntity> items = createTestOrderItems(null);
        order.setOrderItems(items);

        when(orderDAO.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(orderItemDAO.save(any(OrderItemEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderEntity result = orderService.createOrder(order);

        // Assert
        assertNotNull(result);
        verify(foodItemService).reduceStock(1L, 2);
        verify(foodItemService).reduceStock(2L, 1);
    }

    @Test
    void cancelOrder_shouldIncreaseStockForEachItem() {
        // Arrange
        Long orderId = 1L;
        OrderEntity order = createTestOrder();
        order.setId(orderId);
        List<OrderItemEntity> items = createTestOrderItems(orderId);

        when(orderDAO.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemDAO.findByOrderId(orderId)).thenReturn(items);
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);

        // Act
        OrderEntity result = orderService.cancelOrder(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(foodItemService).increaseStock(1L, 2);
        verify(foodItemService).increaseStock(2L, 1);
    }

    @Test
    void updatePaymentStatus_validTransitionPendingToProcessing() {
        // Arrange
        Long orderId = 1L;
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.PENDING);

        when(orderDAO.findById(orderId)).thenReturn(Optional.of(order));
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);

        // Act
        OrderEntity result = orderService.updatePaymentStatus(orderId, PaymentStatus.PROCESSING);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.PROCESSING, result.getPaymentStatus());
    }

    @Test
    void updatePaymentStatus_validTransitionProcessingToSuccess() {
        // Arrange
        Long orderId = 1L;
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.PROCESSING);

        when(orderDAO.findById(orderId)).thenReturn(Optional.of(order));
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);

        // Act
        OrderEntity result = orderService.updatePaymentStatus(orderId, PaymentStatus.SUCCESS);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
    }

    @Test
    void updatePaymentStatus_invalidTransitionPendingToSuccess() {
        // Arrange
        Long orderId = 1L;
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.PENDING);

        when(orderDAO.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> orderService.updatePaymentStatus(orderId, PaymentStatus.SUCCESS));
    }

    @Test
    void updatePaymentStatus_invalidTransitionSuccessToFailed() {
        // Arrange
        Long orderId = 1L;
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.SUCCESS);

        when(orderDAO.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> orderService.updatePaymentStatus(orderId, PaymentStatus.FAILED));
    }
}
