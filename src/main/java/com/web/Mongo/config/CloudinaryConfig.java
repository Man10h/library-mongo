package com.web.Mongo.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary getCloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", "djuq2enmy");
        config.put("api_key", "829565713471837");
        config.put("api_secret", "oHpiWWc8qqRFZUkj-kBtXhsVcJ8");
        config.put("secure", true);
        return new Cloudinary(config);
    }
}
