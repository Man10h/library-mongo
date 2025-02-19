package com.web.Mongo.service;

import com.web.Mongo.model.collection.Book;
import com.web.Mongo.model.collection.User;
import com.web.Mongo.model.dto.MyFavouriteDTO;

import java.util.List;

public interface UserService {
    public User userInfo(String token);
    public String refreshToken(String token);

    public List<Book> getMyFavourite(String userId);
    public String addToMyFavourite(MyFavouriteDTO myFavouriteDTO);
    public String deleteFromMyFavourite(MyFavouriteDTO myFavouriteDTO);
}
