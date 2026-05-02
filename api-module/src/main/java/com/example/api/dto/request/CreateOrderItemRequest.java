package com.example.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 创建订单项请求DTO
 */
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemRequest {

    @NotNull(message = "菜品ID不能为空")
    private Long foodItemId;

    @NotNull(message = "菜品数量不能为空")
    @Min(value = 1, message = "菜品数量必须大于0")
    private Integer quantity;

    @NotNull(message = "菜品单价不能为空")
    @DecimalMin(value = "0.01", message = "菜品单价必须大于0")
    private BigDecimal unitPrice;

    private String specialInstructions;

    // Manual getters and setters
    public Long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}
