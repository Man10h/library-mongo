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
@Document(collection = "refreshToken")
public class RefreshToken {
    @Id
    private String id;

    private String token;

    @DocumentReference
    private User user;
}
