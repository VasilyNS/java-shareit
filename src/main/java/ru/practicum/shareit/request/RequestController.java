package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.tools.Const;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto saveItem(@RequestBody RequestDto requestDto, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Request creation: " + requestDto.toString());
        Long userId = getCurUserId(headers);
        return requestService.saveRequest(requestDto, userId);
    }

    /**
     * Получение списка СВОИХ запросов вместе с данными об ответах на них
     * Поддержка пагинации from/size
     */
    @GetMapping
    public List<RequestDto> getRequestsOwn(@RequestHeader Map<String, String> headers) {
        log.info("Begin of Requests getting by Own");
        Long userId = getCurUserId(headers);
        return requestService.getRequestsOwn(userId);
    }

    /**
     * Получение списка ВСЕХ запросов, созданных другими пользователями
     * Поддержка пагинации from/size
     */
    @GetMapping("/all")
    public List<RequestDto> getRequestsAll(@RequestHeader Map<String, String> headers,
                                           @RequestParam(required = false) Long from,
                                           @RequestParam(required = false) Long size) {
        log.info("Begin of All Requests getting");
        Long userId = getCurUserId(headers);
        return requestService.getRequestsAll(userId, from, size);
    }

    /**
     * Получение ОДНОГО запроса по id
     */
    @GetMapping("/{id}")
    public RequestDto getRequestById(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        log.info("Begin of Request getting by id, id=" + id);
        Long userId = getCurUserId(headers);
        return requestService.getRequestById(id, userId);
    }

    private Long getCurUserId(Map<String, String> headers) {
        return Long.parseLong(headers.get(Const.X_OWNER));
    }

}
