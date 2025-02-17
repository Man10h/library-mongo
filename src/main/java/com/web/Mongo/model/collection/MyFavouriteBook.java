package com.web.Mongo.model.collection;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "myFavouriteBook")
public class MyFavouriteBook {
    @Id
    private String id;

    @DocumentReference
    private User user;

    @DocumentReference
    private Book book;
}
