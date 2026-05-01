package com.example.service.interfaces;

import com.example.data.entity.FoodItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 菜品服务接口
 */
public interface FoodItemService {

    /**
     * 根据ID获取菜品
     */
    Optional<FoodItemEntity> getFoodItemById(Long id);

    /**
     * 创建菜品
     */
    FoodItemEntity createFoodItem(FoodItemEntity foodItem);

    /**
     * 更新菜品
     */
    FoodItemEntity updateFoodItem(Long id, FoodItemEntity foodItem);

    /**
     * 删除菜品
     */
    void deleteFoodItem(Long id);

    /**
     * 获取所有菜品（分页）
     */
    Page<FoodItemEntity> getAllFoodItems(Pageable pageable);

    /**
     * 根据分类获取菜品
     */
    List<FoodItemEntity> getFoodItemsByCategory(String category);

    /**
     * 根据分类获取菜品（分页）
     */
    Page<FoodItemEntity> getFoodItemsByCategory(String category, Pageable pageable);

    /**
     * 搜索菜品
     */
    List<FoodItemEntity> searchFoodItems(String keyword);

    /**
     * 获取可用菜品
     */
    List<FoodItemEntity> getAvailableFoodItems();

    /**
     * 获取可用菜品（分页）
     */
    Page<FoodItemEntity> getAvailableFoodItems(Pageable pageable);

    /**
     * 获取所有分类
     */
    List<String> getAllCategories();

    /**
     * 更新菜品库存
     */
    FoodItemEntity updateStockQuantity(Long foodItemId, Integer quantity);

    /**
     * 更新菜品可用状态
     */
    FoodItemEntity updateAvailability(Long foodItemId, Boolean available);

    /**
     * 更新菜品库存（旧方法，兼容性）
     */
    void updateStock(Long foodItemId, Integer quantity);

    /**
     * 检查菜品库存是否充足
     */
    boolean checkStockAvailability(Long foodItemId, Integer requiredQuantity);

    /**
     * 减少菜品库存
     */
    boolean reduceStock(Long foodItemId, Integer quantity);

    /**
     * 增加菜品库存
     */
    boolean increaseStock(Long foodItemId, Integer quantity);
}