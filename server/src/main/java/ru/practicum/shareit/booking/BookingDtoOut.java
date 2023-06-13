package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoOut {

    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private UserDto booker;

}
