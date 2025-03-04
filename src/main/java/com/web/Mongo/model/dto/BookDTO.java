package com.web.Mongo.model.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private String id;
    private String name;
    private String title;
    private String author;
    private String description;
    private List<String> types;
    private List<MultipartFile> images;
    private List<MultipartFile> files;

    public String toString(){
        return id + name + title + author + description;
    }
}
