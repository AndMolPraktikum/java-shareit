package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoOut {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto owner;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;

    public ItemDtoOut(Long id, String name, String description, Boolean available, BookingShortDto lastBooking,
                      BookingShortDto nextBooking, List<CommentDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }

    public ItemDtoOut(Long id, String name, UserDto owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
    }

    public ItemDtoOut(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

}
