package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestParams;
import ru.practicum.shareit.enums.BookingStatus;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_whenUserAndItemFoundAndDateCorrect_thenBookingCreate() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user2, null);
        long bookerId = 1L;
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                item,
                user,
                BookingStatus.WAITING
        );
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingDtoIn.getItemId())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOut response = bookingService.create(bookingDtoIn, bookerId);

        assertEquals(BookingMapper.toBookingResponseDto(booking), response);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingDtoIn.getItemId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_whenStartAfterEnd_thenBookingWrongTimeExceptionThrown() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusSeconds(2),
                LocalDateTime.now().plusSeconds(1)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user2, null);
        String message = "Время окончания бронирования не может быть раньше времени начала бронирования";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingDtoIn.getItemId())).thenReturn(item);

        BookingWrongTimeException response = assertThrows(BookingWrongTimeException.class,
                () -> bookingService.create(bookingDtoIn, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingDtoIn.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenStartEqualsEnd_thenBookingWrongTimeExceptionThrown() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(1)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user2, null);
        String message = "Время начала и окончания бронирования не может быть равно";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingDtoIn.getItemId())).thenReturn(item);

        BookingWrongTimeException response = assertThrows(BookingWrongTimeException.class,
                () -> bookingService.create(bookingDtoIn, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingDtoIn.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenStartBeforeCurrentTime_thenBookingWrongTimeExceptionThrown() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().minusSeconds(1)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user2, null);
        String message = "Время начала или окончания бронирования не может быть раньше текущего времени";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingDtoIn.getItemId())).thenReturn(item);

        BookingWrongTimeException response = assertThrows(BookingWrongTimeException.class,
                () -> bookingService.create(bookingDtoIn, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingDtoIn.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemUnavailable_thenItemIsUnavailableExceptionThrown() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", false, user2, null);
        String message = "Запрашиваемая вещь с ID: 1 недоступна для бронирования";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingDtoIn.getItemId())).thenReturn(item);

        ItemIsUnavailableException response = assertThrows(ItemIsUnavailableException.class,
                () -> bookingService.create(bookingDtoIn, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingDtoIn.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenRequesterIsOwner_thenItemNotFoundExceptionThrown() {
        BookingDtoIn bookingDtoIn = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", false, user, null);
        String message = "Вы пытаетесь забронировать собственную вещь!";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingDtoIn.getItemId())).thenReturn(item);

        ItemNotFoundException response = assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(bookingDtoIn, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingDtoIn.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemIsAlreadyBooked_thenItemIsUnavailableExceptionThrown() {
        BookingDtoIn brd = new BookingDtoIn(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", false, user, null);
        String message = "Запрашиваемая вещь с ID: 1 забронирована на указанное время";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(brd.getItemId())).thenReturn(item);
        when(bookingRepository.isAvailableForBooking(brd.getItemId(), brd.getStart(), brd.getEnd())).thenReturn(true);

        ItemIsUnavailableException response = assertThrows(ItemIsUnavailableException.class,
                () -> bookingService.create(brd, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(brd.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_whenBookingFoundStatusWaitingApprovedTrue_thenBookingStatusUpdateToApproved() {
        long bookingId = 1L;
        boolean approved = true;
        long ownerId = 2L;
        User owner = new User(2L, "owner", "owner@yandex.ru");
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.WAITING
        );
        Booking bookingOut = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemService.checkUserItem(ownerId, booking.getItem().getId())).thenReturn(null);
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingOut);

        BookingDtoOut response = bookingService.updateBookingStatus(bookingId, approved, ownerId);

        assertEquals(BookingMapper.toBookingResponseDto(bookingOut), response);
        verify(bookingRepository).findById(bookingId);
        verify(itemService).checkUserItem(ownerId, booking.getItem().getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_whenBookingFoundStatusWaitingApprovedFalse_thenBookingStatusUpdateToRejected() {
        long bookingId = 1L;
        boolean approved = false;
        long ownerId = 2L;
        User owner = new User(2L, "owner", "owner@yandex.ru");
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.WAITING
        );
        Booking bookingOut = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.REJECTED
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemService.checkUserItem(ownerId, booking.getItem().getId())).thenReturn(null);
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingOut);

        BookingDtoOut response = bookingService.updateBookingStatus(bookingId, approved, ownerId);

        assertEquals(BookingMapper.toBookingResponseDto(bookingOut), response);
        verify(bookingRepository).findById(bookingId);
        verify(itemService).checkUserItem(ownerId, booking.getItem().getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateBookingStatus_whenBookingFoundStatusNotWaiting_thenBookingAlreadyVerifiedByOwnerExceptionThrown() {
        long bookingId = 1L;
        boolean approved = true;
        long ownerId = 2L;
        User owner = new User(2L, "owner", "owner@yandex.ru");
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyVerifiedByOwnerException.class,
                () -> bookingService.updateBookingStatus(bookingId, approved, ownerId));
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).save(any(Booking.class));
    }


    @Test
    void getBookingByIdForOwnerOrAuthor_whenRequesterCorrect_thenResponseContainsBooking() {
        long bookingId = 1L;
        long userId = 2L;
        User owner = new User(2L, "owner", "owner@yandex.ru");
        Booking bookingOut = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        when(userService.getUserDtoById(userId)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingOut));

        BookingDtoOut response = bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId);

        assertEquals(BookingMapper.toBookingResponseDto(bookingOut), response);
        verify(bookingRepository).findById(bookingId);
        verify(userService).getUserDtoById(userId);
    }

    @Test
    void getBookingByIdForOwnerOrAuthor_whenBookingNotFound_thenBookingNotFoundExceptionThrown() {
        long bookingId = 1L;
        long userId = 2L;
        when(userService.getUserDtoById(userId)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService
                .getBookingByIdForOwnerOrAuthor(bookingId, userId));

        verify(userService).getUserDtoById(userId);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingByIdForOwnerOrAuthor_whenUserNotRelevantBooking_thenUserHasNoLinkBookingOrItemExceptionThrown() {
        long bookingId = 1L;
        long userId = 3L;
        User owner = new User(2L, "owner", "owner@yandex.ru");
        Booking bookingOut = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        when(userService.getUserDtoById(userId)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingOut));

        assertThrows(UserHasNoLinkBookingOrItemException.class, () -> bookingService
                .getBookingByIdForOwnerOrAuthor(bookingId, userId));

        verify(userService).getUserDtoById(userId);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getAllUserBookings_whenStateAll_thenResponseContainsListOfBookings() {
        BookingRequestParams brp = new BookingRequestParams(States.ALL, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4),
                new Item(1L, "Самокат",
                        "Сам едет", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        List<Booking> bookingList = List.of(booking1, booking2);
        PageRequest page = PageRequest.of(brp.getFrom(), brp.getSize());
        when(userService.getUserDtoById(brp.getUserId())).thenReturn(null);
        when(bookingRepository.findByBookerIdOrderByStartDesc(brp.getUserId(), page)).thenReturn(bookingList);

        List<BookingDtoOut> responseList = bookingService.getAllUserBookings(brp);

        assertEquals((BookingMapper.toBookingResponseDtoList(bookingList)).size(), responseList.size());
        verify(userService).getUserDtoById(anyLong());
        verify(bookingRepository).findByBookerIdOrderByStartDesc(brp.getUserId(), page);
    }

    @Test
    void getAllUserBookings_whenStateUnknown_thenFailStateExceptionThrown() {
        BookingRequestParams brp = new BookingRequestParams(States.UNSUPPORTED_STATUS, 1L, 0, 5);
        when(userService.getUserDtoById(brp.getUserId())).thenReturn(null);

        assertThrows(FailStateException.class, () -> bookingService.getAllUserBookings(brp));

        verify(userService).getUserDtoById(anyLong());
    }

    @Test
    void getAllOwnerBookings_whenStateAll_thenResponseContainsListOfBookings() {
        BookingRequestParams brp = new BookingRequestParams(States.ALL, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4),
                new Item(1L, "Самокат",
                        "Сам едет", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        List<Booking> bookingList = List.of(booking1, booking2);
        PageRequest page = PageRequest.of(brp.getFrom(), brp.getSize());
        when(userService.getUserDtoById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdAll(brp.getUserId(), page)).thenReturn(bookingList);

        List<BookingDtoOut> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseDtoList(bookingList)).size(), responseList.size());
        verify(userService).getUserDtoById(anyLong());
        verify(bookingRepository).findByOwnerIdAll(brp.getUserId(), page);
    }

    @Test
    void getAllOwnerBookings_whenStateCurrent_thenResponseContainsListOfBookings() {
        BookingRequestParams brp = new BookingRequestParams(States.CURRENT, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4),
                new Item(1L, "Самокат",
                        "Сам едет", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                BookingStatus.APPROVED
        );
        List<Booking> bookingList = List.of(booking1, booking2);
        PageRequest page = PageRequest.of(brp.getFrom(), brp.getSize());
        when(userService.getUserDtoById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdCurrent(brp.getUserId(), page)).thenReturn(bookingList);

        List<BookingDtoOut> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseDtoList(bookingList)).size(), responseList.size());
        verify(userService).getUserDtoById(anyLong());
        verify(bookingRepository).findByOwnerIdCurrent(brp.getUserId(), page);
    }

    @Test
    void getAllOwnerBookings_whenStateUnknown_thenFailStateExceptionThrown() {
        BookingRequestParams brp = new BookingRequestParams(States.UNSUPPORTED_STATUS, 1L, 0, 5);
        when(userService.getUserDtoById(anyLong())).thenReturn(null);

        assertThrows(FailStateException.class, () -> bookingService.getAllOwnerBookings(brp));

        verify(userService).getUserDtoById(anyLong());
    }
}