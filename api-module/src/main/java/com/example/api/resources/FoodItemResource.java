package com.example.api.resources;

import com.example.api.dto.FoodItemDTO;
import com.example.data.entity.FoodItemEntity;
import com.example.service.interfaces.FoodItemService;
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
 * 菜品资源
 */
@Path("/api/food-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class FoodItemResource {

    private final FoodItemService foodItemService;

    @Autowired
    public FoodItemResource(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    /**
     * 根据ID获取菜品
     */
    @GET
    @Path("/{id}")
    public Response getFoodItemById(@PathParam("id") Long id) {
        Optional<FoodItemEntity> foodItem = foodItemService.getFoodItemById(id);
        if (foodItem.isPresent()) {
            FoodItemDTO dto = convertToDTO(foodItem.get());
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("菜品不存在")
                .build();
    }

    /**
     * 创建菜品
     */
    @POST
    public Response createFoodItem(@Valid FoodItemDTO foodItemDTO) {
        try {
            FoodItemEntity foodItem = convertToEntity(foodItemDTO);
            FoodItemEntity createdFoodItem = foodItemService.createFoodItem(foodItem);
            FoodItemDTO responseDTO = convertToDTO(createdFoodItem);
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
     * 更新菜品信息
     */
    @PUT
    @Path("/{id}")
    public Response updateFoodItem(@PathParam("id") Long id, @Valid FoodItemDTO foodItemDTO) {
        try {
            FoodItemEntity foodItem = convertToEntity(foodItemDTO);
            FoodItemEntity updatedFoodItem = foodItemService.updateFoodItem(id, foodItem);
            FoodItemDTO responseDTO = convertToDTO(updatedFoodItem);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 删除菜品
     */
    @DELETE
    @Path("/{id}")
    public Response deleteFoodItem(@PathParam("id") Long id) {
        try {
            foodItemService.deleteFoodItem(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 获取所有菜品（分页）
     */
    @GET
    public Response getAllFoodItems(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FoodItemEntity> foodItemPage = foodItemService.getAllFoodItems(pageable);

        List<FoodItemDTO> dtos = foodItemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Response.ok(dtos)
                .header("X-Total-Count", foodItemPage.getTotalElements())
                .header("X-Total-Pages", foodItemPage.getTotalPages())
                .build();
    }

    /**
     * 根据分类获取菜品
     */
    @GET
    @Path("/category/{category}")
    public Response getFoodItemsByCategory(
            @PathParam("category") String category,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FoodItemEntity> foodItemPage = foodItemService.getFoodItemsByCategory(category, pageable);

        List<FoodItemDTO> dtos = foodItemPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Response.ok(dtos)
                .header("X-Total-Count", foodItemPage.getTotalElements())
                .header("X-Total-Pages", foodItemPage.getTotalPages())
                .build();
    }

    /**
     * 搜索菜品
     */
    @GET
    @Path("/search")
    public Response searchFoodItems(@QueryParam("keyword") String keyword) {
        List<FoodItemEntity> foodItems = foodItemService.searchFoodItems(keyword);
        List<FoodItemDTO> dtos = foodItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    /**
     * 获取所有分类
     */
    @GET
    @Path("/categories")
    public Response getAllCategories() {
        List<String> categories = foodItemService.getAllCategories();
        return Response.ok(categories).build();
    }

    /**
     * 更新菜品库存
     */
    @PUT
    @Path("/{id}/stock")
    public Response updateStockQuantity(
            @PathParam("id") Long id,
            @QueryParam("quantity") Integer quantity) {
        try {
            FoodItemEntity updatedFoodItem = foodItemService.updateStockQuantity(id, quantity);
            FoodItemDTO responseDTO = convertToDTO(updatedFoodItem);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 更新菜品可用状态
     */
    @PUT
    @Path("/{id}/availability")
    public Response updateAvailability(
            @PathParam("id") Long id,
            @QueryParam("available") Boolean available) {
        try {
            FoodItemEntity updatedFoodItem = foodItemService.updateAvailability(id, available);
            FoodItemDTO responseDTO = convertToDTO(updatedFoodItem);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 检查菜品库存是否充足
     */
    @GET
    @Path("/{id}/check-stock")
    public Response checkStockAvailability(
            @PathParam("id") Long id,
            @QueryParam("quantity") Integer quantity) {
        try {
            boolean isAvailable = foodItemService.checkStockAvailability(id, quantity);
            return Response.ok(isAvailable).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 将FoodItemEntity转换为FoodItemDTO
     */
    private FoodItemDTO convertToDTO(FoodItemEntity entity) {
        FoodItemDTO dto = new FoodItemDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCategory(entity.getCategory());
        dto.setPrice(entity.getPrice());
        dto.setImageUrl(entity.getImageUrl());
        dto.setIsAvailable(entity.getIsAvailable());
        dto.setStockQuantity(entity.getStockQuantity());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * 将FoodItemDTO转换为FoodItemEntity
     */
    private FoodItemEntity convertToEntity(FoodItemDTO dto) {
        FoodItemEntity entity = new FoodItemEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCategory(dto.getCategory());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
        entity.setIsAvailable(dto.getIsAvailable());
        entity.setStockQuantity(dto.getStockQuantity());
        return entity;
    }
}