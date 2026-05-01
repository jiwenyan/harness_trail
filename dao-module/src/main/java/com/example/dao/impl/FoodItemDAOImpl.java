package com.example.dao.impl;

import com.example.dao.interfaces.FoodItemDAO;
import com.example.data.entity.FoodItemEntity;
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
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class FoodItemDAOImpl implements FoodItemDAO {

    private static final String COLLECTION_NAME = "food_items";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public FoodItemDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<FoodItemEntity> findById(Long id) {
        return Optional.ofNullable(mongoTemplate.findById(id, FoodItemEntity.class, COLLECTION_NAME));
    }

    @Override
    public FoodItemEntity save(FoodItemEntity foodItem) {
        if (foodItem.getId() == null) {
            foodItem.setId(generateSequenceId(COLLECTION_NAME));
        }
        LocalDateTime now = LocalDateTime.now();
        foodItem.setCreatedAt(now);
        foodItem.setUpdatedAt(now);
        return mongoTemplate.save(foodItem, COLLECTION_NAME);
    }

    @Override
    public FoodItemEntity update(FoodItemEntity foodItem) {
        foodItem.setUpdatedAt(LocalDateTime.now());
        return mongoTemplate.save(foodItem, COLLECTION_NAME);
    }

    @Override
    public void deleteById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }

    @Override
    public Page<FoodItemEntity> findAll(Pageable pageable) {
        Query query = new Query().with(pageable);
        List<FoodItemEntity> list = mongoTemplate.find(query, FoodItemEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(), FoodItemEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<FoodItemEntity> findByCategory(String category) {
        Query query = new Query(Criteria.where("category").is(category));
        return mongoTemplate.find(query, FoodItemEntity.class, COLLECTION_NAME);
    }

    @Override
    public Page<FoodItemEntity> findByCategory(String category, Pageable pageable) {
        Query query = new Query(Criteria.where("category").is(category)).with(pageable);
        List<FoodItemEntity> list = mongoTemplate.find(query, FoodItemEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(Criteria.where("category").is(category)), FoodItemEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<FoodItemEntity> search(String keyword) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("name").regex(keyword, "i"),
                Criteria.where("description").regex(keyword, "i")
        );
        Query query = new Query(criteria);
        return mongoTemplate.find(query, FoodItemEntity.class, COLLECTION_NAME);
    }

    @Override
    public List<FoodItemEntity> findAvailableItems() {
        Query query = new Query(Criteria.where("is_available").is(true));
        return mongoTemplate.find(query, FoodItemEntity.class, COLLECTION_NAME);
    }

    @Override
    public Page<FoodItemEntity> findAvailableItems(Pageable pageable) {
        Query query = new Query(Criteria.where("is_available").is(true)).with(pageable);
        List<FoodItemEntity> list = mongoTemplate.find(query, FoodItemEntity.class, COLLECTION_NAME);
        long total = mongoTemplate.count(new Query(Criteria.where("is_available").is(true)), FoodItemEntity.class, COLLECTION_NAME);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public void updateStock(Long foodItemId, Integer quantity) {
        Query query = new Query(Criteria.where("_id").is(foodItemId));
        Update update = new Update().set("stock_quantity", quantity);
        mongoTemplate.updateFirst(query, update, FoodItemEntity.class, COLLECTION_NAME);
    }

    @Override
    public boolean existsById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, FoodItemEntity.class, COLLECTION_NAME);
    }

    @Override
    public List<String> findAllCategories() {
        return mongoTemplate.findDistinct(new Query(), "category", COLLECTION_NAME, String.class);
    }

    private Long generateSequenceId(String collectionName) {
        Query query = new Query(Criteria.where("_id").is(collectionName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "database_sequences");
        return counter != null ? counter.getLong("seq") : 1L;
    }
}
