package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestWithItemsDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}
