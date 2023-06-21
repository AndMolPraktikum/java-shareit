package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoOut {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDtoOut item;

    private UserDto booker;

    private BookingStatus status;
}
