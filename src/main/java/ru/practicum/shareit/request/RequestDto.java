package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items = new ArrayList<>();

}
