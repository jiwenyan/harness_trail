package com.example.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建订单请求DTO
 */
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "配送地址不能为空")
    @Size(max = 500, message = "配送地址不能超过500个字符")
    private String deliveryAddress;

    @Size(max = 500, message = "配送备注不能超过500个字符")
    private String deliveryInstructions;

    @NotNull(message = "订单项不能为空")
    @Size(min = 1, message = "至少需要一个订单项")
    private List<CreateOrderItemRequest> orderItems;

    // Manual getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public List<CreateOrderItemRequest> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<CreateOrderItemRequest> orderItems) {
        this.orderItems = orderItems;
    }
}
