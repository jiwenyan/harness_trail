package com.example.dao.impl;

import com.example.dao.interfaces.UserAddressDAO;
import com.example.data.entity.UserAddressEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAddressDAOImplTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private UserAddressDAO userAddressDAO;

    @BeforeEach
    void setUp() {
        userAddressDAO = new UserAddressDAOImpl(mongoTemplate);
    }

    @Test
    void findById_shouldReturnAddressWhenExists() {
        UserAddressEntity address = new UserAddressEntity();
        address.setId(1L);
        when(mongoTemplate.findById(1L, UserAddressEntity.class, "user_addresses")).thenReturn(address);

        Optional<UserAddressEntity> result = userAddressDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        when(mongoTemplate.findById(1L, UserAddressEntity.class, "user_addresses")).thenReturn(null);

        Optional<UserAddressEntity> result = userAddressDAO.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findByUserId_shouldReturnAddresses() {
        when(mongoTemplate.find(any(Query.class), eq(UserAddressEntity.class), eq("user_addresses")))
                .thenReturn(Arrays.asList(new UserAddressEntity(), new UserAddressEntity()));

        List<UserAddressEntity> result = userAddressDAO.findByUserId(1L);

        assertEquals(2, result.size());
    }

    @Test
    void save_shouldSetIdForNewAddress() {
        UserAddressEntity address = new UserAddressEntity();
        when(mongoTemplate.save(any(UserAddressEntity.class), eq("user_addresses"))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mongoTemplate.findAndModify(any(Query.class), any(), any(), eq(org.bson.Document.class), eq("database_sequences")))
                .thenReturn(new org.bson.Document("seq", 1L));

        UserAddressEntity result = userAddressDAO.save(address);

        assertNotNull(result.getId());
    }

    @Test
    void update_shouldSaveAddress() {
        UserAddressEntity address = new UserAddressEntity();
        address.setId(1L);
        when(mongoTemplate.save(any(UserAddressEntity.class), eq("user_addresses"))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAddressEntity result = userAddressDAO.update(address);

        assertNotNull(result);
    }

    @Test
    void deleteById_shouldRemoveAddress() {
        userAddressDAO.deleteById(1L);

        verify(mongoTemplate).remove(any(Query.class), eq("user_addresses"));
    }

    @Test
    void existsById_shouldReturnCorrectResult() {
        when(mongoTemplate.exists(any(Query.class), eq(UserAddressEntity.class), eq("user_addresses"))).thenReturn(true);

        assertTrue(userAddressDAO.existsById(1L));
    }
}
