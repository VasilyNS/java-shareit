package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.tools.Validator;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(Long ownerId, ItemDto itemDto) {
        Validator.itemValidation(itemDto);
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long id, Long ownerId, ItemDto itemDto) {
        Validator.itemValidationForUpdate(itemDto);
        return patch("/" + id, ownerId, itemDto);
    }

    public ResponseEntity<Object> getItemDtoDate(Long id, Long ownerId) {
        return get("/" + id, ownerId);
    }

    public ResponseEntity<Object> getAllItemByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> getItemsByText(String text) {
        return get("/search?text=" + text);
    }

    public ResponseEntity<Object> saveComment(Long itemId, Long userId, CommentDto commentDto) {
        Validator.commentValidation(commentDto);
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}