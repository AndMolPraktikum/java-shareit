package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.States;

import java.util.List;

public interface BookingService {

    Booking create(Booking booking, long bookerId, long itemId);

    Booking updateBookingStatus(long bookingId, boolean approved, long ownerId);

    Booking getBookingByIdForOwnerOrAuthor(long bookingId, Long userId);

    List<Booking> getAllUserBookings(long userId, States state);

    List<Booking> getAllOwnerBookings(long userId, States state);
}
