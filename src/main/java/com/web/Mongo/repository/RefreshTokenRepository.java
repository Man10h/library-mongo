package com.web.Mongo.repository;

import com.web.Mongo.model.collection.RefreshToken;
import com.web.Mongo.model.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

}
