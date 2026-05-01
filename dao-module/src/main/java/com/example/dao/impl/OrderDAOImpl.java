package com.example.dao.impl;

import com.example.dao.interfaces.OrderDAO;
import com.example.data.entity.OrderEntity;
import com.example.data.enums.OrderStatus;
import com.example.data.enums.PaymentStatus;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问实现
 */
@Repository
public class OrderDAOImpl implements OrderDAO {

    private static final String COLLECTION_NAME = "orders";
    private static final String SEQUENCE_KEY = "orders";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public OrderDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<OrderEntity> findById(Long id) {
        OrderEntity order = mongoTemplate.findById(id, OrderEntity.class, COLLECTION_NAME);
        return Optional.ofNullable(order);
    }

    @Override
    public Optional<OrderEntity> findByOrderNumber(String orderNumber) {
        Query query = new Query(Criteria.where("order_number").is(orderNumber));
        OrderEntity order = mongoTemplate.findOne(query, OrderEntity.class, COLLECTION_NAME);
        return Optional.ofNullable(order);
    }

    @Override
    public OrderEntity save(OrderEntity order) {
        if (order.getId() == null) {
            order.setId(generateSequenceId(SEQUENCE_KEY));
        }
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        return mongoTemplate.save(order, COLLECTION_NAME);
    }

    @Override
    public OrderEntity update(OrderEntity order) {
        order.setUpdatedAt(LocalDateTime.now());
        return mongoTemplate.save(order, COLLECTION_NAME);
    }

    @Override
    public void deleteById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }

    @Override
    public List<OrderEntity> findByUserId(Long userId) {
        Query query = new Query(Criteria.where("user_id").is(userId));
        return mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public Page<OrderEntity> findByUserId(Long userId, Pageable pageable) {
        Query query = new Query(Criteria.where("user_id").is(userId)).with(pageable);
        List<OrderEntity> list = mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(Criteria.where("user_id").is(userId)), OrderEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<OrderEntity> findByStatus(OrderStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable) {
        Query query = new Query(Criteria.where("status").is(status)).with(pageable);
        List<OrderEntity> list = mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(Criteria.where("status").is(status)), OrderEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<OrderEntity> findByPaymentStatus(PaymentStatus paymentStatus) {
        Query query = new Query(Criteria.where("payment_status").is(paymentStatus));
        return mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public Page<OrderEntity> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable) {
        Query query = new Query(Criteria.where("payment_status").is(paymentStatus)).with(pageable);
        List<OrderEntity> list = mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(Criteria.where("payment_status").is(paymentStatus)), OrderEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<OrderEntity> findByUserIdAndStatus(Long userId, OrderStatus status) {
        Query query = new Query(Criteria.where("user_id").is(userId).and("status").is(status));
        return mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public List<OrderEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        Query query = new Query(Criteria.where("created_at").gte(start).lte(end));
        return mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public Page<OrderEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Query query = new Query(Criteria.where("created_at").gte(start).lte(end)).with(pageable);
        List<OrderEntity> list = mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(Criteria.where("created_at").gte(start).lte(end)), OrderEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<OrderEntity> findAll(Pageable pageable) {
        Query query = new Query().with(pageable);
        List<OrderEntity> list = mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(), OrderEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public void updateStatus(Long orderId, OrderStatus status) {
        Query query = new Query(Criteria.where("_id").is(orderId));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(query, update, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Query query = new Query(Criteria.where("_id").is(orderId));
        Update update = new Update().set("payment_status", paymentStatus);
        mongoTemplate.updateFirst(query, update, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public boolean existsById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, OrderEntity.class, COLLECTION_NAME);
    }

    @Override
    public Page<OrderEntity> findByFilters(Long userId, OrderStatus status, PaymentStatus paymentStatus,
                                           LocalDateTime startDate, LocalDateTime endDate,
                                           String keyword, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (userId != null) {
            criteria = criteria.and("user_id").is(userId);
        }
        if (status != null) {
            criteria = criteria.and("status").is(status);
        }
        if (paymentStatus != null) {
            criteria = criteria.and("payment_status").is(paymentStatus);
        }
        if (startDate != null && endDate != null) {
            criteria = criteria.and("created_at").gte(startDate).lte(endDate);
        } else if (startDate != null) {
            criteria = criteria.and("created_at").gte(startDate);
        } else if (endDate != null) {
            criteria = criteria.and("created_at").lte(endDate);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            criteria = criteria.and("order_number").regex(keyword, "i");
        }

        Query query = new Query(criteria).with(pageable);
        List<OrderEntity> list = mongoTemplate.find(query, OrderEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(criteria), OrderEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    /**
     * 生成序列ID
     */
    private Long generateSequenceId(String collectionName) {
        Query query = new Query(Criteria.where("_id").is(collectionName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "database_sequences");
        return counter != null ? counter.getLong("seq") : 1L;
    }
}
