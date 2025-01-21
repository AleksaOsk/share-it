package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public UserResponseDto addNewUser(UserRequestDto userRequestDTO) {
        log.info("Пришел запрос на создание нового пользователя с email = {}", userRequestDTO.getEmail());
        String email = userRequestDTO.getEmail();
        UserValidator.checkEmail(userRequestDTO.getEmail(), userRepository.findByEmailIgnoreCase(email));
        User user = UserMapper.mapToUser(userRequestDTO);
        user = userRepository.save(user);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto userRequestDTO) {
        log.info("Пришел запрос на изменение пользователя с id {}", id);
        Optional<User> userOpt = userRepository.findById(id);
        UserValidator.checkUserId(userOpt);
        User user = userOpt.get();

        String email = userRequestDTO.getEmail();
        if (email != null && !email.equals(user.getEmail())) {
            UserValidator.checkEmail(email, userRepository.findByEmailIgnoreCase(email));
        }

        User userNew = UserMapper.updateMapToUser(user, userRequestDTO);
        user = userRepository.save(userNew);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserResponseDto getUser(Long id) {
        log.info("Пришел запрос на получение пользователя с id {}", id);
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с id = " + id));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Пришел запрос на удаление пользователя с id {}", id);
        userRepository.deleteById(id);
    }
}