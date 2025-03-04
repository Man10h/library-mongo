package com.web.Mongo.model.collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "file")
@JsonIgnoreProperties(value = {"bookId"})
public class File {
    @Id
    private String id;

    private String url;
    private String name;
    private String publicId;

    @Field(name = "bookId")
    private ObjectId bookId;
}
