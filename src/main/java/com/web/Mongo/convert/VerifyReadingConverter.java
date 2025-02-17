package com.web.Mongo.convert;

import com.web.Mongo.model.collection.Verify;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;


@ReadingConverter
public class VerifyReadingConverter implements Converter<Document, Verify> {

    @Override
    public Verify convert(Document source) {
        return Verify.builder()
                .id(source.getObjectId("_id").toString())
                .verificationCode(source.getString("verificationCode"))
                .verificationCodeExpiration(source.getDate("verificationCodeExpiration"))
                .build();
    }
}
