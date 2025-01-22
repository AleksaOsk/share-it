package ru.practicum.shareit.mock.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user, user2;
    private Item item, item2, itemUpdate, item3;
    private ItemReqDto itemReqDto, itemReqDto2, itemReqDto3;
    private ItemUpdateRequestDto updatedRequestDto;
    private ItemResponseDto itemResponse, itemResponse2, updatedItemResponse, itemResponse3;
    private Comment comment, comment2;
    private CommentRequestDto commentDto, commentDto2;
    private CommentResponseDto commentResponse, commentResponse2;
    private Booking booking;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "name", "user@mail.ru");
        user2 = new User(2L, "name2", "user2@mail.ru");

        itemReqDto = new ItemReqDto(
                "item name",
                "description",
                true,
                null);
        item = new Item(
                1L,
                itemReqDto.getName(),
                itemReqDto.getDescription(),
                itemReqDto.getAvailable(),
                user,
                null);
        itemResponse = ItemMapper.mapToItemDto(item);

        itemReqDto2 = new ItemReqDto(
                "item2 name",
                "description2",
                true,
                null);
        item2 = ItemMapper.mapToItem(itemReqDto2);
        item2.setId(2L);
        itemResponse2 = ItemMapper.mapToItemDto(item2);

        itemRequest = new ItemRequest(1L, user2, "description", LocalDateTime.now());
        itemReqDto3 = new ItemReqDto(
                "item3 name",
                "description3",
                true,
                1L);
        item3 = ItemMapper.mapToItem(itemReqDto3);
        item3.setId(3L);
        itemResponse3 = ItemMapper.mapToItemDto(item3);

        updatedRequestDto = new ItemUpdateRequestDto(
                "update item name",
                "update description",
                true);
        itemUpdate = new Item(
                1L,
                updatedRequestDto.getName(),
                updatedRequestDto.getDescription(),
                updatedRequestDto.getAvailable(),
                user,
                null);
        updatedItemResponse = new ItemResponseDto(
                itemUpdate.getId(),
                itemUpdate.getName(),
                itemUpdate.getDescription(),
                itemUpdate.getIsAvailable(),
                itemUpdate.getRequest());

        commentDto = new CommentRequestDto("text");
        comment = new Comment(
                1L,
                commentDto.getText(),
                user,
                item,
                LocalDateTime.now());
        commentResponse = new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getItem().getName());

        commentDto2 = new CommentRequestDto("text2");
        comment2 = new Comment(
                1L,
                commentDto.getText(),
                user2,
                item,
                LocalDateTime.now());
        commentResponse2 = new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getItem().getName());

        booking = new Booking(1L,
                LocalDateTime.of(2025, 11, 10, 12, 00, 00),
                LocalDateTime.of(2025, 11, 15, 12, 00, 00),
                item, user2, Status.WAITING);
    }

    @Test
    void createItem() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        ItemResponseDto actualItemDto = itemService.addNewItem(1L, itemReqDto);
        assertEquals(itemReqDto.getName(), actualItemDto.getName());

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemCaptor.capture());
        Item capturedItem = itemCaptor.getValue();
        assertEquals(user, capturedItem.getOwner());

        verify(userRepository).findById(user.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItemWithItemRequest() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item3);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        ItemResponseDto actualItemDto = itemService.addNewItem(1L, itemReqDto3);
        assertEquals(itemReqDto3.getName(), actualItemDto.getName());

        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemCaptor.capture());
        Item capturedItem = itemCaptor.getValue();
        assertEquals(user, capturedItem.getOwner());

        verify(userRepository).findById(user.getId());
        verify(itemRepository).save(any(Item.class));
    }


    @Test
    void updatedItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(itemUpdate);
        ItemResponseDto actualItemDto = itemService.updateItem(user.getId(), item.getId(), updatedRequestDto);

        assertEquals(updatedRequestDto.getName(), actualItemDto.getName());
        assertEquals(updatedRequestDto.getDescription(), actualItemDto.getDescription());
    }

    @Test
    void updateItemWhenItemNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), item.getId(), updatedRequestDto));
        verify(itemRepository).findById(item.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItemWhenUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), item.getId(), updatedRequestDto));
        verify(userRepository).findById(user.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findLastBooking(item.getId()))
                .thenReturn(null);
        when(bookingRepository.findNextBooking(item.getId()))
                .thenReturn(null);
        when(commentRepository.findByItemId(item.getId()))
                .thenReturn(List.of(comment, comment2));

        ItemWithCommentsResponseDto actualItemDto = itemService.getItem(user.getId(), item.getId());

        assertEquals(item.getId(), actualItemDto.getId());
        assertEquals(comment.getAuthor(), actualItemDto.getComments().get(0).getAuthor());
        verify(itemRepository).findById(item.getId());
    }

    @Test
    void getItemsForUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findByOwnerId(anyLong()))
                .thenReturn(List.of(item, item2));
        List<ItemResponseDto> actualItems = itemService.getAllItemsOwner(user.getId());
        assertEquals(2, actualItems.size());
        assertEquals(1L, actualItems.get(0).getId());
    }

    @Test
    void searchItemsWhenTextIsEmpty() {
        List<ItemResponseDto> actualItems = itemService.getItemsByText("");
        assertEquals(0, actualItems.size());
    }

    @Test
    void searchItemsWhenTextIsNull() {
        List<ItemResponseDto> actualItems = itemService.getItemsByText(null);
        assertEquals(0, actualItems.size());
    }

    @Test
    void searchItemsWhenTextIsNotEmpty() {
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase("item2 name"))
                .thenReturn(List.of(item));
        List<ItemResponseDto> actualItems = itemService.getItemsByText("item2 name");
        assertEquals(1, actualItems.size());
        assertEquals(item.getId(), actualItems.get(0).getId());
        assertEquals(item.getName(), actualItems.get(0).getName());
    }
}