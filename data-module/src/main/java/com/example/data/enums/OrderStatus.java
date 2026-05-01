package com.example.data.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    PENDING("待处理"),
    CONFIRMED("已确认"),
    PREPARING("准备中"),
    READY("已就绪"),
    DELIVERING("配送中"),
    DELIVERED("已送达"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}