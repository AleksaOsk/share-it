package ru.practicum.shareit.mock.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl requestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private User user, requestor;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto, itemResponse;
    private ItemRequestWithItemsDto itemRequestWithItemsDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "name1", "user1@mail.ru");
        requestor = new User(2L, "name2", "user2@mail.ru");

        itemRequestDto = new ItemRequestDto("description");
        itemRequest = new ItemRequest(1L, requestor, itemRequestDto.getDescription(), LocalDateTime.now());
        itemResponse = ItemRequestMapper.mapToItemRequestDto(itemRequest);

        item = new Item(
                1L,
                "item name",
                "item description",
                true,
                user,
                itemRequest);

        itemRequestWithItemsDto = new ItemRequestWithItemsDto(
                1L,
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now(),
                List.of(ItemRequestMapper.mapToItemDto(item), ItemRequestMapper.mapToItemDto(item)));
    }

    @Test
    void createRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto result = requestService.addNewItemRequest(requestor.getId(), itemRequestDto);
        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(userRepository).findById(requestor.getId());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void getRequestByIdWhenRequestExists() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        ItemRequestWithItemsDto result = requestService.getItemRequest(itemRequest.getId());
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        verify(itemRequestRepository).findById(itemRequest.getId());
    }

    @Test
    void getRequestByIdWhenRequestDoesNotExist() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            requestService.getItemRequest(999L);
        });
        assertEquals("Запрос вещи не найден с id = 999", exception.getReason());
        verify(itemRequestRepository).findById(999L);
    }

    @Test
    void getRequests() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(requestor));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findByRequestorId(anyLong()))
                .thenReturn(List.of(item));
        List<ItemRequestWithItemsDto> result = requestService.getAllItemRequestsOwner(requestor.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.get(0).getId());
        verify(itemRequestRepository).findAllByRequestorIdOrderByCreatedDesc(requestor.getId());
    }

    @Test
    void getAllRequests() {
        when(itemRequestRepository.findAllByOrderByCreatedDesc())
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> result = requestService.getAllItemRequests();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.get(0).getId());
        verify(itemRequestRepository).findAllByOrderByCreatedDesc();
    }

    @Test
    void getRequest() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong()))
                .thenReturn(List.of(item));
        ItemRequestWithItemsDto result = requestService.getItemRequest(itemRequest.getId());
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        verify(itemRequestRepository).findById(itemRequest.getId());
    }
}
