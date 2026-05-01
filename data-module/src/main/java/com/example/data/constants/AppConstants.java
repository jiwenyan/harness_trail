package com.example.data.constants;

/**
 * 应用常量类
 */
public class AppConstants {

    private AppConstants() {
        // 防止实例化
    }

    // 分页常量
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // 订单相关常量
    public static final String ORDER_NUMBER_PREFIX = "ORD";
    public static final int ORDER_NUMBER_RANDOM_DIGITS = 3;

    // 时间格式
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    // 验证相关
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PHONE_LENGTH = 11;

    // 菜品相关
    public static final int FOOD_NAME_MAX_LENGTH = 100;
    public static final int FOOD_DESCRIPTION_MAX_LENGTH = 500;
    public static final int FOOD_CATEGORY_MAX_LENGTH = 50;

    // 地址相关
    public static final int ADDRESS_MAX_LENGTH = 200;

    // 错误消息
    public static final String ERROR_INVALID_INPUT = "输入参数无效";
    public static final String ERROR_RESOURCE_NOT_FOUND = "资源未找到";
    public static final String ERROR_UNAUTHORIZED = "未授权访问";
    public static final String ERROR_INTERNAL_SERVER = "服务器内部错误";

    // 成功消息
    public static final String SUCCESS_OPERATION = "操作成功";
    public static final String SUCCESS_CREATED = "创建成功";
    public static final String SUCCESS_UPDATED = "更新成功";
    public static final String SUCCESS_DELETED = "删除成功";
}