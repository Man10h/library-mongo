package com.web.Mongo.exception;

import com.web.Mongo.exception.ex.UserNotFoundException;
import com.web.Mongo.model.dto.ErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessageDTO userNotFound(UserNotFoundException e, WebRequest request) {
        return new ErrorMessageDTO(e.getMessage(), 10100L);
    }
}
