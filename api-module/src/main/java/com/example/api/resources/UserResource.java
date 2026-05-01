package com.example.api.resources;

import com.example.api.dto.UserDTO;
import com.example.data.entity.UserEntity;
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

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
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