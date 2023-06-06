package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRawDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.config.MapperUtil;
import ru.practicum.shareit.enums.States;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Входящий запрос GET /bookings/{}. ID пользователя: {}", bookingId, userId);
        final Booking bookingByIdForOwnerOrAuthor = bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId);
        final BookingDto bookingDto = modelMapper.map(bookingByIdForOwnerOrAuthor, BookingDto.class);
        log.info("Исходящий ответ: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestParam(defaultValue = "ALL") States state,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Входящий запрос GET /bookings?state={}. ID пользователя: {}", state, userId);
        final List<Booking> allUserBookings = bookingService.getAllUserBookings(userId, state);
        final List<BookingDto> bookingDtoList = MapperUtil.convertList(allUserBookings, this::convertToBookingDto);
        log.info("Исходящий ответ: {}", bookingDtoList);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestParam(defaultValue = "ALL") States state,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Входящий запрос GET /bookings/owner?state={}. ID владельца: {}", state, userId);
        final List<Booking> allOwnerBookings = bookingService.getAllOwnerBookings(userId, state);
        final List<BookingDto> bookingDtoList = MapperUtil.convertList(allOwnerBookings, this::convertToBookingDto);
        log.info("Исходящий ответ: {}", bookingDtoList);
        return bookingDtoList;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestParam boolean approved,
                                          @PathVariable long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Входящий запрос GET /bookings/{}?approved={}. ID владельца: {}", bookingId, approved, ownerId);
        final Booking updatedBooking = bookingService.updateBookingStatus(bookingId, approved, ownerId);
        final BookingDto bookingDto = modelMapper.map(updatedBooking, BookingDto.class);
        log.info("Исходящий ответ: {}", bookingDto);
        return bookingDto;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody BookingRawDto bookingRawDto) {
        log.info("Входящий запрос POST /bookings. ID пользователя: {}.  BookingDto: {}", userId, bookingRawDto);
        BookingDto incomingRequest = toBookingDto(bookingRawDto);
        Booking booking = modelMapper.map(incomingRequest, Booking.class);
        long itemId = bookingRawDto.getItemId();
        BookingDto responseDto = modelMapper.map(bookingService.create(booking, userId, itemId), BookingDto.class);
        log.info("Исходящий ответ: {}", responseDto);
        return responseDto;
    }

    private BookingDto convertToBookingDto(Booking booking) {
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        bookingDto.setItem(convertToItemDto(booking.getItem()));
        bookingDto.setBooker(convertToUserDto(booking.getBooker()));
        return bookingDto;
    }

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private ItemDto convertToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    private BookingDto toBookingDto(BookingRawDto bookingRawDto) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(bookingRawDto.getStart());
        bookingDto.setEnd(bookingRawDto.getEnd());
        return bookingDto;
    }
}
