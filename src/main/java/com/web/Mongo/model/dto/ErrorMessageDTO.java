package com.web.Mongo.model.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageDTO {
    private String message;
    private Long code;
}
