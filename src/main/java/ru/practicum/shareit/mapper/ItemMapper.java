package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBooking;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemMapper {

    public static Item toEntity(ItemDtoIn itemDtoIn) {
        return new Item(
                itemDtoIn.getId(),
                itemDtoIn.getName(),
                itemDtoIn.getDescription(),
                itemDtoIn.getAvailable()
        );
    }

    public static ItemDtoOut toItemDtoOut(Item item) {
        return new ItemDtoOut(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static List<ItemDtoOut> toItemDtoOutList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDtoOut)
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

    public static ItemDtoOut toItemWithBookingDto(ItemWithBooking itemWithBooking) {
        return new ItemDtoOut(
                itemWithBooking.getId(),
                itemWithBooking.getName(),
                itemWithBooking.getDescription(),
                itemWithBooking.getAvailable(),
                ItemMapper.toBookingShortDto(itemWithBooking.getLastBooking()),
                ItemMapper.toBookingShortDto(itemWithBooking.getNextBooking()),
                CommentMapper.toCommentDtoList(itemWithBooking.getComments())
        );
    }

    public static List<ItemDtoOut> toItemWithBookingDtoList(List<ItemWithBooking> itemWithBooking) {
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

    public static ItemDtoOut toItemWithUserDto(Item item) {
        return new ItemDtoOut(
                item.getId(),
                item.getName(),
                UserMapper.toUserDto(item.getOwner())
        );
    }

    public static List<ItemDtoOut> toItemWithUserDtoList(List<Item> itemList) {
        if (itemList == null) {
            return Collections.emptyList();
        }
        return itemList.stream()
                .map(ItemMapper::toItemWithUserDto)
                .collect(Collectors.toList());
    }

}
