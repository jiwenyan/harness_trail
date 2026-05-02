package com.example.api.resources;

import com.example.api.dto.request.CreateFoodItemRequest;
import com.example.api.dto.response.FoodItemResponse;
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
            FoodItemResponse dto = convertToDTO(foodItem.get());
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
    public Response createFoodItem(@Valid CreateFoodItemRequest request) {
        FoodItemEntity foodItem = convertToEntity(request);
        FoodItemEntity createdFoodItem = foodItemService.createFoodItem(foodItem);
        FoodItemResponse responseDTO = convertToDTO(createdFoodItem);
        return Response.status(Response.Status.CREATED)
                .entity(responseDTO)
                .build();
    }

    /**
     * 更新菜品信息
     */
    @PUT
    @Path("/{id}")
    public Response updateFoodItem(@PathParam("id") Long id, @Valid CreateFoodItemRequest request) {
        FoodItemEntity foodItem = convertToEntity(request);
        FoodItemEntity updatedFoodItem = foodItemService.updateFoodItem(id, foodItem);
        FoodItemResponse responseDTO = convertToDTO(updatedFoodItem);
        return Response.ok(responseDTO).build();
    }

    /**
     * 删除菜品
     */
    @DELETE
    @Path("/{id}")
    public Response deleteFoodItem(@PathParam("id") Long id) {
        foodItemService.deleteFoodItem(id);
        return Response.noContent().build();
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

        List<FoodItemResponse> dtos = foodItemPage.getContent().stream()
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

        List<FoodItemResponse> dtos = foodItemPage.getContent().stream()
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
        List<FoodItemResponse> dtos = foodItems.stream()
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
        FoodItemEntity updatedFoodItem = foodItemService.updateStockQuantity(id, quantity);
        FoodItemResponse responseDTO = convertToDTO(updatedFoodItem);
        return Response.ok(responseDTO).build();
    }

    /**
     * 更新菜品可用状态
     */
    @PUT
    @Path("/{id}/availability")
    public Response updateAvailability(
            @PathParam("id") Long id,
            @QueryParam("available") Boolean available) {
        FoodItemEntity updatedFoodItem = foodItemService.updateAvailability(id, available);
        FoodItemResponse responseDTO = convertToDTO(updatedFoodItem);
        return Response.ok(responseDTO).build();
    }

    /**
     * 检查菜品库存是否充足
     */
    @GET
    @Path("/{id}/check-stock")
    public Response checkStockAvailability(
            @PathParam("id") Long id,
            @QueryParam("quantity") Integer quantity) {
        boolean isAvailable = foodItemService.checkStockAvailability(id, quantity);
        return Response.ok(isAvailable).build();
    }

    /**
     * 将FoodItemEntity转换为FoodItemResponse
     */
    private FoodItemResponse convertToDTO(FoodItemEntity entity) {
        FoodItemResponse dto = new FoodItemResponse();
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
     * 将CreateFoodItemRequest转换为FoodItemEntity
     */
    private FoodItemEntity convertToEntity(CreateFoodItemRequest request) {
        FoodItemEntity entity = new FoodItemEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setPrice(request.getPrice());
        entity.setImageUrl(request.getImageUrl());
        entity.setIsAvailable(request.getIsAvailable());
        entity.setStockQuantity(request.getStockQuantity());
        return entity;
    }
}
