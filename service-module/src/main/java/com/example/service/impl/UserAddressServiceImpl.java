package com.example.service.impl;

import com.example.dao.interfaces.UserAddressDAO;
import com.example.data.entity.UserAddressEntity;
import com.example.service.interfaces.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户地址服务实现
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressDAO userAddressDAO;

    @Autowired
    public UserAddressServiceImpl(UserAddressDAO userAddressDAO) {
        this.userAddressDAO = userAddressDAO;
    }

    @Override
    public Optional<UserAddressEntity> getAddressById(Long id) {
        return userAddressDAO.findById(id);
    }

    @Override
    public List<UserAddressEntity> getAddressesByUserId(Long userId) {
        return userAddressDAO.findByUserId(userId);
    }

    @Override
    public Optional<UserAddressEntity> getDefaultAddress(Long userId) {
        List<UserAddressEntity> addresses = userAddressDAO.findByUserId(userId);
        return addresses.stream()
                .filter(UserAddressEntity::getIsDefault)
                .findFirst();
    }

    @Override
    public UserAddressEntity createAddress(UserAddressEntity address) {
        // 如果设置为默认地址，先取消该用户其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            unsetOtherDefaults(address.getUserId(), null);
        } else {
            // 如果用户暂无地址，自动设为默认
            List<UserAddressEntity> existing = userAddressDAO.findByUserId(address.getUserId());
            if (existing.isEmpty()) {
                address.setIsDefault(true);
            }
        }
        return userAddressDAO.save(address);
    }

    @Override
    public UserAddressEntity updateAddress(Long id, UserAddressEntity address) {
        UserAddressEntity existing = userAddressDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        existing.setContactName(address.getContactName());
        existing.setContactPhone(address.getContactPhone());
        existing.setStreet(address.getStreet());
        existing.setCity(address.getCity());
        existing.setState(address.getState());
        existing.setZipCode(address.getZipCode());
        existing.setLabel(address.getLabel());

        // 处理默认地址变更
        if (Boolean.TRUE.equals(address.getIsDefault()) && !Boolean.TRUE.equals(existing.getIsDefault())) {
            unsetOtherDefaults(existing.getUserId(), id);
            existing.setIsDefault(true);
        } else if (!Boolean.TRUE.equals(address.getIsDefault()) && Boolean.TRUE.equals(existing.getIsDefault())) {
            // 不允许取消唯一默认地址
            List<UserAddressEntity> userAddresses = userAddressDAO.findByUserId(existing.getUserId());
            boolean hasOtherDefault = userAddresses.stream()
                    .anyMatch(a -> !a.getId().equals(id) && Boolean.TRUE.equals(a.getIsDefault()));
            if (!hasOtherDefault) {
                throw new IllegalArgumentException("至少需要一个默认地址");
            }
            existing.setIsDefault(false);
        }

        return userAddressDAO.update(existing);
    }

    @Override
    public void deleteAddress(Long id) {
        UserAddressEntity address = userAddressDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        userAddressDAO.deleteById(id);
    }

    @Override
    public UserAddressEntity setDefaultAddress(Long userId, Long addressId) {
        UserAddressEntity address = userAddressDAO.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("地址不存在"));

        if (!address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不属于该用户");
        }

        unsetOtherDefaults(userId, addressId);
        address.setIsDefault(true);
        return userAddressDAO.update(address);
    }

    /**
     * 取消指定用户的其他默认地址
     */
    private void unsetOtherDefaults(Long userId, Long excludeAddressId) {
        List<UserAddressEntity> addresses = userAddressDAO.findByUserId(userId);
        for (UserAddressEntity addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault()) && !addr.getId().equals(excludeAddressId)) {
                addr.setIsDefault(false);
                userAddressDAO.update(addr);
            }
        }
    }
}
