package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.tools.Const;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> saveRequest(@RequestBody RequestDtoIn requestDtoIn, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Request creation: {}", requestDtoIn.toString());
        Long userId = getCurUserId(headers);
        return requestClient.saveRequest(userId, requestDtoIn);
    }

    /**
     * Получение списка СВОИХ запросов вместе с данными об ответах на них
     * Поддержка пагинации from/size
     */
    @GetMapping
    public ResponseEntity<Object> getRequestsOwn(@RequestHeader Map<String, String> headers) {
        log.info("Begin of Requests getting by Own");
        Long userId = getCurUserId(headers);
        return requestClient.getRequestsOwn(userId);
    }

    /**
     * Получение списка ВСЕХ запросов, созданных другими пользователями
     * Поддержка пагинации from/size
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader Map<String, String> headers,
                                           @RequestParam(defaultValue = "0") Long from,
                                           @RequestParam(defaultValue = "100") Long size) {
        log.info("Begin of All Requests getting");
        Long userId = getCurUserId(headers);
        return requestClient.getRequestsAll(userId, from, size);
    }

    /**
     * Получение ОДНОГО запроса по id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Request getting by id, id={}", id);
        Long userId = getCurUserId(headers);
        return requestClient.getRequestById(id, userId);
    }

    private Long getCurUserId(Map<String, String> headers) {
        return Long.parseLong(headers.get(Const.X_OWNER));
    }

}
