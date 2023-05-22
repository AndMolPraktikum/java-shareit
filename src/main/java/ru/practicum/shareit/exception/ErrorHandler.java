package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleThrowable(final Throwable e) {
        log.info("500 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info("400 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleUserNotFoundException(final UserNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleUserHaveNoSuchItemException(final UserHaveNoSuchItemException e) {
        log.info("404 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response handleUserAlreadyExistException(final UserAlreadyExistException e) {
        log.info("409 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleItemNotFoundException(final ItemNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }
}
