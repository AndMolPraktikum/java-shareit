package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toCommentEntity(CommentDto commentDto) {
        return new Comment(
                commentDto.getText()
        );
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> commentList) {
        if (commentList == null) {
            return Collections.emptyList();
        }
        return commentList.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
