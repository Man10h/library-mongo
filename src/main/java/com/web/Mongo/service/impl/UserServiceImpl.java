package com.web.Mongo.service.impl;

import com.web.Mongo.model.collection.User;
import com.web.Mongo.repository.UserRepository;
import com.web.Mongo.service.TokenService;
import com.web.Mongo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public User userInfo(String token) {
        String username = tokenService.getUsername(token);
        if(username == null){
            return null;
        }
        return mongoTemplate.findOne(new Query(where("username").is(username)), User.class);
    }
}
