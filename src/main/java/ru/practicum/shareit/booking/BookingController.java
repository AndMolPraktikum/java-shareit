package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingRequestParams;
import ru.practicum.shareit.enums.States;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public BookingDtoOut getBookingById(@PathVariable long bookingId,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /bookings/{}. ID пользователя: {}", bookingId, userId);
        final BookingDtoOut bookingDtoOut = bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId);
        log.info("Исходящий ответ: {}", bookingDtoOut);
        return bookingDtoOut;
    }

    @GetMapping
    public List<BookingDtoOut> getAllUserBookings(@RequestParam(defaultValue = "ALL") States state,
                                                  @RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(defaultValue = "5") @Min(1) int size) {
        log.info("Входящий запрос GET /bookings?state={}from={}&size={}. ID пользователя: {}",
                state, from, size, userId);
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        final List<BookingDtoOut> bookingDtoOutList = bookingService.getAllUserBookings(bookingRequestParams);
        log.info("Исходящий ответ: {}", bookingDtoOutList);
        return bookingDtoOutList;
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") States state,
                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "0") @Min(0) int from,
                                                   @RequestParam(defaultValue = "5") @Min(1) int size) {
        log.info("Входящий запрос GET /bookings/owner?state={}from={}&size={}. ID владельца: {}",
                state, from, size, userId);
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        final List<BookingDtoOut> bookingDtoOutList = bookingService.getAllOwnerBookings(bookingRequestParams);
        log.info("Исходящий ответ: {}", bookingDtoOutList);
        return bookingDtoOutList;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut updateBookingStatus(@RequestParam boolean approved,
                                             @PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Входящий запрос GET /bookings/{}?approved={}. ID владельца: {}", bookingId, approved, ownerId);
        final BookingDtoOut bookingDtoOut = bookingService.updateBookingStatus(bookingId, approved, ownerId);
        log.info("Исходящий ответ: {}", bookingDtoOut);
        return bookingDtoOut;
    }

    @PostMapping
    public BookingDtoOut createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Входящий запрос POST /bookings. ID пользователя: {}.  BookingDto: {}", userId, bookingDtoIn);
        BookingDtoOut responseDto = bookingService.create(bookingDtoIn, userId);
        log.info("Исходящий ответ: {}", responseDto);
        return responseDto;
    }
}
