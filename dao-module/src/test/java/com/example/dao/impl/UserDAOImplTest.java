package com.example.dao.impl;

import com.example.dao.interfaces.UserDAO;
import com.example.data.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.bson.Document;

@ExtendWith(MockitoExtension.class)
class UserDAOImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl(mongoTemplate);
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        when(mongoTemplate.findById(1L, UserEntity.class, "users")).thenReturn(user);

        Optional<UserEntity> result = userDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        when(mongoTemplate.findById(1L, UserEntity.class, "users")).thenReturn(null);

        Optional<UserEntity> result = userDAO.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_shouldReturnUserWhenExists() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        when(mongoTemplate.findOne(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(user);

        Optional<UserEntity> result = userDAO.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        when(mongoTemplate.findOne(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(user);

        Optional<UserEntity> result = userDAO.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void findByPhone_shouldReturnUserWhenExists() {
        UserEntity user = new UserEntity();
        user.setPhone("13800138000");
        when(mongoTemplate.findOne(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(user);

        Optional<UserEntity> result = userDAO.findByPhone("13800138000");

        assertTrue(result.isPresent());
        assertEquals("13800138000", result.get().getPhone());
    }

    @Test
    void save_shouldSetIdAndTimestampsForNewUser() {
        UserEntity user = new UserEntity();
        user.setUsername("newuser");
        when(mongoTemplate.save(any(UserEntity.class), eq("users"))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(Query.class), any(), any(), eq(org.bson.Document.class), eq("database_sequences")))
                .thenReturn(new Document("seq", 1L));

        UserEntity result = userDAO.save(user);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void save_shouldPreserveIdForExistingUser() {
        UserEntity user = new UserEntity();
        user.setId(5L);
        user.setUsername("existinguser");
        when(mongoTemplate.save(any(UserEntity.class), eq("users"))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userDAO.save(user);

        assertEquals(5L, result.getId());
    }

    @Test
    void update_shouldSetUpdatedAt() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("updateduser");
        when(mongoTemplate.save(any(UserEntity.class), eq("users"))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userDAO.update(user);

        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void deleteById_shouldRemoveUser() {
        userDAO.deleteById(1L);

        verify(mongoTemplate).remove(any(Query.class), eq("users"));
    }

    @Test
    void existsByUsername_shouldReturnTrueWhenExists() {
        when(mongoTemplate.exists(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(true);

        boolean result = userDAO.existsByUsername("testuser");

        assertTrue(result);
    }

    @Test
    void existsByUsername_shouldReturnFalseWhenNotExists() {
        when(mongoTemplate.exists(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(false);

        boolean result = userDAO.existsByUsername("nonexistent");

        assertFalse(result);
    }

    @Test
    void existsByEmail_shouldReturnCorrectResult() {
        when(mongoTemplate.exists(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(true);

        assertTrue(userDAO.existsByEmail("test@example.com"));
    }

    @Test
    void existsByPhone_shouldReturnCorrectResult() {
        when(mongoTemplate.exists(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(false);

        assertFalse(userDAO.existsByPhone("13800138000"));
    }

    @Test
    void findAll_shouldReturnPagedResults() {
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.find(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(Arrays.asList(user1, user2));
        when(mongoTemplate.count(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(2L);

        Page<UserEntity> result = userDAO.findAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void searchByUsername_shouldReturnMatchingUsers() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        when(mongoTemplate.find(any(Query.class), eq(UserEntity.class), eq("users"))).thenReturn(Arrays.asList(user));

        List<UserEntity> result = userDAO.searchByUsername("test");

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
}
