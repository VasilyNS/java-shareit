package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.UserMapper;

@UtilityClass
public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(UserMapper.toUserDto(request.getRequestor()));
        requestDto.setCreated(request.getCreated());
        return requestDto;
    }

}
