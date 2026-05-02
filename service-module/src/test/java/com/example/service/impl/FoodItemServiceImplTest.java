package com.example.service.impl;

import com.example.dao.interfaces.FoodItemDAO;
import com.example.data.entity.FoodItemEntity;
import com.example.service.interfaces.FoodItemService;
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
class FoodItemServiceImplTest {

    @Mock
    private FoodItemDAO foodItemDAO;

    private FoodItemService foodItemService;

    @BeforeEach
    void setUp() {
        foodItemService = new FoodItemServiceImpl(foodItemDAO);
    }

    private FoodItemEntity createTestFoodItem() {
        FoodItemEntity item = new FoodItemEntity();
        item.setId(1L);
        item.setName("Pizza");
        item.setDescription("Delicious pizza");
        item.setCategory("Italian");
        item.setPrice(new BigDecimal("12.99"));
        item.setIsAvailable(true);
        item.setStockQuantity(50);
        return item;
    }

    @Test
    void getFoodItemById_shouldReturnItemWhenExists() {
        FoodItemEntity item = createTestFoodItem();
        when(foodItemDAO.findById(1L)).thenReturn(Optional.of(item));

        Optional<FoodItemEntity> result = foodItemService.getFoodItemById(1L);

        assertTrue(result.isPresent());
        assertEquals("Pizza", result.get().getName());
    }

