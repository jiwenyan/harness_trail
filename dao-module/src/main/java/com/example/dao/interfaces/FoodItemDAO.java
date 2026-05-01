package com.example.dao.interfaces;

import com.example.data.entity.FoodItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 菜品数据访问接口
 */
public interface FoodItemDAO {

    /**
     * 根据ID查找菜品
     */
    Optional<FoodItemEntity> findById(Long id);

    /**
     * 保存菜品
     */
    FoodItemEntity save(FoodItemEntity foodItem);

    /**
     * 更新菜品
     */
    FoodItemEntity update(FoodItemEntity foodItem);

    /**
     * 删除菜品
     */
    void deleteById(Long id);

    /**
     * 获取所有菜品（分页）
     */
    Page<FoodItemEntity> findAll(Pageable pageable);

    /**
     * 根据分类获取菜品
     */
    List<FoodItemEntity> findByCategory(String category);

    /**
     * 根据分类获取菜品（分页）
     */
    Page<FoodItemEntity> findByCategory(String category, Pageable pageable);

    /**
     * 搜索菜品（按名称或描述）
     */
    List<FoodItemEntity> search(String keyword);

    /**
     * 获取可用菜品
     */
    List<FoodItemEntity> findAvailableItems();

    /**
     * 获取可用菜品（分页）
     */
    Page<FoodItemEntity> findAvailableItems(Pageable pageable);

    /**
     * 更新菜品库存
     */
    void updateStock(Long foodItemId, Integer quantity);

    /**
     * 检查菜品是否存在
     */
    boolean existsById(Long id);

    /**
     * 获取所有分类
     */
    List<String> findAllCategories();
}