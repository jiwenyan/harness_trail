package com.example.service.impl;

import com.example.dao.interfaces.FoodItemDAO;
import com.example.data.entity.FoodItemEntity;
import com.example.service.interfaces.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 菜品服务实现
 */
@Service
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemDAO foodItemDAO;

    @Autowired
    public FoodItemServiceImpl(FoodItemDAO foodItemDAO) {
        this.foodItemDAO = foodItemDAO;
    }

    @Override
    public Optional<FoodItemEntity> getFoodItemById(Long id) {
        return foodItemDAO.findById(id);
    }

    @Override
    public FoodItemEntity createFoodItem(FoodItemEntity foodItem) {
        // 验证菜品数据
        validateFoodItemData(foodItem);

        return foodItemDAO.save(foodItem);
    }

    @Override
    public FoodItemEntity updateFoodItem(Long id, FoodItemEntity foodItem) {
        // 验证菜品是否存在
        FoodItemEntity existingFoodItem = foodItemDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("菜品不存在"));

        // 更新菜品信息
        existingFoodItem.setName(foodItem.getName());
        existingFoodItem.setDescription(foodItem.getDescription());
        existingFoodItem.setCategory(foodItem.getCategory());
        existingFoodItem.setPrice(foodItem.getPrice());
        existingFoodItem.setImageUrl(foodItem.getImageUrl());
        existingFoodItem.setIsAvailable(foodItem.getIsAvailable());
        existingFoodItem.setStockQuantity(foodItem.getStockQuantity());

        return foodItemDAO.update(existingFoodItem);
    }

    @Override
    public void deleteFoodItem(Long id) {
        // 验证菜品是否存在
        if (!foodItemDAO.existsById(id)) {
            throw new IllegalArgumentException("菜品不存在");
        }

        foodItemDAO.deleteById(id);
    }

    @Override
    public Page<FoodItemEntity> getAllFoodItems(Pageable pageable) {
        return foodItemDAO.findAll(pageable);
    }

    @Override
    public List<FoodItemEntity> getFoodItemsByCategory(String category) {
        return foodItemDAO.findByCategory(category);
    }

    @Override
    public Page<FoodItemEntity> getFoodItemsByCategory(String category, Pageable pageable) {
        return foodItemDAO.findByCategory(category, pageable);
    }

    @Override
    public List<FoodItemEntity> searchFoodItems(String keyword) {
        return foodItemDAO.search(keyword);
    }

    @Override
    public List<FoodItemEntity> getAvailableFoodItems() {
        return foodItemDAO.findAvailableItems();
    }

    @Override
    public Page<FoodItemEntity> getAvailableFoodItems(Pageable pageable) {
        return foodItemDAO.findAvailableItems(pageable);
    }

    @Override
    public void updateStock(Long foodItemId, Integer quantity) {
        // 验证菜品是否存在
        if (!foodItemDAO.existsById(foodItemId)) {
            throw new IllegalArgumentException("菜品不存在");
        }

        foodItemDAO.updateStock(foodItemId, quantity);
    }

    @Override
    public boolean checkStockAvailability(Long foodItemId, Integer requiredQuantity) {
        Optional<FoodItemEntity> foodItem = foodItemDAO.findById(foodItemId);
        return foodItem.isPresent() &&
               foodItem.get().getIsAvailable() &&
               foodItem.get().getStockQuantity() >= requiredQuantity;
    }

    @Override
    public boolean reduceStock(Long foodItemId, Integer quantity) {
        Optional<FoodItemEntity> foodItem = foodItemDAO.findById(foodItemId);
        if (foodItem.isPresent() && foodItem.get().getStockQuantity() >= quantity) {
            int newStock = foodItem.get().getStockQuantity() - quantity;
            foodItemDAO.updateStock(foodItemId, newStock);
            return true;
        }
        return false;
    }

    @Override
    public boolean increaseStock(Long foodItemId, Integer quantity) {
        Optional<FoodItemEntity> foodItem = foodItemDAO.findById(foodItemId);
        if (foodItem.isPresent()) {
            int newStock = foodItem.get().getStockQuantity() + quantity;
            foodItemDAO.updateStock(foodItemId, newStock);
            return true;
        }
        return false;
    }

    /**
     * 验证菜品数据
     */
    private void validateFoodItemData(FoodItemEntity foodItem) {
        if (foodItem.getName() == null || foodItem.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("菜品名称不能为空");
        }
        if (foodItem.getCategory() == null || foodItem.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("菜品分类不能为空");
        }
        if (foodItem.getPrice() == null || foodItem.getPrice().doubleValue() <= 0) {
            throw new IllegalArgumentException("菜品价格必须大于0");
        }
        if (foodItem.getStockQuantity() == null || foodItem.getStockQuantity() < 0) {
            throw new IllegalArgumentException("库存数量不能为负数");
        }
    }

    @Override
    public List<String> getAllCategories() {
        return foodItemDAO.findAllCategories();
    }

    @Override
    public FoodItemEntity updateStockQuantity(Long foodItemId, Integer quantity) {
        FoodItemEntity foodItem = foodItemDAO.findById(foodItemId)
                .orElseThrow(() -> new IllegalArgumentException("菜品不存在"));

        if (quantity < 0) {
            throw new IllegalArgumentException("库存数量不能为负数");
        }

        foodItem.setStockQuantity(quantity);
        return foodItemDAO.save(foodItem);
    }

    @Override
    public FoodItemEntity updateAvailability(Long foodItemId, Boolean available) {
        FoodItemEntity foodItem = foodItemDAO.findById(foodItemId)
                .orElseThrow(() -> new IllegalArgumentException("菜品不存在"));

        foodItem.setIsAvailable(available);
        return foodItemDAO.save(foodItem);
    }

}