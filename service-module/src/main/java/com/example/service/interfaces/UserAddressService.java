package com.example.service.interfaces;

import com.example.data.entity.UserAddressEntity;

import java.util.List;
import java.util.Optional;

/**
 * 用户地址服务接口
 */
public interface UserAddressService {

    /**
     * 根据ID获取地址
     */
    Optional<UserAddressEntity> getAddressById(Long id);

    /**
     * 获取用户的所有地址
     */
    List<UserAddressEntity> getAddressesByUserId(Long userId);

    /**
     * 获取用户的默认地址
     */
    Optional<UserAddressEntity> getDefaultAddress(Long userId);

    /**
     * 创建地址
     */
    UserAddressEntity createAddress(UserAddressEntity address);

    /**
     * 更新地址
     */
    UserAddressEntity updateAddress(Long id, UserAddressEntity address);

    /**
     * 删除地址
     */
    void deleteAddress(Long id);

    /**
     * 设置默认地址
     */
    UserAddressEntity setDefaultAddress(Long userId, Long addressId);
}
