package com.web.Mongo.service;

import com.web.Mongo.model.collection.Book;
import com.web.Mongo.model.dto.BookDTO;

import java.util.List;

public interface BookService {
    public List<Book> find(BookDTO bookDTO);
    public Book findById(String id);

    public String create(BookDTO bookDTO);
    public String update(BookDTO bookDTO);
    public String delete(String id);
}
