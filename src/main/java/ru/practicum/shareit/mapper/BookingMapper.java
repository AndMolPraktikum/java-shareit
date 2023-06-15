package ru.practicum.shareit.mapper;


import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.enums.BookingStatus.WAITING;

public class BookingMapper {

    public static Booking toBookingEntity(BookingRequestDto bookingRequestDto) {
        return new Booking(
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                WAITING
        );
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static List<BookingResponseDto> toBookingResponseDtoList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
