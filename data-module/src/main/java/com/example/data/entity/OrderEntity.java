package com.example.data.entity;

import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 */
@Document(collection = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    private Long id;

    @Field("order_number")
    private String orderNumber;

    @NotNull(message = "用户ID不能为空")
    @Field("user_id")
    private Long userId;

    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
    @Field("total_amount")
    private BigDecimal totalAmount;

    @NotNull(message = "配送地址不能为空")
    @Field("delivery_address")
    private String deliveryAddress;

    @Field("delivery_instructions")
    private String deliveryInstructions;

    @NotNull(message = "订单状态不能为空")
    @Field
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull(message = "支付状态不能为空")
    @Field("payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Field("payment_method")
    private String paymentMethod;

    @Field("payment_transaction_id")
    private String paymentTransactionId;

    @Field("estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Field("actual_delivery_time")
    private LocalDateTime actualDeliveryTime;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    private List<OrderItemEntity> orderItems = new ArrayList<>();

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

    public List<OrderItemEntity> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }
}
