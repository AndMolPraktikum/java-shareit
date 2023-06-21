package ru.practicum.shareit.exception;

public class UserHaveNoSuchItemException extends RuntimeException {

    public UserHaveNoSuchItemException(String message) {
        super(message);
    }
}
