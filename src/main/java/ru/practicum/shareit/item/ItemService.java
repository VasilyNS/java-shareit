package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    Item saveItem(ItemDto itemDto, Long ownerId);

    Item updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    ItemDtoDate getItemDtoDate(Long id, Long ownerId);

    List<ItemDtoDate> getAllItemByOwner(Long ownerId);

    List<Item> getItemsByText(String text);

    CommentDto saveComment(Comment comment, Long itemId, Long userId);

    Item checkItemExist(Long id);

}
