package ru.practicum.shareit.exception;

public class NoCompletedBookingsException extends RuntimeException {

    public NoCompletedBookingsException() {
        super();
    }

    public NoCompletedBookingsException(String message) {
        super(message);
    }

    public NoCompletedBookingsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCompletedBookingsException(Throwable cause) {
        super(cause);
    }

    protected NoCompletedBookingsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
