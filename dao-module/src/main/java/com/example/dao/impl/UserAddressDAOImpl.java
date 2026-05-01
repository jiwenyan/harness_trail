package com.example.dao.impl;

import com.example.dao.interfaces.UserAddressDAO;
import com.example.data.entity.UserAddressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

/**
 * 用户地址数据访问实现
 */
@Repository
public class UserAddressDAOImpl implements UserAddressDAO {

    private static final String COLLECTION_NAME = "user_addresses";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserAddressDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<UserAddressEntity> findById(Long id) {
        UserAddressEntity address = mongoTemplate.findById(id, UserAddressEntity.class, COLLECTION_NAME);
        return Optional.ofNullable(address);
    }

    @Override
    public List<UserAddressEntity> findByUserId(Long userId) {
        Query query = new Query(Criteria.where("user_id").is(userId));
        return mongoTemplate.find(query, UserAddressEntity.class, COLLECTION_NAME);
    }

    @Override
    public UserAddressEntity save(UserAddressEntity address) {
        if (address.getId() == null) {
            address.setId(generateSequenceId(COLLECTION_NAME));
        }
        return mongoTemplate.save(address, COLLECTION_NAME);
    }

    @Override
    public UserAddressEntity update(UserAddressEntity address) {
        return mongoTemplate.save(address, COLLECTION_NAME);
    }

    @Override
    public void deleteById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }

    @Override
    public boolean existsById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, UserAddressEntity.class, COLLECTION_NAME);
    }

    private Long generateSequenceId(String collectionName) {
        Query query = new Query(Criteria.where("_id").is(collectionName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "database_sequences");
        return counter != null ? counter.getLong("seq") : 1L;
    }
}
