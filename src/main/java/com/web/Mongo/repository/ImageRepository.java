package com.web.Mongo.repository;

import com.web.Mongo.model.collection.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, String> {
    void deleteByBookId(String bookId);
}
