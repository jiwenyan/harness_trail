package com.example.api.resources;

import com.example.api.dto.request.CreateAddressRequest;
import com.example.api.dto.request.CreateUserRequest;
import com.example.api.dto.response.UserAddressResponse;
import com.example.api.dto.response.UserResponse;
import com.example.data.entity.UserAddressEntity;
import com.example.data.entity.UserEntity;
import com.example.service.interfaces.UserAddressService;
import com.example.service.interfaces.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户资源
 */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class UserResource {

    private final UserService userService;
    private final UserAddressService userAddressService;

    @Autowired
    public UserResource(UserService userService, UserAddressService userAddressService) {
        this.userService = userService;
        this.userAddressService = userAddressService;
    }

    /**
     * 根据ID获取用户
     */
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") Long id) {
        Optional<UserEntity> user = userService.getUserById(id);
        if (user.isPresent()) {
            UserResponse dto = convertToDTO(user.get());
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("用户不存在")
                .build();
    }

    /**
     * 根据用户名获取用户
     */
    @GET
    @Path("/username/{username}")
    public Response getUserByUsername(@PathParam("username") String username) {
        Optional<UserEntity> user = userService.getUserByUsername(username);
        if (user.isPresent()) {
            UserResponse dto = convertToDTO(user.get());
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("用户不存在")
                .build();
    }

    /**
     * 根据邮箱获取用户
     */
    @GET
    @Path("/email/{email}")
    public Response getUserByEmail(@PathParam("email") String email) {
        Optional<UserEntity> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            UserResponse dto = convertToDTO(user.get());
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("用户不存在")
                .build();
    }

    /**
     * 创建用户
     */
    @POST
    public Response createUser(@Valid CreateUserRequest request) {
        UserEntity user = convertToEntity(request);
        UserEntity createdUser = userService.createUser(user);
        UserResponse responseDTO = convertToDTO(createdUser);
        return Response.status(Response.Status.CREATED)
                .entity(responseDTO)
                .build();
    }

    /**
     * 更新用户信息
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, @Valid CreateUserRequest request) {
        UserEntity user = convertToEntity(request);
        user.setId(id);
        UserEntity updatedUser = userService.updateUser(id, user);
        UserResponse responseDTO = convertToDTO(updatedUser);
        return Response.ok(responseDTO).build();
    }

    /**
     * 删除用户
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.noContent().build();
    }

    /**
     * 获取所有用户（分页）
     */
    @GET
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> userPage = userService.getAllUsers(pageable);

        List<UserResponse> dtos = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Response.ok(dtos)
                .header("X-Total-Count", userPage.getTotalElements())
                .header("X-Total-Pages", userPage.getTotalPages())
                .build();
    }

    /**
     * 搜索用户
     */
    @GET
    @Path("/search")
    public Response searchUsers(@QueryParam("keyword") String keyword) {
        List<UserEntity> users = userService.searchUsers(keyword);
        List<UserResponse> dtos = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /**
     * 验证用户凭据
     */
    @POST
    @Path("/validate")
    public Response validateCredentials(
            @FormParam("username") String username,
            @FormParam("password") String password) {
        boolean isValid = userService.validateCredentials(username, password);
        return Response.ok(isValid).build();
    }

    /**
     * 检查用户名是否可用
     */
    @GET
    @Path("/check-username/{username}")
    public Response checkUsernameAvailability(@PathParam("username") String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);
        return Response.ok(isAvailable).build();
    }

    /**
     * 检查邮箱是否可用
     */
    @GET
    @Path("/check-email/{email}")
    public Response checkEmailAvailability(@PathParam("email") String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        return Response.ok(isAvailable).build();
    }

    /**
     * 检查手机号是否可用
     */
    @GET
    @Path("/check-phone/{phone}")
    public Response checkPhoneAvailability(@PathParam("phone") String phone) {
        boolean isAvailable = userService.isPhoneAvailable(phone);
        return Response.ok(isAvailable).build();
    }

    // ========== 地址管理 ==========

    /**
     * 获取用户的所有地址
     */
    @GET
    @Path("/{userId}/addresses")
    public Response getUserAddresses(@PathParam("userId") Long userId) {
        List<UserAddressEntity> addresses = userAddressService.getAddressesByUserId(userId);
        List<UserAddressResponse> dtos = addresses.stream()
                .map(this::convertAddressToDTO)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /**
     * 获取用户默认地址
     */
    @GET
    @Path("/{userId}/addresses/default")
    public Response getUserDefaultAddress(@PathParam("userId") Long userId) {
        Optional<UserAddressEntity> address = userAddressService.getDefaultAddress(userId);
        if (address.isPresent()) {
            return Response.ok(convertAddressToDTO(address.get())).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("默认地址不存在")
                .build();
    }

    /**
     * 创建地址
     */
    @POST
    @Path("/{userId}/addresses")
    public Response createAddress(@PathParam("userId") Long userId, @Valid CreateAddressRequest request) {
        request.setIsDefault(request.getIsDefault() != null && request.getIsDefault());
        UserAddressEntity entity = convertAddressToEntity(request);
        entity.setUserId(userId);
        UserAddressEntity created = userAddressService.createAddress(entity);
        return Response.status(Response.Status.CREATED)
                .entity(convertAddressToDTO(created))
                .build();
    }

    /**
     * 更新地址
     */
    @PUT
    @Path("/{userId}/addresses/{addressId}")
    public Response updateAddress(
            @PathParam("userId") Long userId,
            @PathParam("addressId") Long addressId,
            @Valid CreateAddressRequest request) {
        UserAddressEntity entity = convertAddressToEntity(request);
        entity.setUserId(userId);
        entity.setId(addressId);
        UserAddressEntity updated = userAddressService.updateAddress(addressId, entity);
        return Response.ok(convertAddressToDTO(updated)).build();
    }

    /**
     * 删除地址
     */
    @DELETE
    @Path("/{userId}/addresses/{addressId}")
    public Response deleteAddress(
            @PathParam("userId") Long userId,
            @PathParam("addressId") Long addressId) {
        userAddressService.deleteAddress(addressId);
        return Response.noContent().build();
    }

    /**
     * 设置默认地址
     */
    @PUT
    @Path("/{userId}/addresses/{addressId}/default")
    public Response setDefaultAddress(
            @PathParam("userId") Long userId,
            @PathParam("addressId") Long addressId) {
        UserAddressEntity updated = userAddressService.setDefaultAddress(userId, addressId);
        return Response.ok(convertAddressToDTO(updated)).build();
    }

    /**
     * 将UserEntity转换为UserResponse
     */
    private UserResponse convertToDTO(UserEntity entity) {
        UserResponse dto = new UserResponse();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        // 注意：不返回密码字段
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setFullName(entity.getFullName());
        dto.setDefaultAddress(entity.getDefaultAddress());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * 将UserAddressEntity转换为UserAddressResponse
     */
    private UserAddressResponse convertAddressToDTO(UserAddressEntity entity) {
        UserAddressResponse dto = new UserAddressResponse();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setContactName(entity.getContactName());
        dto.setContactPhone(entity.getContactPhone());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setZipCode(entity.getZipCode());
        dto.setIsDefault(entity.getIsDefault());
        dto.setLabel(entity.getLabel());
        return dto;
    }

    /**
     * 将CreateAddressRequest转换为UserAddressEntity
     */
    private UserAddressEntity convertAddressToEntity(CreateAddressRequest request) {
        UserAddressEntity entity = new UserAddressEntity();
        entity.setContactName(request.getContactName());
        entity.setContactPhone(request.getContactPhone());
        entity.setStreet(request.getStreet());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setZipCode(request.getZipCode());
        entity.setIsDefault(request.getIsDefault());
        entity.setLabel(request.getLabel());
        return entity;
    }

    /**
     * 将CreateUserRequest转换为UserEntity
     */
    private UserEntity convertToEntity(CreateUserRequest request) {
        UserEntity entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setPassword(request.getPassword());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setFullName(request.getFullName());
        entity.setDefaultAddress(request.getDefaultAddress());
        return entity;
    }
}
