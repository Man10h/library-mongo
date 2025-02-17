package com.web.Mongo.model.collection;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.ExplicitEncrypted;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "verify")
public class Verify {
    @Id
    private String id;

    @ExplicitEncrypted(algorithm = "AEAD_AES_256_CBC_HMAC_SHA_512-Random", keyAltName = "/name")
    private String verificationCode;

    private String name;
    private Date verificationCodeExpiration;
}
