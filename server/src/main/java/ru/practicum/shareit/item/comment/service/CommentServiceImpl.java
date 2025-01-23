package ru.practicum.shareit.item.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private CommentRepository commentRepository;

    @Override
    public CommentResponseDto addNewComment(Long itemId, Long userId, CommentRequestDto commentRequestDto) {
        log.info("Пришел запрос на создание комментария от пользователя с id {} для вещи с id {}", userId, itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id = " + itemId + " не существует"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = " + userId + " не существует"));
        Booking booking = bookingRepository.findByBookerIdAndItemId(userId, itemId)
                .orElseThrow(() ->
                        new ValidationException("Пользователь с id " + userId + " не бронировал вещь с id " + itemId));
        BookingValidator.checkBooking(booking);

        Comment comment = CommentMapper.mapToComment(commentRequestDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment = commentRepository.save(comment);
        return CommentMapper.mapToCommentDto(comment);
    }
}