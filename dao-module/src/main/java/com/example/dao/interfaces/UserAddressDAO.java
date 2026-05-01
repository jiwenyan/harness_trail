package com.example.dao.interfaces;

import com.example.data.entity.UserAddressEntity;

import java.util.List;
import java.util.Optional;

/**
 * 用户地址数据访问接口
 */
public interface UserAddressDAO {

    Optional<UserAddressEntity> findById(Long id);

    List<UserAddressEntity> findByUserId(Long userId);

    UserAddressEntity save(UserAddressEntity address);

    UserAddressEntity update(UserAddressEntity address);

    void deleteById(Long id);

    boolean existsById(Long id);
}
