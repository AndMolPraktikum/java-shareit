package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.model.Comment;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBooking {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Booking lastBooking;

    private Booking nextBooking;

    private List<Comment> comments;
}
