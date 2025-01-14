package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

public interface CommentService {
    CommentResponseDto addNewComment(Long id, Long userId, CommentRequestDto commentRequestDto);
}
