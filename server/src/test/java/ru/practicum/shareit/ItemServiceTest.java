package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemReqDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;

    UserRequestDto u1;
    UserResponseDto createdUserU1;
    ItemReqDto i1, i2;
    ItemResponseDto createdItem1, createdItem2;
    ItemUpdateRequestDto request;

    @BeforeEach
    public void createItemDto() {
        u1 = new UserRequestDto("name u1", "u1@mail.ru");
        createdUserU1 = userService.addNewUser(u1);
        i1 = new ItemReqDto("дрель", "супер дрель", true, null);
        createdItem1 = itemService.addNewItem(createdUserU1.getId(), i1);
        i2 = new ItemReqDto("молоток", "супер молоток", true, null);
        createdItem2 = itemService.addNewItem(createdUserU1.getId(), i2);
        request = new ItemUpdateRequestDto("дрель 2", "супер дрель 2", false);
    }

    @Test
    public void testCreateItemInRepository() {
        assertThat(createdItem1.getId()).isNotNull();
        assertThat(createdItem1)
                .hasFieldOrPropertyWithValue("name", "дрель")
                .hasFieldOrPropertyWithValue("description", "супер дрель")
                .hasFieldOrPropertyWithValue("available", true)
                .hasFieldOrPropertyWithValue("request", null);
    }

    @Test
    public void testUpdateItem() {
        ItemResponseDto updatedItem = itemService.updateItem(createdUserU1.getId(), createdItem1.getId(), request);
        assertThat(updatedItem)
                .hasFieldOrPropertyWithValue("name", "дрель 2")
                .hasFieldOrPropertyWithValue("description", "супер дрель 2")
                .hasFieldOrPropertyWithValue("available", false);
    }

    @Test
    public void testGetItemsForUser() {
        Collection<ItemResponseDto> list = itemService.getAllItemsOwner(createdUserU1.getId());
        assertThat(list)
                .hasSize(2)
                .extracting(ItemResponseDto::getName)
                .contains("дрель", "молоток");
    }

    @Test
    public void testSearchItems() {
        Collection<ItemResponseDto> list = itemService.getItemsByText("оло");
        assertThat(list)
                .hasSize(1)
                .extracting(ItemResponseDto::getName)
                .contains("молоток");
    }

    @Test
    public void testDeleteUser() {
        Long id = createdItem1.getId();
        assertNotNull(itemService.getItem(createdUserU1.getId(), id),
                "Вещь не найдена. Метод работает некорректно");
        itemService.deleteItem(id);
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                itemService.getItem(createdUserU1.getId(), id));
        assertThat(exception.getReason()).isEqualTo("Вещь не найден с id = " + id);
    }
}
