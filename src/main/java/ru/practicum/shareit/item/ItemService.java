package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    Item create(ItemDto itemDto, Long ownerId);

    Item update(ItemDto itemDto, Long ownerId, Long itemId);

    Item get(Long id);

    List<Item> getAllByOwner(Long ownerId);

    List<Item> getByText(String text);

}
