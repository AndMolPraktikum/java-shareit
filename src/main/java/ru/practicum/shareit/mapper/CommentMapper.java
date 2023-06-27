package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.comments.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentMapper {

    public static Comment toCommentEntity(CommentRequest commentRequest) {
        return new Comment(
                commentRequest.getText()
        );
    }

    public static CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<CommentResponse> toCommentResponseList(List<Comment> commentList) {
        return commentList.stream()
                .map(CommentMapper::toCommentResponse)
                .collect(Collectors.toList());
    }
}
