package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.tools.Const;
import ru.practicum.shareit.tools.Validator;
import ru.practicum.shareit.tools.exception.RequestNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Transactional
    public RequestDto saveRequest(RequestDtoIn requestDtoIn, Long userId) {
        User user = userService.getUser(userId);
        Validator.requestValidation(requestDtoIn);
        Request request = new Request();

        request.setDescription(requestDtoIn.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        RequestDto outRequestDto = RequestMapper
                .toRequestDto(requestRepository.save(request));

        return outRequestDto;
    }

    /**
     * Получение только своих запросов
     */
    public List<RequestDto> getRequestsOwn(Long userId) {
        User user = userService.checkUserExist(userId);

        // Получение списка всех запросов, удовлетворяющих условиям
        List<Request> requests = requestRepository.findByOwn(user);

        // Набивка массива List<Request> requests вещами
        List<RequestDto> requestsDto = completeRequestsDtoArrayByItems(requests);

        return requestsDto;
    }

    /**
     * Метод со сложной логикой: получение только тех запросов, на которые
     * ссылаются предметы (Item) с полем REQUEST_ID (появилось в ТЗ 15).
     * Причем владельцем этого предмета должен быть пользователь с userId
     * (передается через заголовок HTTP).
     */
    public List<RequestDto> getRequestsAll(Long userId, Long from, Long size) {
        // Блок проверок и валидации
        userService.checkUserExist(userId);
        Validator.pageableValidation(from, size);

        // Блок для подготовки пагинации
        int pageFrom;
        int pageSize;
        if (from == null || size == null) { // Если не задана пагинация, то выбираем все, но всё равно с ограничением!
            pageFrom = 0;
            pageSize = Const.MAX_PAGE_SIZE; // Ограничение выдачи больших списков для защиты от флуда
        } else {
            pageFrom = Math.toIntExact(from / size);  // Конвертация начала страницы в номер элемента (очень условно)
            pageSize = Math.toIntExact(size);
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(pageFrom, pageSize, sort);

        // Запрос в репозиторий с пагинацией и конвертация страницы в список сущностей Request
        Page<Request> requestsPage = requestRepository.findAllByItemsOwner(userId, page);
        List<Request> requests = requestsPage.stream().collect(Collectors.toList());

        // Набивка массива List<Request> requests вещами
        List<RequestDto> requestsDto = completeRequestsDtoArrayByItems(requests);

        return requestsDto;
    }

    public RequestDto getRequestById(Long id, Long userId) {
        userService.checkUserExist(userId);
        Request request = checkRequestExist(id);
        RequestDto requestDto = RequestMapper.toRequestDto(checkRequestExist(id));

        List<ItemDto> itemsDto = toListOfItemDto(itemRepository.findByRequest(request));
        requestDto.setItems(itemsDto);

        return requestDto;
    }

    public Request checkRequestExist(Long id) {
        return requestRepository.findById(id).orElseThrow(() -> new RequestNotFoundException(id));
    }

    /**
     * Получение внутри объекта RequestDto
     * поля со списком "items": [{"requestId": 1}, ... ] через ItemDto в соответствии с требованием тестов
     */
    private List<ItemDto> toListOfItemDto(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            itemDto.setRequestId(item.getRequest().getId());
            itemsDto.add(itemDto);
        }
        return itemsDto;
    }

    /**
     * Набивка массива List<Request> requests вещами
     */
    List<RequestDto> completeRequestsDtoArrayByItems(List<Request> requests) {
        // Получение всех вещей для этих запросов, чтобы был единый запрос к базе
        List<ItemDto> allItemsDto = toListOfItemDto(itemRepository.findByRequestForArray(requests));

        // Разбиваем все вещи на мапу: <id запроса + массив вещей для этого id>
        Map<Long, List<ItemDto>> itemsMap = new HashMap<>();
        for (ItemDto itemDto : allItemsDto) {
            Long id = itemDto.getRequestId();
            if (itemsMap.containsKey(id)) {
                itemsMap.get(id).add(itemDto);
            } else {
                List<ItemDto> itemsListForMap = new ArrayList<>();
                itemsListForMap.add(itemDto);
                itemsMap.put(id, itemsListForMap);
            }
        }

        // Подключаем каждый массив вещей из мапы для соответствующего запроса
        List<RequestDto> requestsDto = new ArrayList<>();
        for (Request request : requests) {
            RequestDto requestDto = RequestMapper.toRequestDto(request);
            Long id = requestDto.getId();
            requestDto.setItems(itemsMap.getOrDefault(id, List.of()));
            requestsDto.add(requestDto);
        }

        return requestsDto;
    }

}
