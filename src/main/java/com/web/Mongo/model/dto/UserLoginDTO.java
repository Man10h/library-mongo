package com.web.Mongo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @JsonProperty(value = "username")
    @NotBlank(message = "required")
    private String username;

    @JsonProperty(value = "password")
    @NotBlank(message = "required")
    private String password;
}
