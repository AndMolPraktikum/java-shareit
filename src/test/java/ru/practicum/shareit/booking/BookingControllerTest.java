package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.BookingRequestParams;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.enums.States;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserResponse;

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
        BookingResponse bookingResponse = new BookingResponse();
        when(bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId))
                .thenReturn(bookingResponse);

        BookingResponse response = bookingController.getBookingById(bookingId, userId);

        assertEquals(bookingResponse, response);
        verify(bookingService).getBookingByIdForOwnerOrAuthor(bookingId, userId);
    }

    @Test
    void getAllUserBookings_whenParamIsCorrect_thenResponseContainsListOfBookings() {
        States state = States.ALL;
        long userId = 1L;
        int from = 0;
        int size = 5;
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        List<BookingResponse> bookingResponseList = List.of(new BookingResponse(), new BookingResponse());
        when(bookingService.getAllUserBookings(bookingRequestParams))
                .thenReturn(bookingResponseList);

        List<BookingResponse> responseDtoList = bookingController.getAllUserBookings(state, userId, from, size);

        assertEquals(bookingResponseList.size(), responseDtoList.size());
        verify(bookingService).getAllUserBookings(bookingRequestParams);
    }

    @Test
    void getAllOwnerBookings_whenParamIsCorrect_thenResponseContainsListOfBookings() {
        States state = States.ALL;
        long userId = 1L;
        int from = 0;
        int size = 5;
        BookingRequestParams bookingRequestParams = new BookingRequestParams(state, userId, from, size);
        List<BookingResponse> bookingResponseList = List.of(new BookingResponse(), new BookingResponse());
        when(bookingService.getAllOwnerBookings(bookingRequestParams))
                .thenReturn(bookingResponseList);

        List<BookingResponse> responseDtoList = bookingController.getAllOwnerBookings(state, userId, from, size);

        assertEquals(bookingResponseList.size(), responseDtoList.size());
        verify(bookingService).getAllOwnerBookings(bookingRequestParams);
    }

    @Test
    void updateBookingStatus_whenInvoked_thenBookingUpdate() {
        boolean approved = true;
        long bookingId = 1L;
        long ownerId = 1L;
        BookingResponse bookingResponse = new BookingResponse(
                1L,
                LocalDateTime.now().plusSeconds(2),
                LocalDateTime.now().plusSeconds(3),
                new ItemDtoOut(1L, "Садовая тачка", "Возит сама", true, null),
                new UserResponse(1L, "user", "user@yandex.ru"),
                BookingStatus.APPROVED
        );
        when(bookingService.updateBookingStatus(bookingId, approved, ownerId))
                .thenReturn(bookingResponse);

        BookingResponse responseDto = bookingController.updateBookingStatus(approved, bookingId, ownerId);

        assertEquals(bookingResponse, responseDto);
        verify(bookingService).updateBookingStatus(bookingId, approved, ownerId);
    }

    @Test
    void createBooking_whenBookerIdIsCorrect_thenUserCreated() {
        long bookerId = 1L;
        BookingRequest bookingRequest = new BookingRequest();
        BookingResponse bookingResponse = new BookingResponse();
        when(bookingService.create(bookingRequest, bookerId)).thenReturn(bookingResponse);

        BookingResponse responseDto = bookingController.createBooking(bookerId, bookingRequest);

        assertEquals(bookingResponse, responseDto);
        verify(bookingService).create(bookingRequest, bookerId);
    }
}