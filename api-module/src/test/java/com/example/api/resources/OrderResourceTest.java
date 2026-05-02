package com.example.api.resources;

import com.example.api.dto.request.CreateOrderItemRequest;
import com.example.api.dto.request.CreateOrderRequest;
import com.example.api.dto.request.PaymentCallbackRequest;
import com.example.data.entity.OrderEntity;
import com.example.data.entity.OrderItemEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import com.example.service.interfaces.OrderService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResourceTest {

    @Mock
    private OrderService orderService;

    private OrderResource orderResource;

    @BeforeEach
    void setUp() {
        orderResource = new OrderResource(orderService);
    }

    private OrderEntity createTestOrder() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setOrderNumber("ORD001");
        order.setUserId(1L);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setDeliveryAddress("测试地址");
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        return order;
    }

    @Test
    void getOrderById_shouldReturn200WhenFound() {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(createTestOrder()));

        Response response = orderResource.getOrderById(1L);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getOrderById_shouldReturn404WhenNotFound() {
        when(orderService.getOrderById(1L)).thenReturn(Optional.empty());

        Response response = orderResource.getOrderById(1L);

        assertEquals(404, response.getStatus());
    }

    @Test
    void getOrderByOrderNumber_shouldReturn200WhenFound() {
        when(orderService.getOrderByOrderNumber("ORD001")).thenReturn(Optional.of(createTestOrder()));

        Response response = orderResource.getOrderByOrderNumber("ORD001");

        assertEquals(200, response.getStatus());
    }

    @Test
    void createOrder_shouldReturn201() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setDeliveryAddress("地址");
        CreateOrderItemRequest item = new CreateOrderItemRequest();
        item.setFoodItemId(1L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        request.setOrderItems(Collections.singletonList(item));

        when(orderService.createOrder(any(OrderEntity.class))).thenReturn(createTestOrder());

        Response response = orderResource.createOrder(request);

        assertEquals(201, response.getStatus());
    }

    @Test
    void getAllOrders_shouldReturn200() {
        Page<OrderEntity> page = new PageImpl<>(Arrays.asList(createTestOrder()));
        when(orderService.getAllOrders(any(PageRequest.class))).thenReturn(page);

        Response response = orderResource.getAllOrders(0, 20);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getOrdersByUserId_shouldReturn200() {
        Page<OrderEntity> page = new PageImpl<>(Arrays.asList(createTestOrder()));
        when(orderService.getOrdersByUserId(eq(1L), any(PageRequest.class))).thenReturn(page);

        Response response = orderResource.getOrdersByUserId(1L, 0, 20);

        assertEquals(200, response.getStatus());
    }

    @Test
    void updateOrder_shouldReturn200() {
        com.example.api.dto.response.OrderResponse updateRequest = new com.example.api.dto.response.OrderResponse();
        updateRequest.setDeliveryAddress("新地址");
        updateRequest.setStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrder(eq(1L), any(OrderEntity.class))).thenReturn(createTestOrder());

        Response response = orderResource.updateOrder(1L, updateRequest);

        assertEquals(200, response.getStatus());
    }

    @Test
    void deleteOrder_shouldReturn204() {
        Response response = orderResource.deleteOrder(1L);

        assertEquals(204, response.getStatus());
        verify(orderService).deleteOrder(1L);
    }

    @Test
    void updateOrderStatus_shouldReturn200() {
        when(orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)).thenReturn(createTestOrder());

        Response response = orderResource.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertEquals(200, response.getStatus());
    }

    @Test
    void updatePaymentStatus_shouldReturn200() {
        when(orderService.updatePaymentStatus(1L, PaymentStatus.SUCCESS)).thenReturn(createTestOrder());

        Response response = orderResource.updatePaymentStatus(1L, PaymentStatus.SUCCESS);

        assertEquals(200, response.getStatus());
    }

    @Test
    void paymentCallback_shouldReturn200() {
        PaymentCallbackRequest callback = new PaymentCallbackRequest();
        callback.setPaymentStatus("SUCCESS");
        callback.setTransactionId("TXN123");
        callback.setPaymentMethod("CREDIT_CARD");

        when(orderService.getOrderById(1L)).thenReturn(Optional.of(createTestOrder()));
        when(orderService.updatePaymentStatus(eq(1L), eq(PaymentStatus.SUCCESS))).thenReturn(createTestOrder());

        Response response = orderResource.paymentCallback(1L, callback);

        assertEquals(200, response.getStatus());
    }

    @Test
    void cancelOrder_shouldReturn200() {
        when(orderService.cancelOrder(1L)).thenReturn(createTestOrder());

        Response response = orderResource.cancelOrder(1L);

        assertEquals(200, response.getStatus());
    }

    @Test
    void calculateTotalAmount_shouldReturn200() {
        when(orderService.calculateTotalAmount(1L)).thenReturn(new BigDecimal("100.00"));

        Response response = orderResource.calculateTotalAmount(1L);

        assertEquals(200, response.getStatus());
    }
}
