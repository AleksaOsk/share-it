package ru.practicum.shareit.mock.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {
    @MockBean
    private ItemRequestServiceImpl itemRequestService;
    @MockBean
    private ItemRequestRepository requestRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto, itemResponse;
    private ItemRequest itemRequest;
    private User user, requestor;
    private Item item, item2, itemUpdate;
    private ItemReqDto itemReqDto, itemReqDto2;
    private ItemResponseDto itemResponseDto, itemResponseDto2;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        user = new User(1L, "name", "user@mail.ru");
        requestor = new User(1L, "requestor", "requestor@mail.ru");

        itemRequestDto = new ItemRequestDto("description");
        itemRequest = new ItemRequest(1L, requestor, itemRequestDto.getDescription(), LocalDateTime.now());
        itemResponse = new ItemRequestDto(1L, requestor, itemRequestDto.getDescription(), LocalDateTime.now());

        itemReqDto = new ItemReqDto(
                "item name",
                "description",
                true,
                itemRequest.getId());
        item = new Item(
                1L,
                itemReqDto.getName(),
                itemReqDto.getDescription(),
                itemReqDto.getAvailable(),
                user,
                itemRequest);
        itemResponseDto = new ItemResponseDto(
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
        itemResponseDto2 = new ItemResponseDto(
                item2.getId(),
                item2.getName(),
                item2.getDescription(),
                item2.getIsAvailable(),
                item2.getRequest());

        itemRequestWithItemsDto = new ItemRequestWithItemsDto(
                1L,
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now(),
                List.of(ItemRequestMapper.mapToItemDto(item), ItemRequestMapper.mapToItemDto(item2)));
    }

    @Test
    void createRequest() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));
        when(itemRequestService.addNewItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemResponse);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())));

    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getItemRequest(anyLong()))
                .thenReturn(itemRequestWithItemsDto);

        mvc.perform(get("/requests/{id}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestWithItemsDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestWithItemsDto.getDescription())));
    }

    @Test
    void getAllItemOfRequestsOwner() throws Exception {
        when(itemRequestService.getAllItemRequestsOwner(requestor.getId()))
                .thenReturn(List.of(itemRequestWithItemsDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestWithItemsDto.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithItemsDto.getDescription())));
    }

    @Test
    void getRequests() throws Exception {
        when(itemRequestService.getAllItemRequests())
                .thenReturn(Collections.singletonList(itemResponse));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponse.getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(itemResponse.getDescription())));
    }
}