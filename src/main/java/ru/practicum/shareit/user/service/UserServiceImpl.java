package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public UserResponseDto addNewUser(UserRequestDto userRequestDTO) {
        log.info("Пришел запрос на создание нового пользователя с email = {}", userRequestDTO.getEmail());
        checkName(userRequestDTO.getName());
        checkEmail(userRequestDTO.getEmail());
        User user = UserMapper.mapToUser(userRequestDTO);
        user = userRepository.addNewUser(user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto userRequestDTO) {
        log.info("Пришел запрос на изменение пользователя с id {}", id);
        checkId(id);
        Optional<User> userOpt = userRepository.getUser(id);
        User user = userOpt.get();

        if (userRequestDTO.getEmail() != null && !userRequestDTO.getEmail().equals(user.getEmail())) {
            checkEmail(userRequestDTO.getEmail());
        }

        User userNew = UserMapper.updateMapToUser(user, userRequestDTO);
        user = userRepository.updateUser(userNew);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserResponseDto getUser(Long id) {
        log.info("Пришел запрос на получение пользователя с id {}", id);
        return userRepository.getUser(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id = " + id));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Пришел запрос на получение списка всех пользователей");
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Пришел запрос на удаление пользователя с id {}", id);
        checkId(id);
        userRepository.deleteUser(id);
    }

    private void checkName(String name) {
        if (name == null || name.isBlank() || name.isEmpty()) {
            throw new ValidationException("Имя должно быть указано");
        }
    }

    private void checkId(long id) {
        if (userRepository.getUser(id).isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
    }

    private void checkEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Имейл должен быть указан");
        } else if (!email.contains("@")) {
            throw new ValidationException("Имейл должен содержать символ '@'");
        } else {
            for (User user1 : userRepository.getAllUsers()) {
                if (user1.getEmail().equals(email)) {
                    throw new DuplicatedException("Этот имейл уже используется");
                }
            }
        }
    }
}