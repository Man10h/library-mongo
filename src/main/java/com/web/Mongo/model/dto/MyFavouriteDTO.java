package com.web.Mongo.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyFavouriteDTO {
    public String userId;
    public String bookId;
}
