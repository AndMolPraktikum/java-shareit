package ru.practicum.shareit.exception;

public class BookingWrongTimeException extends RuntimeException {

    public BookingWrongTimeException() {
        super();
    }

    public BookingWrongTimeException(String message) {
        super(message);
    }

    public BookingWrongTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingWrongTimeException(Throwable cause) {
        super(cause);
    }

    protected BookingWrongTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
