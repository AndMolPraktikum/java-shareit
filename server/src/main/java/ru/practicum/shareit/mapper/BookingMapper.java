package ru.practicum.shareit.mapper;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.enums.BookingStatus.WAITING;

@Service
public class BookingMapper {

    public static Booking toBookingEntity(BookingRequest bookingRequest) {
        return new Booking(
                bookingRequest.getStart(),
                bookingRequest.getEnd(),
                WAITING
        );
    }

    public static BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDtoOut(booking.getItem()),
                UserMapper.toUserResponse(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static List<BookingResponse> toBookingResponseList(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    public static BookingResponse toBookingResponseShort(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingResponse(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
