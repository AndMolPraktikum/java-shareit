package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.ItemWithBooking;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemMapper {

    public static ItemWithBookingDto toItemWithBookingDto(ItemWithBooking itemWithBooking) {

        return new ItemWithBookingDto(
                itemWithBooking.getId(),
                itemWithBooking.getName(),
                itemWithBooking.getDescription(),
                itemWithBooking.getAvailable(),
                ItemMapper.toBookingShortDto(itemWithBooking.getLastBooking()),
                ItemMapper.toBookingShortDto(itemWithBooking.getNextBooking()),
                ItemMapper.toCommentDtoList(itemWithBooking.getComments())
        );
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingShortDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static List<ItemWithBookingDto> toItemWithBookingDtoList(List<ItemWithBooking> items) {
        return items.stream()
                .map(ItemMapper::toItemWithBookingDto)
                .collect(Collectors.toList());
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(ItemMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
