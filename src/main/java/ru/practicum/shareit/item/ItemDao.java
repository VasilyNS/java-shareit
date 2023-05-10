package ru.practicum.shareit.item;

import java.util.List;

public interface ItemDao {

    Item create(Item item, Long ownerId);

    Item update(Item item, Long ownerId, Long itemId);

    Item get(Long id);

    List<Item> getAllByOwner(Long ownerId);

    List<Item> getByText(String text);

}
