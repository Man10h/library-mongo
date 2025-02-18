package com.web.Mongo.service;

import com.web.Mongo.model.collection.User;

public interface TokenService {
    public String generateToken(User user);
    public String generateRefreshToken(User user);
    public String getUsername(String token);
    public boolean validateRefreshToken(String refreshToken);
}
