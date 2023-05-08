package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.tools.Const;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * headers test:
     * for (String s: headers.keySet()) { System.out.println(s + " = " + headers.get(s)); }
     */
    @PostMapping
    public Item create(@RequestBody ItemDto itemDto, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item creation: " + itemDto.toString());
        int ownerId = getOwnerId(headers);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public Item update(@PathVariable int id, @RequestBody ItemDto itemDto,  @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item updating, id=" + id + ", " + itemDto.toString());
        int ownerId = getOwnerId(headers);
        return itemService.update(itemDto, ownerId, id);
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable int id) {
        log.info("Begin of Item getting, id=" + id);
        return itemService.get(id);
    }

    @GetMapping
    public List<Item> findAllByOwner(@RequestHeader Map<String, String> headers) {
        log.info("Begin of Item findAllByOwner");
        int ownerId = getOwnerId(headers);
        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<Item> findByText(@RequestParam String text) {
        log.info("Begin of Item findByText, text=" + text);
        return itemService.findByText(text);
    }

    private int getOwnerId(Map<String, String> headers) {
        return Integer.parseInt(headers.get(Const.X_OWNER));
    }

}
