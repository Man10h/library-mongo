package com.web.Mongo.service.impl;

import com.mongodb.client.vault.ClientEncryption;
import com.web.Mongo.model.collection.RefreshToken;
import com.web.Mongo.model.collection.Role;
import com.web.Mongo.model.collection.User;
import com.web.Mongo.model.collection.Verify;
import com.web.Mongo.model.dto.UserLoginDTO;
import com.web.Mongo.model.dto.UserRegisterDTO;
import com.web.Mongo.repository.RefreshTokenRepository;
import com.web.Mongo.service.AuthenticationService;
import com.web.Mongo.service.MailService;
import com.web.Mongo.service.TokenService;
import com.web.Mongo.util.EncryptionUtil;
import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    // Sua verify
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Autowired
    private ClientEncryption clientEncryption;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public String register(UserRegisterDTO userRegisterDTO) {
        Role role = mongoTemplate.findOne(new Query(where("name").is("USER")), Role.class);
        User usernameUser = mongoTemplate
                .findOne(new Query(where("username").is(userRegisterDTO.getUsername())), User.class);
        if(usernameUser != null){
            return "username is in use";
        }
        User emailUser = mongoTemplate
                .findOne(new Query(where("email").is(userRegisterDTO.getEmail())), User.class);
        if(emailUser != null){
            return "email is in use";
        }
        String code = generateVerificationCode();
        // Create Verify
        Verify verify = Verify.builder()
                .verificationCodeExpiration((new Date(new Date().getTime() + 1000 * 60 * 60)))
                .verificationCode(code)
                .name(userRegisterDTO.getUsername())
                .build();


        // Creat new User
        User user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(passwordEncoder.encode(userRegisterDTO.getPassword()))
                .refreshTokens(new ArrayList<>())
                .enabled(false)
                .email(userRegisterDTO.getEmail())
                .role(role)
                .myFavouriteBooks(new ArrayList<>())
                .verify(verify)
                .build();

        // generate DEKs
        generateDEKs(user);
        mongoTemplate.save(user);

        // Send VerificationCode
        String email = userRegisterDTO.getEmail();
        String username = userRegisterDTO.getUsername();
        sendVerificationCode(email, code, username);

        return "success";
    }




    @Override
    public String login(UserLoginDTO userLoginDTO) {
        User user = mongoTemplate
                .findOne(new Query(where("username").is(userLoginDTO.getUsername())), User.class);
        if(user == null){
            throw new UsernameNotFoundException("username not found");
        }
        if(!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())
            && !user.getEnabled()){
            return "wrong username or password";
        }
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword(), user.getAuthorities());
        authenticationManager.authenticate(authentication);
        // RefreshToken
        mongoTemplate
                .findAllAndRemove(new Query(where("userId").is((user.getId()))), RefreshToken.class);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenService.generateRefreshToken(user))
                .userId((user.getId()))
                .build();
        user.getRefreshTokens().add(refreshToken);
        mongoTemplate.save(refreshToken);
        mongoTemplate.save(user);

        return tokenService.generateToken(user);
    }

    @Override
    public String verify(String email, String verificationCode) {
        User user = mongoTemplate
                .findOne(new Query(where("email").is(email)), User.class);
        if (user == null){
            throw new UsernameNotFoundException("user not found");
        }
        if(user.getEnabled()){
            return "user already verified";
        }
        if(!user.getVerify().getVerificationCode().equals(verificationCode) || new Date(new Date().getTime()).after(user.getVerify().getVerificationCodeExpiration())){
            System.out.println(user.getVerify().getVerificationCode());
            return "failed to verify";
        }
        user.getVerify().setVerificationCode(null);
        user.getVerify().setVerificationCodeExpiration(null);
        user.setEnabled(true);
        mongoTemplate.save(user);
        return "success";
    }

    @Override
    public String resend(String email) {
        User user = mongoTemplate
                .findOne(new Query(where("email").is(email)), User.class);
        if(user == null){
            throw new UsernameNotFoundException("user not found");
        };
        String username = user.getUsername();
        if(user.getEnabled()){
            return "user already in use";
        }
        String newCode = generateVerificationCode();
        user.getVerify().setVerificationCode(newCode);
        user.getVerify().setVerificationCodeExpiration(new Date(new Date().getTime() + 1000 * 60 * 60));
        mongoTemplate.save(user);
        sendVerificationCode(email, newCode, username);
        return "check your email";
    }

    @Override
    public String forgotPassword(String email) {
        User user = mongoTemplate.findOne(new Query(where("email").is(email)), User.class);
        if(user == null){
            throw new UsernameNotFoundException("user not found");
        }
        if(!user.getEnabled()){
            return "user not enabled";
        }
        String newPassword = generateVerificationCode();
        user.setPassword(passwordEncoder.encode(newPassword));
        mongoTemplate.save(user);
        sendNewPassword(email, newPassword, user.getUsername());
        return "check your email";
    }

    @Override
    public String test() {
        User user = mongoTemplate.findOne(new Query(where("name").is("nguyenmanhlc15")), User.class);
        return user.getRefreshTokens().get(0).getToken();
    }

    public void sendVerificationCode(String email, String code, String username) {
        String html = "<html>Verification Code: " + code + "</html>";
        try{
            mailService.sendVerificationCode(email, username, html);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNewPassword(String email, String newPassword, String username) {
        String html = "<html>New Password: " + newPassword + "</html>";
        try{
            mailService.sendVerificationCode(email, username, html);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String generateVerificationCode() {
        Random random = new Random();
        return random.nextInt(10000) + "";
    }

    public void generateDEKs(User user){
        clientEncryption.createDataKey("local", encryptionUtil.keyAltName(user.getVerify().getName()));
    }
}
