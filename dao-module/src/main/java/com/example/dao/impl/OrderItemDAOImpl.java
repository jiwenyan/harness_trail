package com.example.dao.impl;

import com.example.dao.interfaces.OrderItemDAO;
import com.example.data.entity.OrderItemEntity;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrderItemDAOImpl implements OrderItemDAO {

    private static final String COLLECTION_NAME = "order_items";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public OrderItemDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Long generateSequenceId(String collectionName) {
        Query query = new Query(Criteria.where("_id").is(collectionName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "database_sequences");
        return counter != null ? counter.getLong("seq") : 1L;
    }

    @Override
    public Optional<OrderItemEntity> findById(Long id) {
        return Optional.ofNullable(mongoTemplate.findById(id, OrderItemEntity.class, COLLECTION_NAME));
    }

    @Override
    public OrderItemEntity save(OrderItemEntity orderItem) {
        if (orderItem.getId() == null) {
            orderItem.setId(generateSequenceId(COLLECTION_NAME));
        }
        return mongoTemplate.save(orderItem, COLLECTION_NAME);
    }

    @Override
    public List<OrderItemEntity> saveAll(List<OrderItemEntity> orderItems) {
        for (OrderItemEntity orderItem : orderItems) {
            if (orderItem.getId() == null) {
                orderItem.setId(generateSequenceId(COLLECTION_NAME));
            }
            mongoTemplate.save(orderItem, COLLECTION_NAME);
        }
        return orderItems;
    }

    @Override
    public OrderItemEntity update(OrderItemEntity orderItem) {
        return mongoTemplate.save(orderItem, COLLECTION_NAME);
    }

    @Override
    public void deleteById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }

    @Override
    public List<OrderItemEntity> findByOrderId(Long orderId) {
        Query query = new Query(Criteria.where("order_id").is(orderId));
        return mongoTemplate.find(query, OrderItemEntity.class, COLLECTION_NAME);
    }

    @Override
    public List<OrderItemEntity> findByFoodItemId(Long foodItemId) {
        Query query = new Query(Criteria.where("food_item_id").is(foodItemId));
        return mongoTemplate.find(query, OrderItemEntity.class, COLLECTION_NAME);
    }

    @Override
    public void deleteByOrderId(Long orderId) {
        Query query = new Query(Criteria.where("order_id").is(orderId));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }

    @Override
    public boolean existsById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, COLLECTION_NAME);
    }
}
