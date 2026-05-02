package com.example.api.resources;

import com.example.api.dto.request.CreateAddressRequest;
import com.example.api.dto.request.CreateUserRequest;
import com.example.data.entity.UserAddressEntity;
import com.example.data.entity.UserEntity;
import com.example.service.interfaces.UserAddressService;
import com.example.service.interfaces.UserService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserAddressService userAddressService;

    private UserResource userResource;

    @BeforeEach
    void setUp() {
        userResource = new UserResource(userService, userAddressService);
    }

    private UserEntity createTestUser() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setFullName("Test User");
        return user;
    }

    @Test
    void getUserById_shouldReturn200WhenFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.of(createTestUser()));

        Response response = userResource.getUserById(1L);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getUserById_shouldReturn404WhenNotFound() {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        Response response = userResource.getUserById(1L);

        assertEquals(404, response.getStatus());
    }

    @Test
    void getUserByUsername_shouldReturn200WhenFound() {
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(createTestUser()));

        Response response = userResource.getUserByUsername("testuser");

        assertEquals(200, response.getStatus());
    }

    @Test
    void createUser_shouldReturn201() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");
        request.setPhone("13800138000");

        when(userService.createUser(any(UserEntity.class))).thenReturn(createTestUser());

        Response response = userResource.createUser(request);

        assertEquals(201, response.getStatus());
    }

    @Test
    void updateUser_shouldReturn200() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");
        request.setPhone("13800138000");

        when(userService.updateUser(eq(1L), any(UserEntity.class))).thenReturn(createTestUser());

        Response response = userResource.updateUser(1L, request);

        assertEquals(200, response.getStatus());
    }

    @Test
    void deleteUser_shouldReturn204() {
        Response response = userResource.deleteUser(1L);

        assertEquals(204, response.getStatus());
        verify(userService).deleteUser(1L);
    }

    @Test
    void getAllUsers_shouldReturn200() {
        Page<UserEntity> page = new PageImpl<>(Arrays.asList(createTestUser()));
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(page);

        Response response = userResource.getAllUsers(0, 20);

        assertEquals(200, response.getStatus());
    }

    @Test
    void searchUsers_shouldReturn200() {
        when(userService.searchUsers("test")).thenReturn(Arrays.asList(createTestUser()));

        Response response = userResource.searchUsers("test");

        assertEquals(200, response.getStatus());
    }

    @Test
    void validateCredentials_shouldReturn200() {
        when(userService.validateCredentials("testuser", "password")).thenReturn(true);

        Response response = userResource.validateCredentials("testuser", "password");

        assertEquals(200, response.getStatus());
    }

    @Test
    void checkUsernameAvailability_shouldReturn200() {
        when(userService.isUsernameAvailable("testuser")).thenReturn(true);

        Response response = userResource.checkUsernameAvailability("testuser");

        assertEquals(200, response.getStatus());
    }

    @Test
    void checkEmailAvailability_shouldReturn200() {
        when(userService.isEmailAvailable("test@example.com")).thenReturn(false);

        Response response = userResource.checkEmailAvailability("test@example.com");

        assertEquals(200, response.getStatus());
    }

    @Test
    void checkPhoneAvailability_shouldReturn200() {
        when(userService.isPhoneAvailable("13800138000")).thenReturn(true);

        Response response = userResource.checkPhoneAvailability("13800138000");

        assertEquals(200, response.getStatus());
    }

    @Test
    void getUserAddresses_shouldReturn200() {
        when(userAddressService.getAddressesByUserId(1L)).thenReturn(Collections.emptyList());

        Response response = userResource.getUserAddresses(1L);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getUserDefaultAddress_shouldReturn200WhenFound() {
        UserAddressEntity address = new UserAddressEntity();
        address.setId(1L);
        when(userAddressService.getDefaultAddress(1L)).thenReturn(Optional.of(address));

        Response response = userResource.getUserDefaultAddress(1L);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getUserDefaultAddress_shouldReturn404WhenNotFound() {
        when(userAddressService.getDefaultAddress(1L)).thenReturn(Optional.empty());

        Response response = userResource.getUserDefaultAddress(1L);

        assertEquals(404, response.getStatus());
    }

    @Test
    void createAddress_shouldReturn201() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setContactName("John");
        request.setContactPhone("13800138000");
        request.setStreet("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setZipCode("10001");

        when(userAddressService.createAddress(any(UserAddressEntity.class))).thenReturn(new UserAddressEntity());

        Response response = userResource.createAddress(1L, request);

        assertEquals(201, response.getStatus());
    }

    @Test
    void updateAddress_shouldReturn200() {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setContactName("John");
        request.setContactPhone("13800138000");
        request.setStreet("123 Main St");
        request.setCity("New York");
        request.setState("NY");
        request.setZipCode("10001");

        when(userAddressService.updateAddress(eq(1L), any(UserAddressEntity.class))).thenReturn(new UserAddressEntity());

        Response response = userResource.updateAddress(1L, 1L, request);

        assertEquals(200, response.getStatus());
    }

    @Test
    void deleteAddress_shouldReturn204() {
        Response response = userResource.deleteAddress(1L, 1L);

        assertEquals(204, response.getStatus());
        verify(userAddressService).deleteAddress(1L);
    }

    @Test
    void setDefaultAddress_shouldReturn200() {
        when(userAddressService.setDefaultAddress(1L, 1L)).thenReturn(new UserAddressEntity());

        Response response = userResource.setDefaultAddress(1L, 1L);

        assertEquals(200, response.getStatus());
    }
}
