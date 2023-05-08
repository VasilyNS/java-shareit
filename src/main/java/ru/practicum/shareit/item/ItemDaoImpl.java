package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemDaoImpl implements ItemDao {

    private List<Item> items = new ArrayList<>();
    private int count = 1;
    private final UserDao userDao;

    public Item create(Item item, int ownerId) {
        Validator.allItemValidation(item);
        Item newItem = new Item();

        newItem.setId(count);
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());

        userDao.get(ownerId);
        newItem.setOwner(ownerId);

        items.add(newItem);
        count++;

        log.info("End of Item creation: " + newItem.toString());
        return newItem;
    }

    public Item update(Item item, int ownerId, int itemId) {
        for (Item it : items) {
            if (it.getId() == itemId) {

                if (it.getOwner() != ownerId) {
                    throw new ObjectNotFoundException("Error! Item has ownerId=" + it.getOwner() + " but X-Sharer-User-Id ownerId=" + ownerId);
                }

                // Обновляем поля, если они есть в JSON
                if (item.getName() != null) {
                    it.setName(item.getName());
                }
                if (item.getDescription() != null) {
                    it.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    it.setAvailable(item.getAvailable());
                }

                log.info("End of Item updating: " + it.toString());
                return it;
            }

        }

        return item;
    }

    public Item get(int id) {
        for (Item it : items) {
            if (it.getId() == id) {
                log.info("End of Item getting: " + it.toString());
                return it;
            }
        }

        throw new ObjectNotFoundException("No Item with id=" + id);
    }

    public List<Item> findAllByOwner(int ownerId) {
        List<Item> itemList = new ArrayList<>();

        for (Item it : items) {
            if (it.getOwner() == ownerId) {
                itemList.add(it);
            }
        }

        log.info("End of Item findAllByOwner for ownerId=" + ownerId);
        return itemList;
    }

    public List<Item> findByText(String text) {
        List<Item> itemList = new ArrayList<>();

        for (Item it : items) {
            if ((it.getName().toLowerCase().contains(text.toLowerCase()) ||
                    it.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    it.getAvailable() &&
                    !text.isBlank()) {
                itemList.add(it);
            }
        }

        log.info("End of Item findByText for text=" + text);
        return itemList;
    }

}