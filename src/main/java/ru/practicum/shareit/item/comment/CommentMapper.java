package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {
    public static Comment mapToComment(CommentRequestDto request) {
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentResponseDto mapToCommentDto(Comment comment) {
        CommentResponseDto commentDto = new CommentResponseDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setItemName(comment.getItem().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}