    @Test
    void getFoodItemById_shouldReturnEmptyWhenNotExists() {
        when(foodItemDAO.findById(1L)).thenReturn(Optional.empty());

        Optional<FoodItemEntity> result = foodItemService.getFoodItemById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void createFoodItem_shouldSaveSuccessfully() {
        FoodItemEntity item = createTestFoodItem();
        when(foodItemDAO.save(any(FoodItemEntity.class))).thenReturn(item);

        FoodItemEntity result = foodItemService.createFoodItem(item);

        assertNotNull(result);
        verify(foodItemDAO).save(item);
    }

    @Test
    void createFoodItem_shouldThrowWhenNameIsEmpty() {
        FoodItemEntity item = createTestFoodItem();
        item.setName(null);

        assertThrows(IllegalArgumentException.class, () -> foodItemService.createFoodItem(item));
    }

    @Test
    void createFoodItem_shouldThrowWhenCategoryIsEmpty() {
        FoodItemEntity item = createTestFoodItem();
        item.setCategory(null);

        assertThrows(IllegalArgumentException.class, () -> foodItemService.createFoodItem(item));
    }

    @Test
    void createFoodItem_shouldThrowWhenPriceIsInvalid() {
        FoodItemEntity item = createTestFoodItem();
        item.setPrice(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> foodItemService.createFoodItem(item));
    }

    @Test
    void updateFoodItem_shouldUpdateSuccessfully() {
        FoodItemEntity existing = createTestFoodItem();
        FoodItemEntity updates = new FoodItemEntity();
        updates.setName("New Pizza");
        updates.setDescription("Updated");
        updates.setCategory("Italian");
        updates.setPrice(new BigDecimal("15.99"));
        updates.setIsAvailable(true);
        updates.setStockQuantity(30);

        when(foodItemDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(foodItemDAO.update(any(FoodItemEntity.class))).thenReturn(existing);

        FoodItemEntity result = foodItemService.updateFoodItem(1L, updates);

        assertNotNull(result);
        verify(foodItemDAO).update(any(FoodItemEntity.class));
    }

    @Test
    void updateFoodItem_shouldThrowWhenNotExists() {
        when(foodItemDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> foodItemService.updateFoodItem(1L, new FoodItemEntity()));
    }

    @Test
    void deleteFoodItem_shouldDeleteSuccessfully() {
        when(foodItemDAO.existsById(1L)).thenReturn(true);

        foodItemService.deleteFoodItem(1L);

        verify(foodItemDAO).deleteById(1L);
    }

    @Test
    void deleteFoodItem_shouldThrowWhenNotExists() {
        when(foodItemDAO.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> foodItemService.deleteFoodItem(1L));
    }

    @Test
    void reduceStock_shouldReduceStockWhenSufficient() {
        Long foodItemId = 1L;
        Integer quantity = 3;
        FoodItemEntity foodItem = new FoodItemEntity();
        foodItem.setId(foodItemId);
        foodItem.setStockQuantity(10);
        when(foodItemDAO.findById(foodItemId)).thenReturn(Optional.of(foodItem));

        boolean result = foodItemService.reduceStock(foodItemId, quantity);

        assertTrue(result);
        verify(foodItemDAO).updateStock(foodItemId, 7);
    }

    @Test
    void reduceStock_shouldReturnFalseWhenInsufficientStock() {
        Long foodItemId = 1L;
        FoodItemEntity foodItem = new FoodItemEntity();
        foodItem.setId(foodItemId);
        foodItem.setStockQuantity(2);
        when(foodItemDAO.findById(foodItemId)).thenReturn(Optional.of(foodItem));

        boolean result = foodItemService.reduceStock(foodItemId, 5);

        assertFalse(result);
        verify(foodItemDAO, never()).updateStock(anyLong(), anyInt());
    }

    @Test
    void reduceStock_shouldReturnFalseWhenItemNotFound() {
        when(foodItemDAO.findById(1L)).thenReturn(Optional.empty());

        boolean result = foodItemService.reduceStock(1L, 5);

        assertFalse(result);
    }

    @Test
    void increaseStock_shouldIncreaseStock() {
        Long foodItemId = 1L;
        Integer quantity = 3;
        FoodItemEntity foodItem = new FoodItemEntity();
        foodItem.setId(foodItemId);
        foodItem.setStockQuantity(5);
        when(foodItemDAO.findById(foodItemId)).thenReturn(Optional.of(foodItem));

        boolean result = foodItemService.increaseStock(foodItemId, quantity);

        assertTrue(result);
        verify(foodItemDAO).updateStock(foodItemId, 8);
    }

    @Test
    void increaseStock_shouldReturnFalseWhenItemNotFound() {
        when(foodItemDAO.findById(1L)).thenReturn(Optional.empty());

        boolean result = foodItemService.increaseStock(1L, 5);

        assertFalse(result);
    }

    @Test
    void checkStockAvailability_shouldReturnTrueWhenAvailable() {
        FoodItemEntity item = createTestFoodItem();
        item.setStockQuantity(10);
        when(foodItemDAO.findById(1L)).thenReturn(Optional.of(item));

        boolean result = foodItemService.checkStockAvailability(1L, 5);

        assertTrue(result);
    }

    @Test
    void checkStockAvailability_shouldReturnFalseWhenInsufficientStock() {
        FoodItemEntity item = createTestFoodItem();
        item.setStockQuantity(2);
        when(foodItemDAO.findById(1L)).thenReturn(Optional.of(item));

        boolean result = foodItemService.checkStockAvailability(1L, 5);

        assertFalse(result);
    }

    @Test
    void checkStockAvailability_shouldReturnFalseWhenItemNotAvailable() {
        FoodItemEntity item = createTestFoodItem();
        item.setIsAvailable(false);
        item.setStockQuantity(10);
        when(foodItemDAO.findById(1L)).thenReturn(Optional.of(item));

        boolean result = foodItemService.checkStockAvailability(1L, 5);

        assertFalse(result);
    }

    @Test
    void checkStockAvailability_shouldReturnFalseWhenItemNotFound() {
        when(foodItemDAO.findById(1L)).thenReturn(Optional.empty());

        boolean result = foodItemService.checkStockAvailability(1L, 5);

        assertFalse(result);
    }

    @Test
    void updateStockQuantity_shouldUpdateSuccessfully() {
        FoodItemEntity item = createTestFoodItem();
        when(foodItemDAO.findById(1L)).thenReturn(Optional.of(item));
        when(foodItemDAO.save(any(FoodItemEntity.class))).thenReturn(item);

        FoodItemEntity result = foodItemService.updateStockQuantity(1L, 100);

        assertEquals(100, result.getStockQuantity());
    }

    @Test
    void updateAvailability_shouldUpdateSuccessfully() {
        FoodItemEntity item = createTestFoodItem();
        when(foodItemDAO.findById(1L)).thenReturn(Optional.of(item));
        when(foodItemDAO.save(any(FoodItemEntity.class))).thenReturn(item);

        FoodItemEntity result = foodItemService.updateAvailability(1L, false);

        assertFalse(result.getIsAvailable());
    }

    @Test
    void getAllCategories_shouldReturnCategories() {
        when(foodItemDAO.findAllCategories()).thenReturn(Arrays.asList("Italian", "Chinese"));

        List<String> result = foodItemService.getAllCategories();

        assertEquals(2, result.size());
        assertTrue(result.contains("Italian"));
    }
}
