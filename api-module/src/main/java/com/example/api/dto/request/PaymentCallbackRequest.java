package com.example.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 支付回调请求DTO
 */
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackRequest {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotBlank(message = "交易ID不能为空")
    private String transactionId;

    @NotBlank(message = "支付状态不能为空")
    private String paymentStatus;

    private String paymentMethod;
    private String signature;

    // Manual getters and setters for Lombok compatibility
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
