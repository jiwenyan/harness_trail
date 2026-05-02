package com.example.dao.impl;

import com.example.dao.interfaces.OrderItemDAO;
import com.example.data.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemDAOImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private OrderItemDAO orderItemDAO;

    @BeforeEach
    void setUp() {
        orderItemDAO = new OrderItemDAOImpl(mongoTemplate);
    }

    @Test
    void findById_shouldReturnItemWhenExists() {
        OrderItemEntity item = new OrderItemEntity();
        item.setId(1L);
        when(mongoTemplate.findById(1L, OrderItemEntity.class, "order_items")).thenReturn(item);

        Optional<OrderItemEntity> result = orderItemDAO.findById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        when(mongoTemplate.findById(1L, OrderItemEntity.class, "order_items")).thenReturn(null);

        Optional<OrderItemEntity> result = orderItemDAO.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void save_shouldSetIdForNewItem() {
        OrderItemEntity item = new OrderItemEntity();
        when(mongoTemplate.save(any(OrderItemEntity.class), eq("order_items"))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(Query.class), any(), any(), eq(org.bson.Document.class), eq("database_sequences")))
                .thenReturn(new org.bson.Document("seq", 1L));

        OrderItemEntity result = orderItemDAO.save(item);

        assertNotNull(result.getId());
    }

    @Test
    void save_shouldPreserveIdForExistingItem() {
        OrderItemEntity item = new OrderItemEntity();
        item.setId(5L);
        when(mongoTemplate.save(any(OrderItemEntity.class), eq("order_items"))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderItemEntity result = orderItemDAO.save(item);

        assertEquals(5L, result.getId());
    }

    @Test
    void saveAll_shouldSaveAllItems() {
        OrderItemEntity item1 = new OrderItemEntity();
        OrderItemEntity item2 = new OrderItemEntity();
        when(mongoTemplate.save(any(OrderItemEntity.class), eq("order_items"))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(Query.class), any(), any(), eq(org.bson.Document.class), eq("database_sequences")))
                .thenReturn(new org.bson.Document("seq", 1L));

        List<OrderItemEntity> result = orderItemDAO.saveAll(Arrays.asList(item1, item2));

        assertEquals(2, result.size());
        verify(mongoTemplate, times(2)).save(any(OrderItemEntity.class), eq("order_items"));
    }

    @Test
    void update_shouldSaveItem() {
        OrderItemEntity item = new OrderItemEntity();
        item.setId(1L);
        when(mongoTemplate.save(any(OrderItemEntity.class), eq("order_items"))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderItemEntity result = orderItemDAO.update(item);

        assertNotNull(result);
    }

    @Test
    void deleteById_shouldRemoveItem() {
        orderItemDAO.deleteById(1L);

        verify(mongoTemplate).remove(any(Query.class), eq("order_items"));
    }

    @Test
    void findByOrderId_shouldReturnItems() {
        when(mongoTemplate.find(any(Query.class), eq(OrderItemEntity.class), eq("order_items"))).thenReturn(Arrays.asList(new OrderItemEntity()));

        List<OrderItemEntity> result = orderItemDAO.findByOrderId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void findByFoodItemId_shouldReturnItems() {
        when(mongoTemplate.find(any(Query.class), eq(OrderItemEntity.class), eq("order_items"))).thenReturn(Arrays.asList(new OrderItemEntity()));

        List<OrderItemEntity> result = orderItemDAO.findByFoodItemId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void deleteByOrderId_shouldRemoveItems() {
        orderItemDAO.deleteByOrderId(1L);

        verify(mongoTemplate).remove(any(Query.class), eq("order_items"));
    }

    @Test
    void existsById_shouldReturnCorrectResult() {
        when(mongoTemplate.exists(any(Query.class), eq("order_items"))).thenReturn(true);

        assertTrue(orderItemDAO.existsById(1L));
    }
}
