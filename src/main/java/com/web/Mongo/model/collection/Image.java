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
@Document(collection = "image")
public class Image {
    @Id
    private String id;

    private String url;
    private String name;
    private String publicId;

    @DocumentReference
    private Book book;
}
