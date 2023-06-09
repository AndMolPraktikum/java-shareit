package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemMapper {

    public static Item toEntity(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public static ItemWithBooking toItemWithBooking(Item item) {
        return new ItemWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static List<ItemWithBooking> toItemWithBookingList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemWithBooking)
                .collect(Collectors.toList());
    }

    public static ItemWithBookingDto toItemWithBookingDto(ItemWithBooking itemWithBooking) {
        return new ItemWithBookingDto(
                itemWithBooking.getId(),
                itemWithBooking.getName(),
                itemWithBooking.getDescription(),
                itemWithBooking.getAvailable(),
                ItemMapper.toBookingShortDto(itemWithBooking.getLastBooking()),
                ItemMapper.toBookingShortDto(itemWithBooking.getNextBooking()),
                CommentMapper.toCommentDtoList(itemWithBooking.getComments())
        );
    }

    public static List<ItemWithBookingDto> toItemWithBookingDtoList(List<ItemWithBooking> itemWithBooking) {
        return itemWithBooking.stream()
                .map(ItemMapper::toItemWithBookingDto)
                .collect(Collectors.toList());
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

}
