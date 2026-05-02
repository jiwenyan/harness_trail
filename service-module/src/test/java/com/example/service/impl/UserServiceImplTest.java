package com.example.service.impl;

import com.example.dao.interfaces.UserDAO;
import com.example.data.entity.UserEntity;
import com.example.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setFullName("Test User");
        return user;
    }

    @Test
    void createUser_shouldCreateSuccessfully() {
        UserEntity user = createTestUser();
        when(userDAO.existsByUsername("testuser")).thenReturn(false);
        when(userDAO.existsByEmail("test@example.com")).thenReturn(false);
        when(userDAO.existsByPhone("13800138000")).thenReturn(false);
        when(userDAO.save(any(UserEntity.class))).thenReturn(user);
        UserEntity result = userService.createUser(user);
        assertNotNull(result);
        verify(userDAO).save(any(UserEntity.class));
    }

    @Test
    void createUser_shouldThrowWhenUsernameEmpty() {
        UserEntity user = createTestUser();
        user.setUsername(null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_shouldThrowWhenPasswordEmpty() {
        UserEntity user = createTestUser();
        user.setPassword(null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_shouldThrowWhenEmailEmpty() {
        UserEntity user = createTestUser();
        user.setEmail(null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_shouldThrowWhenPhoneEmpty() {
        UserEntity user = createTestUser();
        user.setPhone(null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_shouldThrowWhenUsernameExists() {
        UserEntity user = createTestUser();
        when(userDAO.existsByUsername("testuser")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_shouldThrowWhenEmailExists() {
        UserEntity user = createTestUser();
        when(userDAO.existsByUsername("testuser")).thenReturn(false);
        when(userDAO.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_shouldThrowWhenPhoneExists() {
        UserEntity user = createTestUser();
        when(userDAO.existsByUsername("testuser")).thenReturn(false);
        when(userDAO.existsByEmail("test@example.com")).thenReturn(false);
        when(userDAO.existsByPhone("13800138000")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_shouldUpdateSuccessfully() {
        UserEntity existing = createTestUser();
        UserEntity updates = new UserEntity();
        updates.setFullName("Updated Name");
        updates.setEmail("test@example.com");
        updates.setPhone("13800138000");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(userDAO.update(any(UserEntity.class))).thenReturn(existing);
        UserEntity result = userService.updateUser(1L, updates);
        assertNotNull(result);
        verify(userDAO).update(any(UserEntity.class));
    }

    @Test
    void updateUser_shouldThrowWhenNotExists() {
        when(userDAO.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, new UserEntity()));
    }

    @Test
    void updateUser_shouldCheckEmailUniquenessWhenChanged() {
        UserEntity existing = createTestUser();
        UserEntity updates = new UserEntity();
        updates.setEmail("newemail@example.com");
        updates.setPhone("13800138000");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(userDAO.existsByEmail("newemail@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void updateUser_shouldCheckPhoneUniquenessWhenChanged() {
        UserEntity existing = createTestUser();
        UserEntity updates = new UserEntity();
        updates.setEmail("test@example.com");
        updates.setPhone("13900139000");
        when(userDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(userDAO.existsByPhone("13900139000")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        when(userDAO.findById(1L)).thenReturn(Optional.of(createTestUser()));
        userService.deleteUser(1L);
        verify(userDAO).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowWhenNotExists() {
        when(userDAO.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        UserEntity user = createTestUser();
        when(userDAO.findById(1L)).thenReturn(Optional.of(user));
        var result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getUserById_shouldReturnEmptyWhenNotExists() {
        when(userDAO.findById(1L)).thenReturn(Optional.empty());
        var result = userService.getUserById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void getUserByUsername_shouldReturnUser() {
        UserEntity user = createTestUser();
        when(userDAO.findByUsername("testuser")).thenReturn(Optional.of(user));
        var result = userService.getUserByUsername("testuser");
        assertTrue(result.isPresent());
    }

    @Test
    void searchUsers_shouldReturnResults() {
        when(userDAO.searchByUsername("test")).thenReturn(Arrays.asList(createTestUser()));
        var result = userService.searchUsers("test");
        assertEquals(1, result.size());
    }

    @Test
    void validateCredentials_shouldReturnTrueWhenMatch() {
        UserEntity user = createTestUser();
        when(userDAO.findByUsername("testuser")).thenReturn(Optional.of(user));
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            user.setPassword(java.util.HexFormat.of().formatHex(md.digest("password123".getBytes())));
        } catch (Exception e) {}
        var result = userService.validateCredentials("testuser", "password123");
        assertTrue(result);
    }

    @Test
    void validateCredentials_shouldReturnFalseWhenNoMatch() {
        UserEntity user = createTestUser();
        when(userDAO.findByUsername("testuser")).thenReturn(Optional.of(user));
        var result = userService.validateCredentials("testuser", "wrongpassword");
        assertFalse(result);
    }

    @Test
    void isUsernameAvailable_shouldReturnTrueWhenNotExists() {
        when(userDAO.existsByUsername("newuser")).thenReturn(false);
        assertTrue(userService.isUsernameAvailable("newuser"));
    }

    @Test
    void isEmailAvailable_shouldReturnTrueWhenNotExists() {
        when(userDAO.existsByEmail("new@example.com")).thenReturn(false);
        assertTrue(userService.isEmailAvailable("new@example.com"));
    }

    @Test
    void isPhoneAvailable_shouldReturnTrueWhenNotExists() {
        when(userDAO.existsByPhone("13800138000")).thenReturn(false);
        assertTrue(userService.isPhoneAvailable("13800138000"));
    }
}