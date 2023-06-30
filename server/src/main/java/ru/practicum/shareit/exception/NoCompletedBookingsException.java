package ru.practicum.shareit.exception;

public class NoCompletedBookingsException extends RuntimeException {

    public NoCompletedBookingsException(String message) {
        super(message);
    }
}
