package com.example.data.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 */
@Document(collection = "food_items")
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemEntity {

    @Id
    private Long id;

    @NotBlank(message = "菜品名称不能为空")
    @Size(max = 100, message = "菜品名称不能超过100个字符")
    @Field
    private String name;

    @Size(max = 500, message = "菜品描述不能超过500个字符")
    @Field
    private String description;

    @NotBlank(message = "菜品分类不能为空")
    @Size(max = 50, message = "菜品分类不能超过50个字符")
    @Field
    private String category;

    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Field
    private BigDecimal price;

    @Field("image_url")
    private String imageUrl;

    @Field("is_available")
    private Boolean isAvailable = true;

    @Field("stock_quantity")
    private Integer stockQuantity = 0;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Manual getters and setters for Lombok compatibility
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
