package com.web.Mongo.service.impl;

import com.cloudinary.Cloudinary;
import com.web.Mongo.exception.ex.BookNotFoundException;
import com.web.Mongo.model.collection.Book;
import com.web.Mongo.model.collection.File;
import com.web.Mongo.model.collection.Image;
import com.web.Mongo.model.dto.BookDTO;
import com.web.Mongo.repository.FileRepository;
import com.web.Mongo.service.BookService;
import com.web.Mongo.service.CloudinaryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@EnableCaching
public class BookServiceImpl implements BookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CloudinaryService cloudinaryService;


    @Cacheable(value = "book", key = "#bookDTO.toString()")
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
        if(bookDTO.getId() != null){
            return "fail to create";
        }
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
                    .bookId(new ObjectId(book.getId()))
                    .build();
            mongoTemplate.insert(file);
//            files.add(file);
        }
        for(MultipartFile multipartFile: bookDTO.getImages()) {
            Map res = cloudinaryService.upload(multipartFile);
            Image image = Image.builder()
                    .url((String) res.get("url"))
                    .publicId((String) res.get("public_id"))
                    .name((String) res.get("original_filename"))
                    .bookId(new ObjectId(book.getId()))
                    .build();
            mongoTemplate.insert(image);
//            images.add(image);
        }
        mongoTemplate.save(book);
        return "created successfully";
    }

    @CacheEvict(value = "book", key = "#bookDTO.toString()")
    @Override
    public String update(BookDTO bookDTO) {
        if(bookDTO.getId() == null){
            return "fail to update";
        }
        Book book = mongoTemplate.findById(bookDTO.getId(), Book.class);
        if(book == null){
            throw new BookNotFoundException("book not found");
        }
        if(bookDTO.getName() != null){
            book.setName(bookDTO.getName());
        }
        if(bookDTO.getName() != null){
            book.setTitle(bookDTO.getTitle());
        }
        if(bookDTO.getName() != null){
            book.setDescription(bookDTO.getDescription());
        }
        if(!bookDTO.getTypes().isEmpty()){
            book.setType(String.join(",", bookDTO.getTypes()));
        }
        mongoTemplate.save(book);
        return "update successfully";
    }

    @Override
    public String deleteById(String id) {
        Book book = mongoTemplate.findById(id, Book.class);
        if(book == null){
            throw new BookNotFoundException("book not found");
        }
        return delete(book);
    }

    @CacheEvict(value = "book", allEntries = true)
    public String delete(Book book) {
        mongoTemplate.remove(new Query(where("bookId").is(book.getId())), Image.class);
        mongoTemplate.remove(new Query(where("bookId").is(book.getId())), File.class);
        mongoTemplate.remove(book);
        return "delete successfully";
    }


}
