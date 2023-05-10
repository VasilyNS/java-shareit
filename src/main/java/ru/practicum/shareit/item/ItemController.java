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
        Long ownerId = getOwnerId(headers);
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody ItemDto itemDto, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item updating, id=" + id + ", " + itemDto.toString());
        Long ownerId = getOwnerId(headers);
        return itemService.update(itemDto, ownerId, id);
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable Long id) {
        log.info("Begin of Item getting, id=" + id);
        return itemService.get(id);
    }

    @GetMapping
    public List<Item> getAllByOwner(@RequestHeader Map<String, String> headers) {
        log.info("Begin of Item findAllByOwner");
        Long ownerId = getOwnerId(headers);
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<Item> getByText(@RequestParam String text) {
        log.info("Begin of Item findByText, text=" + text);
        return itemService.getByText(text);
    }

    private Long getOwnerId(Map<String, String> headers) {
        return Long.parseLong(headers.get(Const.X_OWNER));
    }

}
