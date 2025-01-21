package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceTest {
    private final UserService userService;
    UserRequestDto u1, u2;
    UserResponseDto createdUserU1, createdUserU2;
    UserUpdateRequestDto request, requestWithRepeatEmail;

    @BeforeEach
    public void createUserDto() {
        u1 = new UserRequestDto("name u1", "u1@mail.ru");
        u2 = new UserRequestDto("name u2", "u2@mail.ru");
        request = new UserUpdateRequestDto("name request", "request@mail.ru");
        requestWithRepeatEmail = new UserUpdateRequestDto("name request", "u2@mail.ru");
        createdUserU1 = userService.addNewUser(u1);
        createdUserU2 = userService.addNewUser(u2);
    }


    @Test
    public void testCreateUserInRepository() {
        assertThat(createdUserU1.getId()).isNotNull();
        assertThat(createdUserU1)
                .hasFieldOrPropertyWithValue("name", "name u1")
                .hasFieldOrPropertyWithValue("email", "u1@mail.ru");
    }

    @Test
    public void testUpdateUserInRepository() {
        UserResponseDto updatedUser = userService.updateUser(createdUserU1.getId(), request);
        assertThat(updatedUser)
                .hasFieldOrPropertyWithValue("name", "name request")
                .hasFieldOrPropertyWithValue("email", "request@mail.ru");
    }

    @Test
    public void testUpdateUserWithRepeatEmail() {
        ConflictException exception = assertThrows(ConflictException.class, () ->
                userService.updateUser(createdUserU1.getId(), requestWithRepeatEmail));
        assertThat(exception.getReason()).isEqualTo("Имейл уже используется");
    }

    @Test
    public void testDeleteUser() {
        Long id = createdUserU1.getId();
        assertNotNull(userService.getUser(id), "Пользователь не найден. Метод работает некорректно");
        userService.deleteUser(id);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUser(id));
        assertThat(exception.getReason()).isEqualTo("Пользователь не найден с id = " + id);
    }
}
