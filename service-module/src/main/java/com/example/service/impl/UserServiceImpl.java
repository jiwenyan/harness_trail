package com.example.service.impl;

import com.example.dao.interfaces.UserDAO;
import com.example.data.entity.UserEntity;
import com.example.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        return userDAO.findById(id);
    }

    @Override
    public Optional<UserEntity> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> getUserByPhone(String phone) {
        return userDAO.findByPhone(phone);
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        // 验证用户数据
        validateUserData(user);

        // 检查用户名、邮箱、手机号是否已存在
        if (userDAO.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        if (userDAO.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        if (userDAO.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("手机号已存在");
        }

        return userDAO.save(user);
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity user) {
        // 验证用户是否存在
        UserEntity existingUser = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 更新用户信息
        existingUser.setFullName(user.getFullName());
        existingUser.setDefaultAddress(user.getDefaultAddress());

        // 如果邮箱有变化，检查新邮箱是否可用
        if (!existingUser.getEmail().equals(user.getEmail())) {
            if (userDAO.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("邮箱已存在");
            }
            existingUser.setEmail(user.getEmail());
        }

        // 如果手机号有变化，检查新手机号是否可用
        if (!existingUser.getPhone().equals(user.getPhone())) {
            if (userDAO.existsByPhone(user.getPhone())) {
                throw new IllegalArgumentException("手机号已存在");
            }
            existingUser.setPhone(user.getPhone());
        }

        return userDAO.update(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        // 验证用户是否存在
        if (!userDAO.findById(id).isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        userDAO.deleteById(id);
    }

    @Override
    public Page<UserEntity> getAllUsers(Pageable pageable) {
        return userDAO.findAll(pageable);
    }

    @Override
    public List<UserEntity> searchUsers(String keyword) {
        return userDAO.searchByUsername(keyword);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        Optional<UserEntity> user = userDAO.findByUsername(username);
        return user.isPresent() && user.get().getPassword().equals(password);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userDAO.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userDAO.existsByEmail(email);
    }

    @Override
    public boolean isPhoneAvailable(String phone) {
        return !userDAO.existsByPhone(phone);
    }

    /**
     * 验证用户数据
     */
    private void validateUserData(UserEntity user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
    }
}