package com.web.Mongo.service.impl;

import com.web.Mongo.exception.ex.BookNotFoundException;
import com.web.Mongo.exception.ex.UserNotFoundException;
import com.web.Mongo.model.collection.Book;
import com.web.Mongo.model.collection.MyFavouriteBook;
import com.web.Mongo.model.collection.User;
import com.web.Mongo.model.dto.MyFavouriteDTO;
import com.web.Mongo.repository.UserRepository;
import com.web.Mongo.service.TokenService;
import com.web.Mongo.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public String refreshToken(String token) {
        if(!tokenService.validateRefreshToken(token)){
            return "fail to refresh token, pls login again";
        }
        String username = tokenService.getUsername(token);
        User user = mongoTemplate.findOne(new Query(where("username").is(username)), User.class);
        if(user == null){
            throw new UserNotFoundException("User not found");
        }
        return tokenService.generateToken(user);
    }

    @Override
    public List<Book> getMyFavourite(String userId) {
        List<Book> books = new ArrayList<>();
        List<MyFavouriteBook> myFavouriteBooks = mongoTemplate.find(new Query(where("userId").is(new ObjectId(userId))), MyFavouriteBook.class);
        for(MyFavouriteBook myFavouriteBook: myFavouriteBooks){
            Book book = mongoTemplate.findById(myFavouriteBook.getBookId(), Book.class);
            if(book == null){
                throw new BookNotFoundException("Book not found");
            }
            books.add(book);
        }
        return books;
    }

    @Override
    public String addToMyFavourite(MyFavouriteDTO myFavouriteDTO) {
        if(myFavouriteDTO.getBookId() == null || myFavouriteDTO.getBookId().equals("")
        || myFavouriteDTO.getUserId() == null || myFavouriteDTO.getUserId().equals("")
        ){
            return "fail to add to your favourite";
        }
        User user = mongoTemplate.findById(myFavouriteDTO.getUserId(), User.class);
        if(user == null){
            throw new UserNotFoundException("User not found");
        }
        Book book = mongoTemplate.findById(myFavouriteDTO.getBookId(), Book.class);
        if(book == null){
            throw new BookNotFoundException("Book not found");
        }
        MyFavouriteBook myFavouriteBook = MyFavouriteBook.builder()
                .userId(new ObjectId(user.getId()))
                .bookId(new ObjectId(book.getId()))
                .build();
        mongoTemplate.save(myFavouriteBook);
        return "add to your favourite successfully";
    }

    @Override
    public String deleteFromMyFavourite(MyFavouriteDTO myFavouriteDTO) {
        if(myFavouriteDTO.getBookId() == null || myFavouriteDTO.getBookId().equals("")
                || myFavouriteDTO.getUserId() == null || myFavouriteDTO.getUserId().equals("")
        ){
            return "fail to delete";
        }
        mongoTemplate.remove(new Query(where("userId").is(new ObjectId(myFavouriteDTO.getUserId()))
                .and("bookId").is(new ObjectId(myFavouriteDTO.getBookId()))), "myFavouriteBook");
        return "delete successfully";
    }
}
