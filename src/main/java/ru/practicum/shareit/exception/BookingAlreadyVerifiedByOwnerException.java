package ru.practicum.shareit.exception;

public class BookingAlreadyVerifiedByOwnerException extends RuntimeException {

    public BookingAlreadyVerifiedByOwnerException(String message) {
        super(message);
    }

}
