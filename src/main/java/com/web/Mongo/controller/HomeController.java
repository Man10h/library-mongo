package com.web.Mongo.controller;

import com.web.Mongo.model.collection.User;
import com.web.Mongo.model.dto.UserLoginDTO;
import com.web.Mongo.model.dto.UserRegisterDTO;
import com.web.Mongo.service.AuthenticationService;
import com.web.Mongo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/home")
@RestController
public class HomeController {

    /*
    /login
    /register
    /resend
    /verify
    /refreshToken
    /forgotPassword
    /book?find
    /book?id
     */

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !userRegisterDTO.getRetype().equals(userRegisterDTO.getPassword())) {
            return ResponseEntity.ok("invalid username or password");
        }
        return ResponseEntity.ok(authenticationService.register(userRegisterDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginDTO userLoginDTO,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.ok("invalid username or password");
        }
        return ResponseEntity.ok(authenticationService.login(userLoginDTO));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam(name = "email") String email,
                                         @RequestParam(name = "code") String code) {
        return ResponseEntity.ok(authenticationService.verify(email, code));
    }

    @GetMapping("/resend")
    public ResponseEntity<String> resend(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(authenticationService.resend(email));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok(authenticationService.test());
    }

    @GetMapping("/userInfo")
    public ResponseEntity<User> userInfo(@RequestParam(name = "token") String token){
        return ResponseEntity.ok(userService.userInfo(token));
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(@RequestParam(name = "token") String token){
        return ResponseEntity.ok(userService.refreshToken(token));
    }
}
