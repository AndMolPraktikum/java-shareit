package ru.practicum.shareit.exception;

public class FailStateException extends RuntimeException {

    public FailStateException() {
    }

    public FailStateException(String message) {
        super(message);
    }

    public FailStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailStateException(Throwable cause) {
        super(cause);
    }

    public FailStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
