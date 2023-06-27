package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {

    @NotNull
    private long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}
