package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestParams;
import ru.practicum.shareit.enums.States;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.enums.BookingStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final ItemService itemService;

    @Autowired
    private final UserService userService;

    @Transactional
    @Override
    public BookingResponse create(BookingRequest bookingRequest, long bookerId) {
        Booking booking = BookingMapper.toBookingEntity(bookingRequest);
        final User user = userService.getUserById(bookerId);
        final Item item = itemService.getItemById(bookingRequest.getItemId());
        booking.setBooker(user);
        checkBooking(booking, item);
        booking.setItem(item);
        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponse updateBookingStatus(long bookingId, boolean approved, long ownerId) {
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
        return BookingMapper.toBookingResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse getBookingByIdForOwnerOrAuthor(long bookingId, Long userId) {
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
        return BookingMapper.toBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getAllUserBookings(BookingRequestParams bookingRequestParams) {
        States state = bookingRequestParams.getState();
        Long bookerId = bookingRequestParams.getUserId();
        int from = bookingRequestParams.getFrom();
        int size = bookingRequestParams.getSize();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        userService.getUserById(bookerId);
        switch (state) {
            case CURRENT:
                return BookingMapper.toBookingResponseList(bookingRepository.findByBookerIdCurrent(bookerId, page));
            case PAST:
                return BookingMapper.toBookingResponseList(bookingRepository.findByBookerIdPast(bookerId, page));
            case FUTURE:
                return BookingMapper.toBookingResponseList(bookingRepository.findByBookerIdFuture(bookerId, page));
            case WAITING:
                return BookingMapper.toBookingResponseList(bookingRepository.findByBookerIdWaiting(bookerId, page));
            case REJECTED:
                return BookingMapper.toBookingResponseList(bookingRepository.findByBookerIdRejected(bookerId, page));
            case ALL:
                return BookingMapper.toBookingResponseList(bookingRepository
                        .findByBookerIdOrderByStartDesc(bookerId, page));
            default:
                throw new FailStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingResponse> getAllOwnerBookings(BookingRequestParams bookingRequestParams) {
        States state = bookingRequestParams.getState();
        Long ownerId = bookingRequestParams.getUserId();
        int from = bookingRequestParams.getFrom();
        int size = bookingRequestParams.getSize();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        userService.getUserById(ownerId);
        switch (state) {
            case CURRENT:
                return BookingMapper.toBookingResponseList(bookingRepository.findByOwnerIdCurrent(ownerId, page));
            case PAST:
                return BookingMapper.toBookingResponseList(bookingRepository.findByOwnerIdPast(ownerId, page));
            case FUTURE:
                return BookingMapper.toBookingResponseList(bookingRepository.findByOwnerIdFuture(ownerId, page));
            case WAITING:
                return BookingMapper.toBookingResponseList(bookingRepository.findByOwnerIdWaiting(ownerId, page));
            case REJECTED:
                return BookingMapper.toBookingResponseList(bookingRepository.findByOwnerIdRejected(ownerId, page));
            case ALL:
                return BookingMapper.toBookingResponseList(bookingRepository.findByOwnerIdAll(ownerId, page));
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
        Long itemId = item.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (bookingRepository.isAvailableForBooking(itemId, start, end)) {
            log.error("Запрашиваемая вещь с ID: {} забронирована на указанное время", itemId);
            throw new ItemIsUnavailableException(String.format("Запрашиваемая вещь " +
                    "с ID: %d забронирована на указанное время", itemId));
        }

        if (booking.getBooker().getId().equals(item.getOwner().getId())) {
            log.error("Вы пытаетесь забронировать собственную вещь.");
            throw new ItemNotFoundException("Вы пытаетесь забронировать собственную вещь!");
        }

        if (!item.getAvailable()) {
            log.error("Запрашиваемая вещь с ID: {} недоступна для бронирования", item.getId());
            throw new ItemIsUnavailableException(String.format("Запрашиваемая вещь " +
                    "с ID: %d недоступна для бронирования", item.getId()));
        }

        if (!LocalDateTime.now().isBefore(start) || !LocalDateTime.now().isBefore(end)) {
            log.error("Время начала или окончания бронирования не может быть раньше текущего времени");
            throw new BookingWrongTimeException("Время начала или окончания бронирования " +
                    "не может быть раньше текущего времени");
        }
        if (start.isAfter(end)) {
            log.error("Время окончания бронирования не может быть раньше времени начала бронирования");
            throw new BookingWrongTimeException("Время окончания бронирования " +
                    "не может быть раньше времени начала бронирования");
        }
        if (start.equals(end)) {
            log.error("Время начала и окончания бронирования не может быть равно");
            throw new BookingWrongTimeException("Время начала и окончания бронирования не может быть равно");
        }
    }
}
