package com.example.service.interfaces;

import com.example.data.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据ID获取用户
     */
    Optional<UserEntity> getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    Optional<UserEntity> getUserByUsername(String username);

    /**
     * 根据邮箱获取用户
     */
    Optional<UserEntity> getUserByEmail(String email);

    /**
     * 根据手机号获取用户
     */
    Optional<UserEntity> getUserByPhone(String phone);

    /**
     * 创建用户
     */
    UserEntity createUser(UserEntity user);

    /**
     * 更新用户信息
     */
    UserEntity updateUser(Long id, UserEntity user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 获取所有用户（分页）
     */
    Page<UserEntity> getAllUsers(Pageable pageable);

    /**
     * 搜索用户
     */
    List<UserEntity> searchUsers(String keyword);

    /**
     * 验证用户凭据
     */
    boolean validateCredentials(String username, String password);

    /**
     * 检查用户名是否可用
     */
    boolean isUsernameAvailable(String username);

    /**
     * 检查邮箱是否可用
     */
    boolean isEmailAvailable(String email);

    /**
     * 检查手机号是否可用
     */
    boolean isPhoneAvailable(String phone);
}