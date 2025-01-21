package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ItemDto {
    @NotBlank(message = "name должно быть указано")
    @Size(max = 200)
    private String name;
    @NotBlank(message = "description должно быть указано")
    @Size(max = 355)
    private String description;
    @NotNull(message = "available должен быть указан")
    private Boolean available;
    private Long requestId;
}
