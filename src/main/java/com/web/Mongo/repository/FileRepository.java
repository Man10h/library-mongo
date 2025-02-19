package com.web.Mongo.repository;

import com.web.Mongo.model.collection.File;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FileRepository extends MongoRepository<File, String> {
    void deleteByBookId(String bookId);
}
