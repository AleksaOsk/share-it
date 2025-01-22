package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public ErrorResponse handleAnnotationsField(ConstraintViolationException e) { //исключение при срабатывании аннотации на отдельных полях (переменные пути)
        String response = e.getConstraintViolations() //выводим все сообщения через запятую
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
        log.error("Пользователь указал некорректные данные. " + response);
        return new ErrorResponse("Указаны некорректные данные. " + response);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorResponse handleMissingHeader(MissingRequestHeaderException e) { //исключение для отсутствия заголовка X-Sharer-User-Id
        log.error("Отсутствует заголовок X-Sharer-User-Id");
        return new ErrorResponse("Не указан заголовок X-Sharer-User-Id. " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public ErrorResponse handleMissingField(JsonMappingException e) { //исключение для отсутствия поля в JSOn
        log.error("В JSON не указано обязательное поле");
        return new ErrorResponse("В запросе отсутствует обязательное поле" + e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAnnotationsObject(MethodArgumentNotValidException e) { //исключение при срабатывании аннотации на объектах
        String response = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(); //можно вернуть массивом все ошибки валидации
        log.error("Пользователь указал некорректные данные." + response);
        ErrorResponse errorResponse = new ErrorResponse("Указаны некорректные данные. " + response);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); //400
    }
}