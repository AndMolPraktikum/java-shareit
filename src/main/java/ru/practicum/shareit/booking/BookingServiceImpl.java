package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
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
    public BookingResponseDto create(BookingRequestDto bookingRequestDto, long bookerId) {
        Booking booking = BookingMapper.toBookingEntity(bookingRequestDto);
        final User user = userService.getUserById(bookerId);
        final Item item = itemService.getItemById(bookingRequestDto.getItemId());
        booking.setBooker(user);
        checkBooking(booking, item);
        booking.setItem(item);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto updateBookingStatus(long bookingId, boolean approved, long ownerId) {
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
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingByIdForOwnerOrAuthor(long bookingId, Long userId) {
        userService.getUserDtoById(userId);
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
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllUserBookings(long bookerId, States state) {
        userService.getUserDtoById(bookerId);
        switch (state) {
            case CURRENT:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByBookerIdCurrent(bookerId));
            case PAST:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByBookerIdPast(bookerId));
            case FUTURE:
                return BookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdFuture(bookerId));
            case WAITING:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByBookerIdWaiting(bookerId));
            case REJECTED:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByBookerIdRejected(bookerId));
            case ALL:
                return BookingMapper.toBookingResponseDtoList(
                        bookingRepository.findByBookerIdOrderByStartDesc(bookerId));
            default:
                throw new FailStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingResponseDto> getAllOwnerBookings(long ownerId, States state) {
        userService.getUserDtoById(ownerId);
        switch (state) {
            case CURRENT:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByOwnerIdCurrent(ownerId));
            case PAST:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByOwnerIdPast(ownerId));
            case FUTURE:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByOwnerIdFuture(ownerId));
            case WAITING:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByOwnerIdWaiting(ownerId));
            case REJECTED:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByOwnerIdRejected(ownerId));
            case ALL:
                return BookingMapper.toBookingResponseDtoList(bookingRepository.findByOwnerIdAll(ownerId));
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
            log.error("Запрашиваемая вещь с ID: {} забронирована на указанное время", booking.getItem());
            throw new ItemIsUnavailableException(String.format("Запрашиваемая вещь " +
                    "с ID: %s забронирована на указанное время", booking.getItem()));
        }

        if (booking.getBooker().getId().equals(item.getOwner().getId())) {
            log.error("Вы пытаетесь забронировать собственную вещь.");
            throw new ItemNotFoundException("Вы пытаетесь забронировать собственную вещь!");
        }

        if (!item.getAvailable()) {
            log.error("Запрашиваемая вещь с ID: {} недоступна для бронирования", booking.getItem());
            throw new ItemIsUnavailableException(String.format("Запрашиваемая вещь " +
                    "с ID: %s недоступна для бронирования", booking.getItem()));
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
