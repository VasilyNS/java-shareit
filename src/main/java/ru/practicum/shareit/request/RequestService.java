package ru.practicum.shareit.request;

import java.util.List;

public interface RequestService {

    RequestDto saveRequest(RequestDto requestDto, Long userId);

    List<RequestDto> getRequestsOwn(Long userId);

    List<RequestDto> getRequestsAll(Long userId, Long from, Long size);

    RequestDto getRequestById(Long id, Long userId);

    Request checkRequestExist(Long id);

}
