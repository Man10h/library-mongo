package com.web.Mongo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    @JsonProperty(value = "username")
    @NotBlank(message = "required")
    private String username;

    @JsonProperty(value = "password")
    @NotBlank(message = "required")
    private String password;

    @JsonProperty(value = "retype")
    @NotBlank(message = "required")
    private String retype;

    @JsonProperty(value = "email")
    @NotBlank(message = "required")
    private String email;

}
