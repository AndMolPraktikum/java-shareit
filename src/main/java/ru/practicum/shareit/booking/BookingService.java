package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.enums.States;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(BookingRequestDto bookingRequestDto, long bookerId);

    BookingResponseDto updateBookingStatus(long bookingId, boolean approved, long ownerId);

    BookingResponseDto getBookingByIdForOwnerOrAuthor(long bookingId, Long userId);

    List<BookingResponseDto> getAllUserBookings(long userId, States state);

    List<BookingResponseDto> getAllOwnerBookings(long userId, States state);
}
