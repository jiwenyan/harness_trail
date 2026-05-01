package com.example.dao.interfaces;

import com.example.data.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
public interface UserDAO {

    /**
     * 根据ID查找用户
     */
    Optional<UserEntity> findById(Long id);

    /**
     * 根据用户名查找用户
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<UserEntity> findByPhone(String phone);

    /**
     * 保存用户
     */
    UserEntity save(UserEntity user);

    /**
     * 更新用户
     */
    UserEntity update(UserEntity user);

    /**
     * 删除用户
     */
    void deleteById(Long id);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 获取所有用户（分页）
     */
    Page<UserEntity> findAll(Pageable pageable);

    /**
     * 根据用户名搜索用户
     */
    List<UserEntity> searchByUsername(String username);
}