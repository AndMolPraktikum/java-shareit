package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.BookingRequestParams;

import java.util.List;

public interface BookingService {

    BookingResponse create(BookingRequest bookingRequest, long bookerId);

    BookingResponse updateBookingStatus(long bookingId, boolean approved, long ownerId);

    BookingResponse getBookingByIdForOwnerOrAuthor(long bookingId, Long userId);

    List<BookingResponse> getAllUserBookings(BookingRequestParams bookingRequestParams);

    List<BookingResponse> getAllOwnerBookings(BookingRequestParams bookingRequestParams);
}
