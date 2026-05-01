package com.example.dao.impl;

import com.example.dao.interfaces.UserDAO;
import com.example.data.entity.UserEntity;
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
public class UserDAOImpl implements UserDAO {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserDAOImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        UserEntity user = mongoTemplate.findById(id, UserEntity.class, "users");
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return Optional.ofNullable(mongoTemplate.findOne(query, UserEntity.class, "users"));
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return Optional.ofNullable(mongoTemplate.findOne(query, UserEntity.class, "users"));
    }

    @Override
    public Optional<UserEntity> findByPhone(String phone) {
        Query query = new Query(Criteria.where("phone").is(phone));
        return Optional.ofNullable(mongoTemplate.findOne(query, UserEntity.class, "users"));
    }

    @Override
    public UserEntity save(UserEntity user) {
        if (user.getId() == null) {
            user.setId(generateSequenceId("users"));
        }
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return mongoTemplate.save(user, "users");
    }

    @Override
    public UserEntity update(UserEntity user) {
        user.setUpdatedAt(LocalDateTime.now());
        return mongoTemplate.save(user, "users");
    }

    @Override
    public void deleteById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, "users");
    }

    @Override
    public boolean existsByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.exists(query, UserEntity.class, "users");
    }

    @Override
    public boolean existsByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.exists(query, UserEntity.class, "users");
    }

    @Override
    public boolean existsByPhone(String phone) {
        Query query = new Query(Criteria.where("phone").is(phone));
        return mongoTemplate.exists(query, UserEntity.class, "users");
    }

    @Override
    public Page<UserEntity> findAll(Pageable pageable) {
        Query query = new Query().with(pageable);
        List<UserEntity> list = mongoTemplate.find(query, UserEntity.class, "users");
        long total = mongoTemplate.count(new Query(), UserEntity.class, "users");
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<UserEntity> searchByUsername(String username) {
        Query query = new Query(Criteria.where("username").regex(username, "i"));
        return mongoTemplate.find(query, UserEntity.class, "users");
    }

    private Long generateSequenceId(String collectionName) {
        Query query = new Query(Criteria.where("_id").is(collectionName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        Document counter = mongoTemplate.findAndModify(query, update, options, Document.class, "database_sequences");
        return counter != null ? counter.getLong("seq") : 1L;
    }
}
