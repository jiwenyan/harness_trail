package com.example.api.dto.response;

import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 */
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderNumber;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
    private BigDecimal totalAmount;

    @NotNull(message = "配送地址不能为空")
    private String deliveryAddress;

    private String deliveryInstructions;

    @NotNull(message = "订单状态不能为空")
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull(message = "支付状态不能为空")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String paymentMethod;
    private String paymentTransactionId;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<OrderItemResponse> orderItems;

    // Manual getters and setters for Lombok compatibility
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public LocalDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(LocalDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public LocalDateTime getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(LocalDateTime actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
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

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemResponse> orderItems) {
        this.orderItems = orderItems;
    }
}
