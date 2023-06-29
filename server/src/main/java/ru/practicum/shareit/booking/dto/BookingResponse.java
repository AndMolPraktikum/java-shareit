package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDtoOut item;

    private UserResponse booker;

    private BookingStatus status;

    private long bookerId;

    public BookingResponse(long id, long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }

    public BookingResponse(long id, LocalDateTime start, LocalDateTime end, ItemDtoOut item, UserResponse booker, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
