package com.example.api.resources;

import com.example.api.dto.OrderDTO;
import com.example.api.dto.OrderItemDTO;
import com.example.data.entity.OrderEntity;
import com.example.data.entity.OrderItemEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import com.example.service.interfaces.OrderService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单资源
 */
@Path("/api/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class OrderResource {

    private final OrderService orderService;

    @Autowired
    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 根据ID获取订单
     */
    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        Optional<OrderEntity> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            OrderDTO dto = convertToDTO(order.get());
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("订单不存在")
                .build();
    }

    /**
     * 根据订单号获取订单
     */
    @GET
    @Path("/order-number/{orderNumber}")
    public Response getOrderByOrderNumber(@PathParam("orderNumber") String orderNumber) {
        Optional<OrderEntity> order = orderService.getOrderByOrderNumber(orderNumber);
        if (order.isPresent()) {
            OrderDTO dto = convertToDTO(order.get());
            return Response.ok(dto).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("订单不存在")
                .build();
    }

    /**
     * 创建订单
     */
    @POST
    public Response createOrder(@Valid OrderDTO orderDTO) {
        try {
            OrderEntity order = convertToEntity(orderDTO);
            OrderEntity createdOrder = orderService.createOrder(order);
            OrderDTO responseDTO = convertToDTO(createdOrder);
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
     * 更新订单信息
     */
    @PUT
    @Path("/{id}")
    public Response updateOrder(@PathParam("id") Long id, @Valid OrderDTO orderDTO) {
        try {
            OrderEntity order = convertToEntity(orderDTO);
            OrderEntity updatedOrder = orderService.updateOrder(id, order);
            OrderDTO responseDTO = convertToDTO(updatedOrder);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 删除订单
     */
    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        try {
            orderService.deleteOrder(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 获取所有订单（分页）
     */
    @GET
    public Response getAllOrders(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage = orderService.getAllOrders(pageable);

        List<OrderDTO> dtos = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Response.ok(dtos)
                .header("X-Total-Count", orderPage.getTotalElements())
                .header("X-Total-Pages", orderPage.getTotalPages())
                .build();
    }

    /**
     * 根据用户ID获取订单
     */
    @GET
    @Path("/user/{userId}")
    public Response getOrdersByUserId(
            @PathParam("userId") Long userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage = orderService.getOrdersByUserId(userId, pageable);

        List<OrderDTO> dtos = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return Response.ok(dtos)
                .header("X-Total-Count", orderPage.getTotalElements())
                .header("X-Total-Pages", orderPage.getTotalPages())
                .build();
    }

    /**
     * 更新订单状态
     */
    @PUT
    @Path("/{id}/status")
    public Response updateOrderStatus(
            @PathParam("id") Long id,
            @QueryParam("status") OrderStatus status) {
        try {
            OrderEntity updatedOrder = orderService.updateOrderStatus(id, status);
            OrderDTO responseDTO = convertToDTO(updatedOrder);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 更新支付状态
     */
    @PUT
    @Path("/{id}/payment-status")
    public Response updatePaymentStatus(
            @PathParam("id") Long id,
            @QueryParam("paymentStatus") PaymentStatus paymentStatus) {
        try {
            OrderEntity updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
            OrderDTO responseDTO = convertToDTO(updatedOrder);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 取消订单
     */
    @PUT
    @Path("/{id}/cancel")
    public Response cancelOrder(@PathParam("id") Long id) {
        try {
            OrderEntity cancelledOrder = orderService.cancelOrder(id);
            OrderDTO responseDTO = convertToDTO(cancelledOrder);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 计算订单总金额
     */
    @GET
    @Path("/{id}/total-amount")
    public Response calculateTotalAmount(@PathParam("id") Long id) {
        try {
            BigDecimal totalAmount = orderService.calculateTotalAmount(id);
            return Response.ok(totalAmount).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    /**
     * 将OrderEntity转换为OrderDTO
     */
    private OrderDTO convertToDTO(OrderEntity entity) {
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setUserId(entity.getUserId());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setDeliveryAddress(entity.getDeliveryAddress());
        dto.setDeliveryInstructions(entity.getDeliveryInstructions());
        dto.setStatus(entity.getStatus());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentTransactionId(entity.getPaymentTransactionId());
        dto.setEstimatedDeliveryTime(entity.getEstimatedDeliveryTime());
        dto.setActualDeliveryTime(entity.getActualDeliveryTime());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // 转换订单项
        if (entity.getOrderItems() != null) {
            List<OrderItemDTO> itemDTOs = entity.getOrderItems().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());
            dto.setOrderItems(itemDTOs);
        }

        return dto;
    }

    /**
     * 将OrderItemEntity转换为OrderItemDTO
     */
    private OrderItemDTO convertItemToDTO(OrderItemEntity entity) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(entity.getId());
        dto.setOrderId(entity.getOrderId());
        dto.setFoodItemId(entity.getFoodItemId());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setSpecialInstructions(entity.getSpecialInstructions());
        return dto;
    }

    /**
     * 将OrderDTO转换为OrderEntity
     */
    private OrderEntity convertToEntity(OrderDTO dto) {
        OrderEntity entity = new OrderEntity();
        entity.setId(dto.getId());
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setUserId(dto.getUserId());
        entity.setTotalAmount(dto.getTotalAmount());
        entity.setDeliveryAddress(dto.getDeliveryAddress());
        entity.setDeliveryInstructions(dto.getDeliveryInstructions());
        entity.setStatus(dto.getStatus());
        entity.setPaymentStatus(dto.getPaymentStatus());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setPaymentTransactionId(dto.getPaymentTransactionId());
        entity.setEstimatedDeliveryTime(dto.getEstimatedDeliveryTime());
        entity.setActualDeliveryTime(dto.getActualDeliveryTime());

        // 转换订单项
        if (dto.getOrderItems() != null) {
            List<OrderItemEntity> itemEntities = dto.getOrderItems().stream()
                    .map(itemDTO -> convertItemToEntity(itemDTO, entity))
                    .collect(Collectors.toList());
            entity.setOrderItems(itemEntities);
        }

        return entity;
    }

    /**
     * 将OrderItemDTO转换为OrderItemEntity
     */
    private OrderItemEntity convertItemToEntity(OrderItemDTO dto, OrderEntity order) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(dto.getId());
        entity.setOrderId(order.getId());
        entity.setFoodItemId(dto.getFoodItemId());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setSpecialInstructions(dto.getSpecialInstructions());
        return entity;
    }
}