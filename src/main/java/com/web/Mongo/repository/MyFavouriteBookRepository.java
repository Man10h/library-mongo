package com.web.Mongo.repository;

import com.web.Mongo.model.collection.MyFavouriteBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyFavouriteBookRepository extends MongoRepository<MyFavouriteBook, String> {
    List<MyFavouriteBook> findByUser_Id(String userId);
}
