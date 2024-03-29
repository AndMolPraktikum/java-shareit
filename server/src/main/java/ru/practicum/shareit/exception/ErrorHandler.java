package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {



    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleItemIsUnavailableException(final ItemIsUnavailableException e) {
        log.info("400 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleBookingWrongTimeException(final BookingWrongTimeException e) {
        log.info("400 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleBookingAlreadyVerifiedByOwnerException(final BookingAlreadyVerifiedByOwnerException e) {
        log.info("400 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleNoCompletedBookingsException(final NoCompletedBookingsException e) {
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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleItemNotFoundException(final ItemNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleBookingNotFoundException(final BookingNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleUserHasNoLinkBookingOrItemException(final UserHasNoLinkBookingOrItemException e) {
        log.info("404 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleItemRequestNotFoundException(final ItemRequestNotFoundException e) {
        log.info("404 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleFailStateException(final FailStateException e) {
        log.info("500 {}", e.getMessage(), e);
        return new ErrorMessage(String.format("%s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response handleThrowable(final Throwable e) {
        log.info("500 {}", e.getMessage(), e);
        return new Response(String.format("%s %s", LocalDateTime.now(), e.getMessage()));
    }
}
