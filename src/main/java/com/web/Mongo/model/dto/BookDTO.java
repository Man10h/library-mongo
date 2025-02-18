package com.web.Mongo.model.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private String name;
    private String title;
    private String author;
    private String description;
    private List<String> type;
    private List<MultipartFile> images;
    private List<MultipartFile> files;
}
