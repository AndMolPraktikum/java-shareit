package ru.practicum.shareit.comments;

import ru.practicum.shareit.comments.dto.CommentDto;

public interface CommentService {
    CommentDto create(long itemId, long userId, CommentDto commentDto);
}
