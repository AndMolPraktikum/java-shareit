package ru.practicum.shareit.exception;

public class BookingWrongTimeException extends RuntimeException {

    public BookingWrongTimeException(String message) {
        super(message);
    }
}
