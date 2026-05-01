package com.example.data.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

/**
 * 订单项实体类
 */
@Document(collection = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    @Id
    private Long id;

    @NotNull(message = "订单ID不能为空")
    @Field("order_id")
    private Long orderId;

    @NotNull(message = "菜品ID不能为空")
    @Field("food_item_id")
    private Long foodItemId;

    @NotNull(message = "菜品数量不能为空")
    @Min(value = 1, message = "菜品数量必须大于0")
    @Field
    private Integer quantity;

    @NotNull(message = "菜品单价不能为空")
    @DecimalMin(value = "0.01", message = "菜品单价必须大于0")
    @Field("unit_price")
    private BigDecimal unitPrice;

    @NotNull(message = "菜品总价不能为空")
    @DecimalMin(value = "0.01", message = "菜品总价必须大于0")
    @Field("total_price")
    private BigDecimal totalPrice;

    @Field("special_instructions")
    private String specialInstructions;

    // Manual getters and setters for Lombok compatibility
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}
