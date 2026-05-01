package com.example.data.enums;

/**
 * 支付状态枚举
 */
public enum PaymentStatus {
    PENDING("待支付"),
    PROCESSING("支付处理中"),
    SUCCESS("支付成功"),
    FAILED("支付失败"),
    REFUNDED("已退款"),
    CANCELLED("支付取消");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}