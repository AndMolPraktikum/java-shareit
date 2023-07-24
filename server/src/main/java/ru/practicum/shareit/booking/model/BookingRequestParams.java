package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.enums.States;

@Data
@AllArgsConstructor
public class BookingRequestParams {

    States state;

    Long userId;

    int from;

    int size;
}
