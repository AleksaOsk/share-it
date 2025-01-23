package ru.practicum.shareit.mock.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Item item;
    private Comment comment;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user1@email.ru");
        item = new Item(1L, "Item 1", "Description", true, user, null);
        comment = new Comment(1L, "Comment", user, item, LocalDateTime.now());
        commentRequestDto = new CommentRequestDto("Comment");
        booking = new Booking(1L,
                LocalDateTime.of(2025, 1, 10, 12, 00, 00),
                LocalDateTime.of(2025, 1, 15, 12, 00, 00),
                item, user, Status.APPROVED);
    }

    @Test
    void createComment() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId()))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        CommentResponseDto actualCommentDto = commentService.addNewComment(item.getId(), user.getId(), commentRequestDto);
        assertEquals(commentRequestDto.getText(), actualCommentDto.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createCommentWhenItemNotFound() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> commentService.addNewComment(item.getId(), user.getId(), commentRequestDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenUserHasNotBookedItem() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> commentService.addNewComment(item.getId(), user.getId(), commentRequestDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenBookingIsActive() {
        booking = new Booking(1L,
                LocalDateTime.of(2025, 1, 10, 12, 00, 00),
                LocalDateTime.now().plusDays(30),
                item, user, Status.APPROVED);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId()))
                .thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> commentService.addNewComment(item.getId(), user.getId(), commentRequestDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
