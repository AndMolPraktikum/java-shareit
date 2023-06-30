package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.user.dto.UserResponse;

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
    private UserResponse owner;

    private BookingResponse lastBooking;

    private BookingResponse nextBooking;

    private List<CommentResponse> comments;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long requestId;

    public ItemDtoOut(Long id, String name, String description, Boolean available, BookingResponse lastBooking,
                      BookingResponse nextBooking, List<CommentResponse> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }

    public ItemDtoOut(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
