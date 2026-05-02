package com.example.service.impl;

import com.example.dao.interfaces.FoodItemDAO;
import com.example.data.entity.FoodItemEntity;
import com.example.service.interfaces.FoodItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void reduceStock_shouldReduceStockWhenSufficient() {
        // Arrange
        Long foodItemId = 1L;
        Integer quantity = 3;
        FoodItemEntity foodItem = new FoodItemEntity();
        foodItem.setId(foodItemId);
        foodItem.setStockQuantity(10);
        when(foodItemDAO.findById(foodItemId)).thenReturn(Optional.of(foodItem));

        // Act
        boolean result = foodItemService.reduceStock(foodItemId, quantity);

        // Assert
        assertTrue(result);
        verify(foodItemDAO).updateStock(foodItemId, 7);
    }

    @Test
    void increaseStock_shouldIncreaseStock() {
        // Arrange
        Long foodItemId = 1L;
        Integer quantity = 3;
        FoodItemEntity foodItem = new FoodItemEntity();
        foodItem.setId(foodItemId);
        foodItem.setStockQuantity(5);
        when(foodItemDAO.findById(foodItemId)).thenReturn(Optional.of(foodItem));

        // Act
        boolean result = foodItemService.increaseStock(foodItemId, quantity);

        // Assert
        assertTrue(result);
        verify(foodItemDAO).updateStock(foodItemId, 8);
    }
}
