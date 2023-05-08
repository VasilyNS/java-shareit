package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    Item create(ItemDto itemDto, int ownerId);

    Item update(ItemDto itemDto, int ownerId, int itemId);

    Item get(int id);

    List<Item> findAllByOwner(int ownerId);

    List<Item> findByText(String text);

}
