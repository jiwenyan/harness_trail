package com.example.dao.impl;

import com.example.dao.interfaces.FoodItemDAO;
import com.example.data.entity.FoodItemEntity;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodItemDAOImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private FoodItemDAO foodItemDAO;

    @BeforeEach
    void setUp() {
        foodItemDAO = new FoodItemDAOImpl(mongoTemplate);
    }

    @Test
    void findById_shouldReturnFoodItemWhenExists() {
        FoodItemEntity item = new FoodItemEntity();
        item.setId(1L);
        item.setName("Pizza");
        when(mongoTemplate.findById(1L, FoodItemEntity.class, "food_items")).thenReturn(item);

        Optional<FoodItemEntity> result = foodItemDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Pizza", result.get().getName());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        when(mongoTemplate.findById(1L, FoodItemEntity.class, "food_items")).thenReturn(null);

        Optional<FoodItemEntity> result = foodItemDAO.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void save_shouldSetIdAndTimestampsForNewItem() {
        FoodItemEntity item = new FoodItemEntity();
        item.setName("Burger");
        when(mongoTemplate.save(any(FoodItemEntity.class), eq("food_items"))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(Query.class), any(), any(), eq(org.bson.Document.class), eq("database_sequences")))
                .thenReturn(new org.bson.Document("seq", 1L));

        FoodItemEntity result = foodItemDAO.save(item);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void update_shouldSetUpdatedAt() {
        FoodItemEntity item = new FoodItemEntity();
        item.setId(1L);
        when(mongoTemplate.save(any(FoodItemEntity.class), eq("food_items"))).thenAnswer(invocation -> invocation.getArgument(0));

        FoodItemEntity result = foodItemDAO.update(item);

        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void deleteById_shouldRemoveItem() {
        foodItemDAO.deleteById(1L);

        verify(mongoTemplate).remove(any(Query.class), eq("food_items"));
    }

    @Test
    void findAll_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(Arrays.asList(new FoodItemEntity()));
        when(mongoTemplate.count(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(1L);

        Page<FoodItemEntity> result = foodItemDAO.findAll(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByCategory_shouldReturnList() {
        when(mongoTemplate.find(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(Arrays.asList(new FoodItemEntity()));

        List<FoodItemEntity> result = foodItemDAO.findByCategory("Pizza");

        assertEquals(1, result.size());
    }

    @Test
    void findByCategory_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(Arrays.asList(new FoodItemEntity()));
        when(mongoTemplate.count(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(1L);

        Page<FoodItemEntity> result = foodItemDAO.findByCategory("Pizza", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void search_shouldReturnMatchingItems() {
        when(mongoTemplate.find(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(Arrays.asList(new FoodItemEntity()));

        List<FoodItemEntity> result = foodItemDAO.search("pizza");

        assertEquals(1, result.size());
    }

    @Test
    void findAvailableItems_shouldReturnList() {
        when(mongoTemplate.find(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(Arrays.asList(new FoodItemEntity()));

        List<FoodItemEntity> result = foodItemDAO.findAvailableItems();

        assertEquals(1, result.size());
    }

    @Test
    void findAvailableItems_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(Arrays.asList(new FoodItemEntity()));
        when(mongoTemplate.count(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(1L);

        Page<FoodItemEntity> result = foodItemDAO.findAvailableItems(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateStock_shouldUpdateField() {
        foodItemDAO.updateStock(1L, 10);

        verify(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq(FoodItemEntity.class), eq("food_items"));
    }

    @Test
    void existsById_shouldReturnCorrectResult() {
        when(mongoTemplate.exists(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(true);

        assertTrue(foodItemDAO.existsById(1L));
    }

    @Test
    void findAllCategories_shouldReturnDistinctCategories() {
        when(mongoTemplate.findDistinct(any(Query.class), eq("category"), eq("food_items"), eq(String.class)))
                .thenReturn(Arrays.asList("Pizza", "Burger"));

        List<String> result = foodItemDAO.findAllCategories();

        assertEquals(2, result.size());
    }

    @Test
    void findByFilters_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(Arrays.asList(new FoodItemEntity()));
        when(mongoTemplate.count(any(Query.class), eq(FoodItemEntity.class), eq("food_items"))).thenReturn(1L);

        Page<FoodItemEntity> result = foodItemDAO.findByFilters("Pizza", true,
                BigDecimal.ONE, BigDecimal.TEN, null, pageable);

        assertEquals(1, result.getTotalElements());
    }
}
