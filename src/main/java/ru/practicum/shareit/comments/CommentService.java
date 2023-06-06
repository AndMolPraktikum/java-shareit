package ru.practicum.shareit.comments;

import ru.practicum.shareit.comments.model.Comment;

public interface CommentService {
    Comment create(long itemId, long userId, Comment comment);
}
