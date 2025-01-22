package ru.practicum.shareit.mock.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;


    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;

    private User user, updatedUser;
    private UserRequestDto userRequestDto1;
    private UserUpdateRequestDto updatedRequest;
    private UserResponseDto userResponseDto, updateResponseDto;

    @BeforeEach
    void setUp() {
        userRequestDto1 = new UserRequestDto("user", "user@mail.ru");
        user = new User(1L, userRequestDto1.getName(), userRequestDto1.getEmail());
        userResponseDto = UserMapper.mapToUserDto(user);

        updatedRequest = new UserUpdateRequestDto("update user", "update@mail.ru");
        updatedUser = new User(1L, updatedRequest.getName(), updatedRequest.getEmail());
        updateResponseDto = UserMapper.mapToUserDto(updatedUser);
    }

    @Test
    void createUser() throws Exception {
        when(userService.addNewUser(any(UserRequestDto.class)))
                .thenReturn(userResponseDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail())));
    }


    @Test
    void getUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(userResponseDto);
        mvc.perform(get("/users/{id}", userResponseDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(userResponseDto.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userService.updateUser(updateResponseDto.getId(), updatedRequest))
                .thenReturn(updateResponseDto);
        mvc.perform(patch("/users/{id}", updateResponseDto.getId())
                        .content(mapper.writeValueAsString(updateResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(updateResponseDto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{id}", userResponseDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден")).when(userService).deleteUser(anyLong());

        mvc.perform(delete("/users/{id}", 999)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(
                        "Пользователь не найден")));
    }
}