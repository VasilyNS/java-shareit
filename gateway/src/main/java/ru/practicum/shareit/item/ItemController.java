
package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.tools.Const;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    /**
     * headers test:
     * for (String s: headers.keySet()) { System.out.println(s + " = " + headers.get(s)); }
     */
    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody ItemDto itemDto,
                                           @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item creation: {}", itemDto.toString());
        Long ownerId = getCurUserId(headers);
        return itemClient.saveItem(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable Long id,
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item updating, id={}, {}", id, itemDto.toString());
        Long ownerId = getCurUserId(headers);
        return itemClient.updateItem(id, ownerId, itemDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Item getting, id={}", id);
        Long ownerId = getCurUserId(headers);
        return itemClient.getItemDtoDate(id, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemByOwner(@RequestHeader Map<String, String> headers) {
        log.info("Begin of Item findAllByOwner");
        Long ownerId = getCurUserId(headers);
        return itemClient.getAllItemByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam String text) {
        log.info("Begin of Item findByText, text={}", text);
        return itemClient.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@PathVariable Long itemId,
                                  @RequestBody CommentDto commentDto,
                                  @RequestHeader Map<String, String> headers) {
        log.info("Begin of Comment creation: {}", commentDto.toString());
        Long userId = getCurUserId(headers);
        return itemClient.saveComment(itemId, userId, commentDto);
    }

    private Long getCurUserId(Map<String, String> headers) {
        return Long.parseLong(headers.get(Const.X_OWNER));
    }

}
