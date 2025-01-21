package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping
    public UserResponseDto addNewUser(@RequestBody UserRequestDto userRequestDTO) {
        return userService.addNewUser(userRequestDTO);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@PathVariable("userId") Long id, @RequestBody UserUpdateRequestDto userRequestDTO) {
        return userService.updateUser(id, userRequestDTO);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable("userId") Long id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long id) {
        userService.deleteUser(id);
    }
}
