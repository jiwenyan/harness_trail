package com.example.dao.impl;

import com.example.dao.interfaces.OrderDAO;
import com.example.data.entity.OrderEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDAOImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private OrderDAO orderDAO;

    @BeforeEach
    void setUp() {
        orderDAO = new OrderDAOImpl(mongoTemplate);
    }

    @Test
    void findById_shouldReturnOrderWhenExists() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        when(mongoTemplate.findById(1L, OrderEntity.class, "orders")).thenReturn(order);

        Optional<OrderEntity> result = orderDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        when(mongoTemplate.findById(1L, OrderEntity.class, "orders")).thenReturn(null);

        Optional<OrderEntity> result = orderDAO.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByOrderNumber_shouldReturnOrder() {
        OrderEntity order = new OrderEntity();
        order.setOrderNumber("ORD001");
        when(mongoTemplate.findOne(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(order);

        Optional<OrderEntity> result = orderDAO.findByOrderNumber("ORD001");

        assertTrue(result.isPresent());
        assertEquals("ORD001", result.get().getOrderNumber());
    }

    @Test
    void save_shouldSetIdAndTimestampsForNewOrder() {
        OrderEntity order = new OrderEntity();
        when(mongoTemplate.save(any(OrderEntity.class), eq("orders"))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(Query.class), any(), any(), eq(org.bson.Document.class), eq("database_sequences")))
                .thenReturn(new org.bson.Document("seq", 1L));

        OrderEntity result = orderDAO.save(order);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void update_shouldSetUpdatedAt() {
        OrderEntity order = new OrderEntity();
        order.setId(1L);
        when(mongoTemplate.save(any(OrderEntity.class), eq("orders"))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEntity result = orderDAO.update(order);

        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void deleteById_shouldRemoveOrder() {
        orderDAO.deleteById(1L);

        verify(mongoTemplate).remove(any(Query.class), eq("orders"));
    }

    @Test
    void findByUserId_shouldReturnList() {
        OrderEntity order = new OrderEntity();
        order.setUserId(1L);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(order));

        List<OrderEntity> result = orderDAO.findByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void findByUserId_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));
        when(mongoTemplate.count(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(1L);

        Page<OrderEntity> result = orderDAO.findByUserId(1L, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByStatus_shouldReturnList() {
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));

        List<OrderEntity> result = orderDAO.findByStatus(OrderStatus.PENDING);

        assertEquals(1, result.size());
    }

    @Test
    void findByStatus_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));
        when(mongoTemplate.count(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(1L);

        Page<OrderEntity> result = orderDAO.findByStatus(OrderStatus.PENDING, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByPaymentStatus_shouldReturnList() {
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));

        List<OrderEntity> result = orderDAO.findByPaymentStatus(PaymentStatus.PENDING);

        assertEquals(1, result.size());
    }

    @Test
    void findByPaymentStatus_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));
        when(mongoTemplate.count(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(1L);

        Page<OrderEntity> result = orderDAO.findByPaymentStatus(PaymentStatus.PENDING, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByCreatedAtBetween_shouldReturnList() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));

        List<OrderEntity> result = orderDAO.findByCreatedAtBetween(start, end);

        assertEquals(1, result.size());
    }

    @Test
    void findByCreatedAtBetween_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));
        when(mongoTemplate.count(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(1L);

        Page<OrderEntity> result = orderDAO.findByCreatedAtBetween(start, end, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findAll_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));
        when(mongoTemplate.count(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(1L);

        Page<OrderEntity> result = orderDAO.findAll(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateStatus_shouldUpdateField() {
        orderDAO.updateStatus(1L, OrderStatus.CONFIRMED);

        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(OrderEntity.class), eq("orders"));
    }

    @Test
    void updatePaymentStatus_shouldUpdateField() {
        orderDAO.updatePaymentStatus(1L, PaymentStatus.SUCCESS);

        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(OrderEntity.class), eq("orders"));
    }

    @Test
    void existsById_shouldReturnTrueWhenExists() {
        when(mongoTemplate.exists(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(true);

        assertTrue(orderDAO.existsById(1L));
    }

    @Test
    void findByFilters_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(Arrays.asList(new OrderEntity()));
        when(mongoTemplate.count(any(Query.class), eq(OrderEntity.class), eq("orders"))).thenReturn(1L);

        Page<OrderEntity> result = orderDAO.findByFilters(1L, OrderStatus.PENDING, PaymentStatus.PENDING,
                null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }
}
