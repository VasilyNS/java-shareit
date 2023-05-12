package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;

    public Item create(ItemDto itemDto, Long ownerId) {
        Item item = ItemMapper.toItem(itemDto);
        return itemDao.create(item, ownerId);
    }

    public Item update(ItemDto itemDto, Long ownerId, Long itemId) {
        Item item = ItemMapper.toItem(itemDto);
        return itemDao.update(item, ownerId, itemId);
    }

    public Item get(Long id) {
        return itemDao.get(id);
    }

    public List<Item> getAllByOwner(Long ownerId) {
        return itemDao.getAllByOwner(ownerId);
    }

    public List<Item> getByText(String text) {
        return itemDao.getByText(text);
    }

}
