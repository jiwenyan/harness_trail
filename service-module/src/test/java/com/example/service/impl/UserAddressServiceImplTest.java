package com.example.service.impl;

import com.example.dao.interfaces.UserAddressDAO;
import com.example.data.entity.UserAddressEntity;
import com.example.service.interfaces.UserAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAddressServiceImplTest {

    @Mock
    private UserAddressDAO userAddressDAO;

    private UserAddressService userAddressService;

    @BeforeEach
    void setUp() {
        userAddressService = new UserAddressServiceImpl(userAddressDAO);
    }

    private UserAddressEntity createTestAddress() {
        UserAddressEntity address = new UserAddressEntity();
        address.setId(1L);
        address.setUserId(1L);
        address.setContactName("John");
        address.setContactPhone("13800138000");
        address.setStreet("123 Main St");
        address.setCity("New York");
        address.setState("NY");
        address.setZipCode("10001");
        address.setIsDefault(false);
        address.setLabel("Home");
        return address;
    }

    @Test
    void createAddress_firstAddressShouldBeDefault() {
        UserAddressEntity address = createTestAddress();
        when(userAddressDAO.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(userAddressDAO.save(any(UserAddressEntity.class))).thenReturn(address);

        UserAddressEntity result = userAddressService.createAddress(address);

        assertTrue(result.getIsDefault());
    }

    @Test
    void createAddress_setDefaultUnsetsOthers() {
        UserAddressEntity address1 = createTestAddress();
        address1.setId(1L);
        address1.setIsDefault(true);

        UserAddressEntity newAddress = createTestAddress();
        newAddress.setId(2L);
        newAddress.setIsDefault(true);

        when(userAddressDAO.findByUserId(1L)).thenReturn(Collections.singletonList(address1));
        when(userAddressDAO.save(any(UserAddressEntity.class))).thenReturn(newAddress);

        userAddressService.createAddress(newAddress);

        assertFalse(address1.getIsDefault());
        verify(userAddressDAO).update(address1);
    }

    @Test
    void createAddress_notDefaultWithExistingAddresses() {
        UserAddressEntity existing = createTestAddress();
        existing.setIsDefault(true);

        UserAddressEntity newAddress = createTestAddress();
        newAddress.setId(2L);
        newAddress.setIsDefault(false);

        when(userAddressDAO.findByUserId(1L)).thenReturn(Collections.singletonList(existing));
        when(userAddressDAO.save(any(UserAddressEntity.class))).thenReturn(newAddress);

        UserAddressEntity result = userAddressService.createAddress(newAddress);

        assertFalse(result.getIsDefault());
    }

    @Test
    void getAddressesByUserId_shouldReturnAddresses() {
        when(userAddressDAO.findByUserId(1L)).thenReturn(Arrays.asList(createTestAddress()));

        List<UserAddressEntity> result = userAddressService.getAddressesByUserId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getDefaultAddress_shouldReturnDefaultWhenExists() {
        UserAddressEntity address1 = createTestAddress();
        address1.setIsDefault(false);
        UserAddressEntity address2 = createTestAddress();
        address2.setId(2L);
        address2.setIsDefault(true);

        when(userAddressDAO.findByUserId(1L)).thenReturn(Arrays.asList(address1, address2));

        Optional<UserAddressEntity> result = userAddressService.getDefaultAddress(1L);

        assertTrue(result.isPresent());
        assertTrue(result.get().getIsDefault());
    }

    @Test
    void getDefaultAddress_shouldReturnEmptyWhenNoDefault() {
        UserAddressEntity address = createTestAddress();
        address.setIsDefault(false);

        when(userAddressDAO.findByUserId(1L)).thenReturn(Collections.singletonList(address));

        Optional<UserAddressEntity> result = userAddressService.getDefaultAddress(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void updateAddress_shouldUpdateSuccessfully() {
        UserAddressEntity existing = createTestAddress();
        UserAddressEntity updates = createTestAddress();
        updates.setContactName("Jane");
        updates.setIsDefault(false);

        when(userAddressDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(userAddressDAO.update(any(UserAddressEntity.class))).thenReturn(existing);

        UserAddressEntity result = userAddressService.updateAddress(1L, updates);

        assertNotNull(result);
        verify(userAddressDAO).update(any(UserAddressEntity.class));
    }

    @Test
    void updateAddress_shouldThrowWhenNotExists() {
        when(userAddressDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userAddressService.updateAddress(1L, new UserAddressEntity()));
    }

    @Test
    void updateAddress_settingDefaultUnsetsOthers() {
        UserAddressEntity existing = createTestAddress();
        existing.setIsDefault(false);

        UserAddressEntity address2 = createTestAddress();
        address2.setId(2L);
        address2.setIsDefault(true);

        UserAddressEntity updates = createTestAddress();
        updates.setIsDefault(true);

        when(userAddressDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(userAddressDAO.findByUserId(1L)).thenReturn(Arrays.asList(existing, address2));
        when(userAddressDAO.update(any(UserAddressEntity.class))).thenReturn(existing);

        userAddressService.updateAddress(1L, updates);

        assertFalse(address2.getIsDefault());
    }

    @Test
    void deleteAddress_shouldDeleteSuccessfully() {
        UserAddressEntity address = createTestAddress();
        when(userAddressDAO.findById(1L)).thenReturn(Optional.of(address));

        userAddressService.deleteAddress(1L);

        verify(userAddressDAO).deleteById(1L);
    }

    @Test
    void deleteAddress_shouldThrowWhenNotExists() {
        when(userAddressDAO.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userAddressService.deleteAddress(1L));
    }

    @Test
    void setDefaultAddress_shouldSetDefaultSuccessfully() {
        UserAddressEntity existing = createTestAddress();
        existing.setIsDefault(false);

        when(userAddressDAO.findById(1L)).thenReturn(Optional.of(existing));
        when(userAddressDAO.update(any(UserAddressEntity.class))).thenReturn(existing);

        UserAddressEntity result = userAddressService.setDefaultAddress(1L, 1L);

        assertTrue(result.getIsDefault());
    }

    @Test
    void setDefaultAddress_shouldThrowWhenUserIdMismatch() {
        UserAddressEntity address = createTestAddress();
        address.setUserId(2L);

        when(userAddressDAO.findById(1L)).thenReturn(Optional.of(address));

        assertThrows(IllegalArgumentException.class,
                () -> userAddressService.setDefaultAddress(1L, 1L));
    }

    @Test
    void setDefaultAddress_shouldUnsetOtherDefaults() {
        UserAddressEntity address1 = createTestAddress();
        address1.setId(1L);
        address1.setIsDefault(false);

        UserAddressEntity address2 = createTestAddress();
        address2.setId(2L);
        address2.setIsDefault(true);

        when(userAddressDAO.findById(1L)).thenReturn(Optional.of(address1));
        when(userAddressDAO.findByUserId(1L)).thenReturn(Arrays.asList(address1, address2));
        when(userAddressDAO.update(any(UserAddressEntity.class))).thenReturn(address1);

        userAddressService.setDefaultAddress(1L, 1L);

        assertFalse(address2.getIsDefault());
    }
}