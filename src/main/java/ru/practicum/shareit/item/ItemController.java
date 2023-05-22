
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
    public Item saveItem(@RequestBody ItemDto itemDto, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item creation: " + itemDto.toString());
        Long ownerId = getCurUserId(headers);
        return itemService.saveItem(itemDto, ownerId);
    }

    @PatchMapping("/{id}")
    public Item updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item updating, id=" + id + ", " + itemDto.toString());
        Long ownerId = getCurUserId(headers);
        return itemService.updateItem(itemDto, ownerId, id);
    }

    @GetMapping("/{id}")
    public ItemDtoDate getItem(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item getting, id=" + id);
        Long ownerId = getCurUserId(headers);
        return itemService.getItemDtoDate(id, ownerId);
    }

    @GetMapping
    public List<ItemDtoDate> getAllItemByOwner(@RequestHeader Map<String, String> headers) {
        log.info("Begin of Item findAllByOwner");
        Long ownerId = getCurUserId(headers);
        return itemService.getAllItemByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<Item> getItemsByText(@RequestParam String text) {
        log.info("Begin of Item findByText, text=" + text);
        return itemService.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@PathVariable Long itemId,
                            @RequestBody Comment comment,
                            @RequestHeader Map<String, String> headers) {
        log.info("Begin of Comment creation: " + comment.toString());
        Long userId = getCurUserId(headers);
        return itemService.saveComment(comment, itemId, userId);
    }


    private Long getCurUserId(Map<String, String> headers) {
        return Long.parseLong(headers.get(Const.X_OWNER));
    }

}
