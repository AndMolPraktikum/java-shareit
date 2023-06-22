package ru.practicum.shareit.comments;

import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.comments.dto.CommentResponse;

public interface CommentService {
    CommentResponse create(long itemId, long userId, CommentRequest commentRequest);
}
