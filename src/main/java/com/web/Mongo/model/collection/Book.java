package com.web.Mongo.model.collection;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "book")
public class Book {
    @Id
    private String id;

    private String name;
    private String title;
    private String type;
    private String author;
    private String description;
    private Long like;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'bookId': ?#{#self._id}}")
    private List<Image> images;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'bookId': ?#{#self._id}}")
    private List<File> files;

    @DocumentReference(lazy = true)
    private List<MyFavouriteBook> myFavouriteBooks;

    @Version
    private Long version;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
