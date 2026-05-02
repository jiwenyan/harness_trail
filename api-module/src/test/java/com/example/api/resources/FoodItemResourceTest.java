package com.example.api.resources;

import com.example.api.dto.request.CreateFoodItemRequest;
import com.example.data.entity.FoodItemEntity;
import com.example.service.interfaces.FoodItemService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodItemResourceTest {

    @Mock
    private FoodItemService foodItemService;

    private FoodItemResource foodItemResource;

    @BeforeEach
    void setUp() {
        foodItemResource = new FoodItemResource(foodItemService);
    }

    private FoodItemEntity createTestFoodItem() {
        FoodItemEntity item = new FoodItemEntity();
        item.setId(1L);
        item.setName("Pizza");
        item.setCategory("Italian");
        item.setPrice(new BigDecimal("12.99"));
        item.setIsAvailable(true);
        item.setStockQuantity(50);
        return item;
    }

    @Test
    void getFoodItemById_shouldReturn200WhenFound() {
        when(foodItemService.getFoodItemById(1L)).thenReturn(Optional.of(createTestFoodItem()));
        Response response = foodItemResource.getFoodItemById(1L);
        assertEquals(200, response.getStatus());
    }

    @Test
    void getFoodItemById_shouldReturn404WhenNotFound() {
        when(foodItemService.getFoodItemById(1L)).thenReturn(Optional.empty());
        Response response = foodItemResource.getFoodItemById(1L);
        assertEquals(404, response.getStatus());
    }

    @Test
    void createFoodItem_shouldReturn201() {
        CreateFoodItemRequest request = new CreateFoodItemRequest();
        request.setName("Burger");
        request.setCategory("American");
        request.setPrice(new BigDecimal("9.99"));
        request.setStockQuantity(20);
        when(foodItemService.createFoodItem(any(FoodItemEntity.class))).thenReturn(createTestFoodItem());
        Response response = foodItemResource.createFoodItem(request);
        assertEquals(201, response.getStatus());
    }

    @Test
    void updateFoodItem_shouldReturn200() {
        CreateFoodItemRequest request = new CreateFoodItemRequest();
        request.setName("Updated Pizza");
        request.setCategory("Italian");
        request.setPrice(new BigDecimal("15.99"));
        request.setStockQuantity(30);
        when(foodItemService.updateFoodItem(eq(1L), any(FoodItemEntity.class))).thenReturn(createTestFoodItem());
        Response response = foodItemResource.updateFoodItem(1L, request);
        assertEquals(200, response.getStatus());
    }

    @Test
    void deleteFoodItem_shouldReturn204() {
        Response response = foodItemResource.deleteFoodItem(1L);
        assertEquals(204, response.getStatus());
        verify(foodItemService).deleteFoodItem(1L);
    }

    @Test
    void getAllFoodItems_shouldReturn200() {
        Page<FoodItemEntity> page = new PageImpl<>(Arrays.asList(createTestFoodItem()));
        when(foodItemService.getAllFoodItems(any(PageRequest.class))).thenReturn(page);
        Response response = foodItemResource.getAllFoodItems(0, 20);
        assertEquals(200, response.getStatus());
    }

    @Test
    void getFoodItemsByCategory_shouldReturn200() {
        Page<FoodItemEntity> page = new PageImpl<>(Arrays.asList(createTestFoodItem()));
        when(foodItemService.getFoodItemsByCategory(eq("Italian"), any(PageRequest.class))).thenReturn(page);
        Response response = foodItemResource.getFoodItemsByCategory("Italian", 0, 20);
        assertEquals(200, response.getStatus());
    }

    @Test
    void searchFoodItems_shouldReturn200() {
        when(foodItemService.searchFoodItems("pizza")).thenReturn(Arrays.asList(createTestFoodItem()));
        Response response = foodItemResource.searchFoodItems("pizza");
        assertEquals(200, response.getStatus());
    }

    @Test
    void getAllCategories_shouldReturn200() {
        when(foodItemService.getAllCategories()).thenReturn(Arrays.asList("Italian", "Chinese"));
        Response response = foodItemResource.getAllCategories();
        assertEquals(200, response.getStatus());
    }

    @Test
    void updateStockQuantity_shouldReturn200() {
        when(foodItemService.updateStockQuantity(1L, 100)).thenReturn(createTestFoodItem());
        Response response = foodItemResource.updateStockQuantity(1L, 100);
        assertEquals(200, response.getStatus());
    }

    @Test
    void updateAvailability_shouldReturn200() {
        when(foodItemService.updateAvailability(1L, false)).thenReturn(createTestFoodItem());
        Response response = foodItemResource.updateAvailability(1L, false);
        assertEquals(200, response.getStatus());
    }

    @Test
    void checkStockAvailability_shouldReturn200() {
        when(foodItemService.checkStockAvailability(1L, 5)).thenReturn(true);
        Response response = foodItemResource.checkStockAvailability(1L, 5);
        assertEquals(200, response.getStatus());
    }
}
