package com.web.Mongo.util;

import com.web.Mongo.exception.ex.UserNotFoundException;
import com.web.Mongo.model.collection.User;
import com.web.Mongo.repository.UserRepository;
import com.web.Mongo.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class MyCustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = defaultOAuth2User.getAttribute("email");
        Optional<User> optional =userRepository.findByEmail(email);
        if (optional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        User user = optional.get();
        String token = tokenService.generateToken(user);
        response.setContentType("application/json");
        response.getWriter().write("{\"accessToken\": \"" + token + "\"}");
        response.getWriter().flush();
    }
}
