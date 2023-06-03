package ru.practicum.shareit.request;

public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(request.getRequestor());
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }

}
