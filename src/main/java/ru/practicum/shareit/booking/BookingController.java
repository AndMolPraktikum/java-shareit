package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.enums.States;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /bookings/{}. ID пользователя: {}", bookingId, userId);
        final BookingResponseDto bookingResponseDto = bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId);
        log.info("Исходящий ответ: {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") States state,
                                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Входящий запрос GET /bookings?state={}. ID пользователя: {}", state, userId);
        final List<BookingResponseDto> bookingResponseDtoList = bookingService.getAllUserBookings(userId, state);
        log.info("Исходящий ответ: {}", bookingResponseDtoList);
        return bookingResponseDtoList;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") States state,
                                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Входящий запрос GET /bookings/owner?state={}. ID владельца: {}", state, userId);
        final List<BookingResponseDto> bookingResponseDtoList = bookingService.getAllOwnerBookings(userId, state);
        log.info("Исходящий ответ: {}", bookingResponseDtoList);
        return bookingResponseDtoList;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(@RequestParam boolean approved,
                                                  @PathVariable long bookingId,
                                                  @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Входящий запрос GET /bookings/{}?approved={}. ID владельца: {}", bookingId, approved, ownerId);
        final BookingResponseDto bookingResponseDto = bookingService.updateBookingStatus(bookingId, approved, ownerId);
        log.info("Исходящий ответ: {}", bookingResponseDto);
        return bookingResponseDto;
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Входящий запрос POST /bookings. ID пользователя: {}.  BookingDto: {}", userId, bookingRequestDto);
        BookingResponseDto responseDto = bookingService.create(bookingRequestDto, userId);
        log.info("Исходящий ответ: {}", responseDto);
        return responseDto;
    }
}
