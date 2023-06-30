package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingRequestParams;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.enums.States;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@PathVariable long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /bookings/{}. ID пользователя: {}", bookingId, userId);
        final BookingResponse bookingResponse = bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId);
        log.info("Исходящий ответ: {}", bookingResponse);
        return bookingResponse;
    }

    @GetMapping
    public List<BookingResponse> getAllUserBookings(@RequestParam(defaultValue = "ALL") States state,
                                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "5") int size) {
        log.info("Входящий запрос GET /bookings?state={}from={}&size={}. ID пользователя: {}",
                state, from, size, userId);
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        final List<BookingResponse> bookingResponseList = bookingService.getAllUserBookings(bookingRequestParams);
        log.info("Исходящий ответ: {}", bookingResponseList);
        return bookingResponseList;
    }

    @GetMapping("/owner")
    public List<BookingResponse> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") States state,
                                                     @RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "5") int size) {
        log.info("Входящий запрос GET /bookings/owner?state={}from={}&size={}. ID владельца: {}",
                state, from, size, userId);
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        final List<BookingResponse> bookingResponseList = bookingService.getAllOwnerBookings(bookingRequestParams);
        log.info("Исходящий ответ: {}", bookingResponseList);
        return bookingResponseList;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateBookingStatus(@RequestParam boolean approved,
                                               @PathVariable long bookingId,
                                               @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Входящий запрос GET /bookings/{}?approved={}. ID владельца: {}", bookingId, approved, ownerId);
        final BookingResponse bookingResponse = bookingService.updateBookingStatus(bookingId, approved, ownerId);
        log.info("Исходящий ответ: {}", bookingResponse);
        return bookingResponse;
    }

    @PostMapping
    public BookingResponse createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody BookingRequest bookingRequest) {
        log.info("Входящий запрос POST /bookings. ID пользователя: {}.  BookingRequest: {}", userId, bookingRequest);
        BookingResponse response = bookingService.create(bookingRequest, userId);
        log.info("Исходящий ответ: {}", response);
        return response;
    }
}
