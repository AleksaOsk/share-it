package ru.practicum.shareit.mock.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemController;
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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private CommentService commentService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    private User user, user2;
    private Item item, item2, itemUpdate;
    private ItemReqDto itemReqDto, itemReqDto2;
    private ItemUpdateRequestDto updatedRequestDto;
    private ItemResponseDto itemResponse, itemResponse2, updatedItemResponse;
    private Comment comment;
    private CommentRequestDto commentDto;
    private CommentResponseDto commentResponse;
    private Booking booking;

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
        itemResponse = new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest());

        itemReqDto2 = new ItemReqDto(
                "item2 name",
                "description2",
                true,
                null);
        item2 = new Item(
                2L,
                itemReqDto2.getName(),
                itemReqDto2.getDescription(),
                itemReqDto2.getAvailable(),
                user,
                null);
        itemResponse2 = new ItemResponseDto(
                item2.getId(),
                item2.getName(),
                item2.getDescription(),
                item2.getIsAvailable(),
                item2.getRequest());

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

        booking = new Booking(1L,
                LocalDateTime.of(2025, 11, 10, 12, 00, 00),
                LocalDateTime.of(2025, 11, 15, 12, 00, 00),
                item, user2, Status.WAITING);
    }

    @Test
    void createItem() throws Exception {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        when(itemService.addNewItem(anyLong(), any(ItemReqDto.class)))
                .thenReturn(itemResponse);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())));

    }

    @Test
    void getItemById() throws Exception {
        ItemWithCommentsResponseDto itemWithCommentsResponseDto = new ItemWithCommentsResponseDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getRequest(), null, null, null);
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemWithCommentsResponseDto);
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        mvc.perform(get("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithCommentsResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithCommentsResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemWithCommentsResponseDto.getDescription())));
    }

    @Test
    void getItemsForUser() throws Exception {
        when(itemService.getAllItemsOwner(anyLong()))
                .thenReturn(Arrays.asList(itemResponse, itemResponse2));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Проверка размера списка
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("item name")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("item2 name")))
                .andExpect(jsonPath("$[1].description", is("description2")));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemUpdateRequestDto.class)))
                .thenReturn(updatedItemResponse);
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        mvc.perform(patch("/items/{id}", updatedItemResponse.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(updatedItemResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(updatedItemResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemResponse.getName())))
                .andExpect(jsonPath("$.available", is(updatedItemResponse.getAvailable())))
                .andExpect(jsonPath("$.description", is(updatedItemResponse.getDescription())));
    }

    @Test
    void addComment() throws Exception {
        when(commentService.addNewComment(anyLong(), anyLong(), any(CommentRequestDto.class)))
                .thenReturn(commentResponse);
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        mvc.perform(post("/items/{id}/comment", item.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void searchItemsEmptyText() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}