package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingRequestParams;

import java.util.List;

public interface BookingService {

    BookingDtoOut create(BookingDtoIn bookingDtoIn, long bookerId);

    BookingDtoOut updateBookingStatus(long bookingId, boolean approved, long ownerId);

    BookingDtoOut getBookingByIdForOwnerOrAuthor(long bookingId, Long userId);

    List<BookingDtoOut> getAllUserBookings(BookingRequestParams bookingRequestParams);

    List<BookingDtoOut> getAllOwnerBookings(BookingRequestParams bookingRequestParams);
}
