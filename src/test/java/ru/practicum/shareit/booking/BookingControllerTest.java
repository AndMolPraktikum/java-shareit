package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.BookingRequestParams;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.enums.States;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void getBookingById_whenUserIdAndBookingIdIsCorrect_thenResponseContainsBooking() {
        long userId = 1L;
        long bookingId = 1L;
        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        when(bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId))
                .thenReturn(bookingDtoOut);

        BookingDtoOut response = bookingController.getBookingById(bookingId, userId);

        assertEquals(bookingDtoOut, response);
        verify(bookingService).getBookingByIdForOwnerOrAuthor(bookingId, userId);
    }

    @Test
    void getAllUserBookings_whenParamIsCorrect_thenResponseContainsListOfBookings() {
        States state = States.ALL;
        long userId = 1L;
        int from = 0;
        int size = 5;
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        List<BookingDtoOut> bookingDtoOutList = List.of(new BookingDtoOut(), new BookingDtoOut());
        when(bookingService.getAllUserBookings(bookingRequestParams))
                .thenReturn(bookingDtoOutList);

        List<BookingDtoOut> responseDtoList = bookingController.getAllUserBookings(state, userId, from, size);

        assertEquals(bookingDtoOutList.size(), responseDtoList.size());
        verify(bookingService).getAllUserBookings(bookingRequestParams);
    }

    @Test
    void getAllOwnerBookings_whenParamIsCorrect_thenResponseContainsListOfBookings() {
        States state = States.ALL;
        long userId = 1L;
        int from = 0;
        int size = 5;
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        List<BookingDtoOut> bookingDtoOutList = List.of(new BookingDtoOut(), new BookingDtoOut());
        when(bookingService.getAllOwnerBookings(bookingRequestParams))
                .thenReturn(bookingDtoOutList);

        List<BookingDtoOut> responseDtoList = bookingController.getAllOwnerBookings(state, userId, from, size);

        assertEquals(bookingDtoOutList.size(), responseDtoList.size());
        verify(bookingService).getAllOwnerBookings(bookingRequestParams);
    }

    @Test
    void updateBookingStatus_whenInvoked_thenBookingUpdate() {
        boolean approved = true;
        long bookingId = 1L;
        long ownerId = 1L;
        BookingDtoOut bookingDtoOut = new BookingDtoOut(
                1L,
                LocalDateTime.now().plusSeconds(2),
                LocalDateTime.now().plusSeconds(3),
                new ItemDtoOut(1L, "Садовая тачка", "Возит сама", true, null),
                new UserDto(1L, "user", "user@yandex.ru"),
                BookingStatus.APPROVED
        );
        when(bookingService.updateBookingStatus(bookingId, approved, ownerId))
                .thenReturn(bookingDtoOut);

        BookingDtoOut responseDto = bookingController.updateBookingStatus(approved, bookingId, ownerId);

        assertEquals(bookingDtoOut, responseDto);
        verify(bookingService).updateBookingStatus(bookingId, approved, ownerId);
    }

    @Test
    void createBooking_whenBookerIdIsCorrect_thenUserCreated() {
        long bookerId = 1L;
        BookingDtoIn bookingDtoIn = new BookingDtoIn();
        BookingDtoOut bookingDtoOut = new BookingDtoOut();
        when(bookingService.create(bookingDtoIn, bookerId)).thenReturn(bookingDtoOut);

        BookingDtoOut responseDto = bookingController.createBooking(bookerId, bookingDtoIn);

        assertEquals(bookingDtoOut, responseDto);
        verify(bookingService).create(bookingDtoIn, bookerId);
    }
}