package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private int owner;
    //private int request;  // для БД
    private ItemRequest request;

}