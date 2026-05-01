package com.example.api.resources;

import com.example.api.dto.UserAddressDTO;
import com.example.api.dto.UserDTO;
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
            UserDTO dto = convertToDTO(user.get());
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
            UserDTO dto = convertToDTO(user.get());
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
            UserDTO dto = convertToDTO(user.get());
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
    public Response createUser(@Valid UserDTO userDTO) {
        try {
            UserEntity user = convertToEntity(userDTO);
            UserEntity createdUser = userService.createUser(user);
            UserDTO responseDTO = convertToDTO(createdUser);
            return Response.status(Response.Status.CREATED)
                    .entity(responseDTO)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 更新用户信息
     */
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, @Valid UserDTO userDTO) {
        try {
            UserEntity user = convertToEntity(userDTO);
            UserEntity updatedUser = userService.updateUser(id, user);
            UserDTO responseDTO = convertToDTO(updatedUser);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 删除用户
     */
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
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

        List<UserDTO> dtos = userPage.getContent().stream()
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
        List<UserDTO> dtos = users.stream()
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
        List<UserAddressDTO> dtos = addresses.stream()
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
    public Response createAddress(@PathParam("userId") Long userId, @Valid UserAddressDTO dto) {
        try {
            dto.setUserId(userId);
            UserAddressEntity entity = convertAddressToEntity(dto);
            UserAddressEntity created = userAddressService.createAddress(entity);
            return Response.status(Response.Status.CREATED)
                    .entity(convertAddressToDTO(created))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 更新地址
     */
    @PUT
    @Path("/{userId}/addresses/{addressId}")
    public Response updateAddress(
            @PathParam("userId") Long userId,
            @PathParam("addressId") Long addressId,
            @Valid UserAddressDTO dto) {
        try {
            dto.setUserId(userId);
            UserAddressEntity entity = convertAddressToEntity(dto);
            UserAddressEntity updated = userAddressService.updateAddress(addressId, entity);
            return Response.ok(convertAddressToDTO(updated)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 删除地址
     */
    @DELETE
    @Path("/{userId}/addresses/{addressId}")
    public Response deleteAddress(
            @PathParam("userId") Long userId,
            @PathParam("addressId") Long addressId) {
        try {
            userAddressService.deleteAddress(addressId);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 设置默认地址
     */
    @PUT
    @Path("/{userId}/addresses/{addressId}/default")
    public Response setDefaultAddress(
            @PathParam("userId") Long userId,
            @PathParam("addressId") Long addressId) {
        try {
            UserAddressEntity updated = userAddressService.setDefaultAddress(userId, addressId);
            return Response.ok(convertAddressToDTO(updated)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 将UserEntity转换为UserDTO
     */
    private UserDTO convertToDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
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
     * 将UserDTO转换为UserEntity
     */
    /**
     * 将UserAddressEntity转换为UserAddressDTO
     */
    private UserAddressDTO convertAddressToDTO(UserAddressEntity entity) {
        UserAddressDTO dto = new UserAddressDTO();
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
     * 将UserAddressDTO转换为UserAddressEntity
     */
    private UserAddressEntity convertAddressToEntity(UserAddressDTO dto) {
        UserAddressEntity entity = new UserAddressEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setContactName(dto.getContactName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setStreet(dto.getStreet());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setZipCode(dto.getZipCode());
        entity.setIsDefault(dto.getIsDefault());
        entity.setLabel(dto.getLabel());
        return entity;
    }

    private UserEntity convertToEntity(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setFullName(dto.getFullName());
        entity.setDefaultAddress(dto.getDefaultAddress());
        return entity;
    }
}