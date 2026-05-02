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
import java.time.LocalDateTime;
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
        order.setOrderNumber("ORD001");
        order.setUserId(1L);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setDeliveryAddress("test");
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
        item1.setSpecialInstructions("No onions");

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
    void getOrderById_shouldReturnOrderWhenExists() {
        OrderEntity order = createTestOrder();
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        Optional<OrderEntity> result = orderService.getOrderById(1L);
        assertTrue(result.isPresent());
        assertEquals("ORD001", result.get().getOrderNumber());
    }

    @Test
    void getOrderById_shouldReturnEmptyWhenNotExists() {
        when(orderDAO.findById(1L)).thenReturn(Optional.empty());
        Optional<OrderEntity> result = orderService.getOrderById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void getOrderByOrderNumber_shouldReturnOrderWhenExists() {
        OrderEntity order = createTestOrder();
        when(orderDAO.findByOrderNumber("ORD001")).thenReturn(Optional.of(order));
        Optional<OrderEntity> result = orderService.getOrderByOrderNumber("ORD001");
        assertTrue(result.isPresent());
    }

    @Test
    void createOrder_shouldCalculateTotalAmountAndSetDefaults() {
        OrderEntity order = createTestOrder();
        order.setOrderItems(null);
        when(orderDAO.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        OrderEntity result = orderService.createOrder(order);
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(PaymentStatus.PENDING, result.getPaymentStatus());
    }

    @Test
    void createOrder_shouldReduceStockForEachItem() {
        OrderEntity order = createTestOrder();
        List<OrderItemEntity> items = createTestOrderItems(null);
        order.setOrderItems(items);
        when(orderDAO.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(orderItemDAO.save(any(OrderItemEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        OrderEntity result = orderService.createOrder(order);
        assertNotNull(result);
        verify(foodItemService).reduceStock(1L, 2);
        verify(foodItemService).reduceStock(2L, 1);
    }

    @Test
    void createOrder_shouldGenerateOrderNumber() {
        OrderEntity order = createTestOrder();
        order.setOrderNumber(null);
        when(orderDAO.save(any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        OrderEntity result = orderService.createOrder(order);
        assertNotNull(result.getOrderNumber());
        assertTrue(result.getOrderNumber().startsWith("ORD"));
    }

    @Test
    void updateOrder_shouldUpdateSuccessfully() {
        OrderEntity existing = createTestOrder();
        OrderEntity updates = new OrderEntity();
        updates.setDeliveryAddress("new");
        updates.setDeliveryInstructions("fast");
        when(orderDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(existing);
        OrderEntity result = orderService.updateOrder(1L, updates);
        assertNotNull(result);
        verify(orderDAO).update(any(OrderEntity.class));
    }

    @Test
    void updateOrder_shouldThrowWhenNotExists() {
        when(orderDAO.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrder(1L, new OrderEntity()));
    }

    @Test
    void deleteOrder_shouldDeleteItemsAndOrder() {
        when(orderDAO.existsById(1L)).thenReturn(true);
        orderService.deleteOrder(1L);
        verify(orderItemDAO).deleteByOrderId(1L);
        verify(orderDAO).deleteById(1L);
    }

    @Test
    void deleteOrder_shouldThrowWhenNotExists() {
        when(orderDAO.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,
                () -> orderService.deleteOrder(1L));
    }

    @Test
    void confirmOrder_shouldSucceedWhenPending() {
        OrderEntity order = createTestOrder();
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        boolean result = orderService.confirmOrder(1L);
        assertTrue(result);
        verify(orderDAO).updateStatus(1L, OrderStatus.CONFIRMED);
    }

    @Test
    void confirmOrder_shouldFailWhenNotPending() {
        OrderEntity order = createTestOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        boolean result = orderService.confirmOrder(1L);
        assertFalse(result);
    }

    @Test
    void startDelivery_shouldSucceedWhenConfirmed() {
        OrderEntity order = createTestOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        boolean result = orderService.startDelivery(1L);
        assertTrue(result);
        verify(orderDAO).updateStatus(1L, OrderStatus.DELIVERING);
    }

    @Test
    void completeDelivery_shouldSucceedWhenDelivering() {
        OrderEntity order = createTestOrder();
        order.setStatus(OrderStatus.DELIVERING);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        boolean result = orderService.completeDelivery(1L);
        assertTrue(result);
        verify(orderDAO).updateStatus(1L, OrderStatus.DELIVERED);
    }

    @Test
    void updateOrderStatus_validTransitionPendingToConfirmed() {
        OrderEntity order = createTestOrder();
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);
        OrderEntity result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void updateOrderStatus_invalidTransitionPendingToDelivering() {
        OrderEntity order = createTestOrder();
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.DELIVERING));
    }

    @Test
    void updateOrderStatus_shouldThrowWhenOrderNotFound() {
        when(orderDAO.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED));
    }

    @Test
    void updatePaymentStatus_validTransitionPendingToProcessing() {
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.PENDING);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);
        OrderEntity result = orderService.updatePaymentStatus(1L, PaymentStatus.PROCESSING);
        assertEquals(PaymentStatus.PROCESSING, result.getPaymentStatus());
    }

    @Test
    void updatePaymentStatus_validTransitionProcessingToSuccess() {
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.PROCESSING);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);
        OrderEntity result = orderService.updatePaymentStatus(1L, PaymentStatus.SUCCESS);
        assertEquals(PaymentStatus.SUCCESS, result.getPaymentStatus());
    }

    @Test
    void updatePaymentStatus_invalidTransitionPendingToSuccess() {
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.PENDING);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class,
                () -> orderService.updatePaymentStatus(1L, PaymentStatus.SUCCESS));
    }

    @Test
    void updatePaymentStatus_invalidTransitionSuccessToFailed() {
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class,
                () -> orderService.updatePaymentStatus(1L, PaymentStatus.FAILED));
    }

    @Test
    void updatePaymentStatus_refundShouldRestoreStockAndCancelOrder() {
        OrderEntity order = createTestOrder();
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        List<OrderItemEntity> items = createTestOrderItems(1L);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemDAO.findByOrderId(1L)).thenReturn(items);
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);
        OrderEntity result = orderService.updatePaymentStatus(1L, PaymentStatus.REFUNDED);
        assertEquals(PaymentStatus.REFUNDED, result.getPaymentStatus());
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(foodItemService).increaseStock(1L, 2);
        verify(foodItemService).increaseStock(2L, 1);
    }

    @Test
    void cancelOrder_shouldIncreaseStockForEachItem() {
        Long orderId = 1L;
        OrderEntity order = createTestOrder();
        order.setId(orderId);
        List<OrderItemEntity> items = createTestOrderItems(orderId);
        when(orderDAO.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemDAO.findByOrderId(orderId)).thenReturn(items);
        when(orderDAO.update(any(OrderEntity.class))).thenReturn(order);
        OrderEntity result = orderService.cancelOrder(orderId);
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(foodItemService).increaseStock(1L, 2);
        verify(foodItemService).increaseStock(2L, 1);
    }

    @Test
    void cancelOrder_shouldThrowWhenNotPending() {
        OrderEntity order = createTestOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderDAO.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class,
                () -> orderService.cancelOrder(1L));
    }

    @Test
    void getOrdersByFilters_shouldDelegateToDAO() {
        when(orderDAO.findByFilters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());
        orderService.getOrdersByFilters(1L, OrderStatus.PENDING, PaymentStatus.PENDING,
                null, null, null, org.springframework.data.domain.PageRequest.of(0, 20));
        verify(orderDAO).findByFilters(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void calculateTotalAmount_shouldReturnTotalFromItems() {
        when(orderDAO.findById(1L)).thenReturn(Optional.of(createTestOrder()));
        when(orderItemDAO.findByOrderId(1L)).thenReturn(createTestOrderItems(1L));
        BigDecimal result = orderService.calculateTotalAmount(1L);
        assertEquals(new BigDecimal("130.00"), result);
    }

    @Test
    void calculateTotalAmount_shouldThrowWhenOrderNotFound() {
        when(orderDAO.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> orderService.calculateTotalAmount(1L));
    }
}
