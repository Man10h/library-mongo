package com.web.Mongo.service;

import com.web.Mongo.model.collection.User;

public interface UserService {
    public User userInfo(String token);
    public String refreshToken(String token);
}
