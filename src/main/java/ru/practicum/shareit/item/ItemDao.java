package ru.practicum.shareit.item;

import java.util.List;

public interface ItemDao {

    Item create(Item item, int ownerId);

    Item update(Item item, int ownerId, int itemId);

    Item get(int id);

    List<Item> findAllByOwner(int ownerId);

    List<Item> findByText(String text);

}
