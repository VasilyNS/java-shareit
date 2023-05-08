package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    public Item create(ItemDto itemDto, int ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        return itemDao.create(item, ownerId);
    }

    public Item update(ItemDto itemDto, int ownerId, int itemId) {
        Item item = ItemMapper.toItem(itemDto);
        return itemDao.update(item, ownerId, itemId);
    }

    public Item get(int id) {
        return itemDao.get(id);
    }

    public List<Item> findAllByOwner(int ownerId) {
        return itemDao.findAllByOwner(ownerId);
    }

    public List<Item> findByText(String text) {
        return itemDao.findByText(text);
    }

}
