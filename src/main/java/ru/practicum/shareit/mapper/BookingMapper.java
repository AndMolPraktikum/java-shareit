package ru.practicum.shareit.mapper;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.enums.BookingStatus.WAITING;

@Service
public class BookingMapper {

    public static Booking toBookingEntity(BookingDtoIn bookingDtoIn) {
        return new Booking(
                bookingDtoIn.getStart(),
                bookingDtoIn.getEnd(),
                WAITING
        );
    }

    public static BookingDtoOut toBookingResponseDto(Booking booking) {
        return new BookingDtoOut(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDtoOut(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static List<BookingDtoOut> toBookingResponseDtoList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
