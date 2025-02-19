package com.web.Mongo.controller;

import com.web.Mongo.model.collection.Book;
import com.web.Mongo.model.dto.MyFavouriteDTO;
import com.web.Mongo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    /*
     /myFavourite post
     /myFavourite delete
     /myFavourite get
    */

    @Autowired
    private UserService userService;

    @GetMapping("/myFavourite")
    public ResponseEntity<List<Book>> getMyFavourite(@RequestParam(name = "id") String id) {
        return ResponseEntity.ok(userService.getMyFavourite(id));
    }

    @PostMapping("/myFavourite")
    public ResponseEntity<String> addMyFavourite(@RequestBody MyFavouriteDTO myFavouriteDTO) {
        return ResponseEntity.ok(userService.addToMyFavourite(myFavouriteDTO));
    }

    @DeleteMapping("/myFavourite")
    public ResponseEntity<String> deleteMyFavourite(@RequestBody MyFavouriteDTO myFavouriteDTO) {
        return ResponseEntity.ok(userService.deleteFromMyFavourite(myFavouriteDTO));
    }

}
