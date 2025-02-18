package com.web.Mongo.service;

import com.web.Mongo.model.dto.UserLoginDTO;
import com.web.Mongo.model.dto.UserRegisterDTO;

public interface AuthenticationService {
    public String register(UserRegisterDTO userRegisterDTO);
    public String login(UserLoginDTO userLoginDTO);
    public String verify(String email, String verificationCode);
    public String resend(String email);
    public String forgotPassword(String email);

    public String test();
}
