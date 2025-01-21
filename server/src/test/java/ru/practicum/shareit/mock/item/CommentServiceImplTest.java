package ru.practicum.shareit.mock.item;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
//    @Mock
//    private CommentRepository commentRepository;
//    @Mock
//    private ItemRepository itemRepository;
//    @Mock
//    private BookingRepository bookingRepository;
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private CommentServiceImpl commentService;
//
//    private User user;
//    private Item item;
//    private Comment comment;
//    private CommentDto commentDto;
//    private Booking booking;
//
//    @BeforeEach
//    void setUp() {
//        user = User.builder()
//                .id(1L)
//                .name("User")
//                .email("user1@email.ru")
//                .build();
//
//        item = Item.builder()
//                .id(1L)
//                .name("Item 1")
//                .description("Description")
//                .available(true)
//                .owner(user)
//                .build();
//
//        comment = Comment.builder()
//                .id(1L)
//                .text("Comment")
//                .item(item)
//                .author(user)
//                .build();
//
//        commentDto = CommentDto.builder()
//                .text("Comment")
//                .build();
//
//        booking = Booking.builder()
//                .id(1L)
//                .booker(user)
//                .item(item)
//                .end(LocalDateTime.now().minusDays(1)) // Завершено
//                .build();
//    }
//
//    @Test
//    void createComment() {
//        when(userService.getUserById(user.getId())).thenReturn(user);
//        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
//        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(Optional.of(booking));
//        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
//        CommentDto actualCommentDto = commentService.createComment(user.getId(), commentDto, item.getId());
//        assertEquals(commentDto.getText(), actualCommentDto.getText());
//        verify(commentRepository).save(any(Comment.class));
//    }
//
//    @Test
//    void createCommentWhenItemNotFound() {
//        when(userService.getUserById(user.getId())).thenReturn(user);
//        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());
//        assertThrows(ItemIdNotFoundException.class, () -> commentService.createComment(user.getId(), commentDto, item.getId()));
//        verify(commentRepository, never()).save(any(Comment.class));
//    }
//
//    @Test
//    void createCommentWhenUserHasNotBookedItem() {
//        when(userService.getUserById(user.getId())).thenReturn(user);
//        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
//        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(Optional.empty());
//        assertThrows(BadRequestException.class, () -> commentService.createComment(user.getId(), commentDto, item.getId()));
//        verify(commentRepository, never()).save(any(Comment.class));
//    }
//
//    @Test
//    void createCommentWhenBookingIsActive() {
//        booking = Booking.builder()
//                .id(1L)
//                .booker(user)
//                .item(item)
//                .end(LocalDateTime.now().plusDays(1)) // Активное бронирование
//                .build();
//
//        when(userService.getUserById(user.getId())).thenReturn(user);
//        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
//        when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(Optional.of(booking));
//        assertThrows(BadRequestException.class, () -> commentService.createComment(user.getId(), commentDto, item.getId()));
//        verify(commentRepository, never()).save(any(Comment.class));
//    }
//
//    @Test
//    void commentsForItem() {
//        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
//        List<Comment> actualComments = commentService.commentsForItem(item.getId());
//        assertEquals(1, actualComments.size());
//        assertEquals(comment, actualComments.get(0));
//        verify(commentRepository).findAllByItemId(item.getId());
//    }
}
