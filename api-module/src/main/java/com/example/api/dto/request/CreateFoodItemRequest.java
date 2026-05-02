package com.example.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 创建菜品请求DTO
 */
@NoArgsConstructor
@AllArgsConstructor
public class CreateFoodItemRequest {

    @NotBlank(message = "菜品名称不能为空")
    @Size(max = 100, message = "菜品名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "菜品描述不能超过500个字符")
    private String description;

    @NotBlank(message = "菜品分类不能为空")
    @Size(max = 50, message = "菜品分类不能超过50个字符")
    private String category;

    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    private String imageUrl;
    private Boolean isAvailable = true;
    private Integer stockQuantity = 0;

    // Manual getters and setters for Lombok compatibility
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
}
