package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingDtoForBookerId;

import java.util.ArrayList;
import java.util.List;

/**
 * Item для lastBooking и nextBooking, а также комменты
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoDate {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments = new ArrayList<>();
    private BookingDtoForBookerId lastBooking;
    private BookingDtoForBookerId nextBooking;

}
