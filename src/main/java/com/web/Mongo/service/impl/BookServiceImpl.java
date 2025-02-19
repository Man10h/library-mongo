package com.web.Mongo.service.impl;

import com.cloudinary.Cloudinary;
import com.web.Mongo.exception.ex.BookNotFoundException;
import com.web.Mongo.model.collection.Book;
import com.web.Mongo.model.collection.File;
import com.web.Mongo.model.collection.Image;
import com.web.Mongo.model.dto.BookDTO;
import com.web.Mongo.service.BookService;
import com.web.Mongo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CloudinaryService cloudinaryService;

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
        if (bookDTO.getTypes() != null && !bookDTO.getTypes().isEmpty()) {
            criteria.and("type").in(bookDTO.getTypes());
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

    @Override
    public String create(BookDTO bookDTO) {
        List<String> types = bookDTO.getTypes();
        String type = String.join(",", types);
        Book book = Book.builder()
                .name(bookDTO.getName())
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .description(bookDTO.getDescription())
                .type(type)
                .myFavouriteBooks(new ArrayList<>())
                .images(new ArrayList<>())
                .files(new ArrayList<>())
                .like(0L)
                .build();
        mongoTemplate.insert(book);
        List<File> files = book.getFiles();
        List<Image> images = book.getImages();
        for(MultipartFile multipartFile: bookDTO.getFiles()) {
            Map res = cloudinaryService.upload(multipartFile);
            File file = File.builder()
                    .url((String) res.get("url"))
                    .publicId((String) res.get("public_id"))
                    .name((String) res.get("original_filename"))
                    .bookId(book.getId())
                    .build();
            mongoTemplate.insert(file);
            files.add(file);
        }
        for(MultipartFile multipartFile: bookDTO.getImages()) {
            Map res = cloudinaryService.upload(multipartFile);
            Image image = Image.builder()
                    .url((String) res.get("url"))
                    .publicId((String) res.get("public_id"))
                    .name((String) res.get("original_filename"))
                    .bookId(book.getId())
                    .build();
            mongoTemplate.insert(image);
            images.add(image);
        }
        book.setFiles(files);
        book.setImages(images);
        mongoTemplate.save(book);
        return "created successfully";
    }

}
