package com.web.Mongo.controller;

import com.web.Mongo.model.dto.BookDTO;
import com.web.Mongo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    /*
     /create Post
     /edit Post
     /delete Delete
    */

    @Autowired
    private BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@ModelAttribute BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.create(bookDTO));
    }
}
