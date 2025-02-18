package com.web.Mongo.service.impl;

import com.web.Mongo.exception.ex.BookNotFoundException;
import com.web.Mongo.model.collection.Book;
import com.web.Mongo.model.dto.BookDTO;
import com.web.Mongo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Transactional(readOnly = true)
    public List<Book> find(BookDTO bookDTO) {
        Criteria criteria = new Criteria();
        if (bookDTO.getTitle() != null && !bookDTO.getTitle().isEmpty()) {
            criteria.and("title").regex(bookDTO.getTitle(), "i");
        }
        if (bookDTO.getAuthor() != null && !bookDTO.getAuthor().isEmpty()) {
            criteria.and("author").regex(bookDTO.getAuthor(), "i");
        }
        if (bookDTO.getName() != null && !bookDTO.getName().isEmpty()) {
            criteria.and("name").regex(bookDTO.getName(), "i");
        }
        if (bookDTO.getType() != null && !bookDTO.getType().isEmpty()) {
            criteria.and("type").in(bookDTO.getType());
        }
        TypedAggregation<Book> aggregation = Aggregation.newAggregation(
                Book.class,
                Aggregation.match(criteria)
        );

        return mongoTemplate.aggregate(aggregation, "book", Book.class).getMappedResults();
    }

    @Transactional(readOnly = true)
    public Book findById(String id) {
        Book book = mongoTemplate.findById(id, Book.class);
        if (book == null) {
            throw new BookNotFoundException("book not found");
        }
        return book;
    }

}
