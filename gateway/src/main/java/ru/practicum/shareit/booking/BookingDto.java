package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
	private long itemId;
	//@FutureOrPresent
	private LocalDateTime start;
	//@Future
	private LocalDateTime end;
}
