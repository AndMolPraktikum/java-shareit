package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.States;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.enums.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final UserService userService;

    @Override
    public Booking create(Booking booking, long bookerId, long itemId) {
        final User user = userService.getUserById(bookerId);
        final Item item = itemService.getItemById(itemId);
        booking.setBooker(user);
        checkBooking(booking, item);
        booking.setItem(item);
        booking.setStatus(WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(long bookingId, boolean approved, long ownerId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getStatus().equals(WAITING)) {
            log.error("Бронирование уже проверено владельцем");
            throw new BookingAlreadyVerifiedByOwnerException("Бронирование уже проверено владельцем");
        }
        itemService.checkUserItem(ownerId, booking.getItem().getId());
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingByIdForOwnerOrAuthor(long bookingId, Long userId) {
        userService.getUserById(userId);
        Booking booking = getBookingById(bookingId);
        final long bookerId = booking.getBooker().getId();
        final long ownerId = booking.getItem().getOwner().getId();

        if (bookerId != userId && ownerId != userId) {
            log.error("Заявленный пользователь с ID: {} не является ни владельцем вещи с ID: {} " +
                    "ни автором бронирования с ID: {}", userId, ownerId, bookerId);
            throw new UserHasNoLinkBookingOrItemException(String.format("Заявленный пользователь с ID: %d " +
                    "не является ни владельцем вещи с ID: %d " +
                    "ни автором бронирования с ID: %d", userId, ownerId, bookerId));
        }
        return booking;
    }

    @Override
    public List<Booking> getAllUserBookings(long bookerId, States state) {
        userService.getUserById(bookerId);
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdCurrent(bookerId);
            case PAST:
                return bookingRepository.findByBookerIdPast(bookerId);
            case FUTURE:
                return bookingRepository.findByBookerIdFuture(bookerId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBookerIdWaiting(bookerId);
            case REJECTED:
                return bookingRepository.findByBookerIdRejected(bookerId);
            case ALL:
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            default:
                throw new FailStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<Booking> getAllOwnerBookings(long ownerId, States state) {
        userService.getUserById(ownerId);
        switch (state) {
            case CURRENT:
                return bookingRepository.findByOwnerIdCurrent(ownerId);
            case PAST:
                return bookingRepository.findByOwnerIdPast(ownerId);
            case FUTURE:
                return bookingRepository.findByOwnerIdFuture(ownerId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByOwnerIdWaiting(ownerId);
            case REJECTED:
                return bookingRepository.findByOwnerIdRejected(ownerId);
            case ALL:
                return bookingRepository.findByOwnerIdAll(ownerId);
            default:
                throw new FailStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking getBookingById(long bookingId) {
        final Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            log.error("Бронирования с ID {} не существует", bookingId);
            throw new BookingNotFoundException(String.format("Бронирования с ID %d не существует", bookingId));
        }
        return bookingOptional.get();
    }

    private void checkBooking(Booking booking, Item item) {
        if (booking.getBooker().getId().equals(item.getId())) {
            log.error("Вы пытаетесь забронировать собственную вещь.");
            throw new ItemNotFoundException("Вы пытаетесь забронировать собственную вещь!");
        }

        if (!item.getAvailable()) {
            log.error("Запрашиваемая вещь с ID: {} недоступна для бронирования", booking.getItem());
            throw new ItemIsUnavailableException(String.format("Запрашиваемая вещь " +
                    "с ID: %s недоступна для бронирования", booking.getItem()));
        }

        if (!LocalDateTime.now().isBefore(booking.getStart()) || !LocalDateTime.now().isBefore(booking.getEnd())) {
            log.error("Время начала или окончания бронирования не может быть раньше текущего времени");
            throw new BookingWrongTimeException("Время начала или окончания бронирования " +
                    "не может быть раньше текущего времени");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            log.error("Время окончания бронирования не может быть раньше времени начала бронирования");
            throw new BookingWrongTimeException("Время окончания бронирования " +
                    "не может быть раньше времени начала бронирования");
        }
        if (booking.getStart().equals(booking.getEnd())) {
            log.error("Время начала и окончания бронирования не может быть равно");
            throw new BookingWrongTimeException("Время начала и окончания бронирования не может быть равно");
        }
    }
}
