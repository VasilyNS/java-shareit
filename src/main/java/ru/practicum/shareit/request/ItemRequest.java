package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private int id;
    private String description;
    private int requestor;
    private LocalDateTime created;

}
