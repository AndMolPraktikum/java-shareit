package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
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
import static ru.practicum.shareit.enums.BookingStatus.APPROVED;


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
        BookingRequest bookingRequest = new BookingRequest(
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
        when(itemService.getItemById(bookingRequest.getItemId())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.create(bookingRequest, bookerId);

        assertEquals(BookingMapper.toBookingResponse(booking), response);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingRequest.getItemId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void create_whenStartAfterEnd_thenBookingWrongTimeExceptionThrown() {
        BookingRequest bookingRequest = new BookingRequest(
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
        when(itemService.getItemById(bookingRequest.getItemId())).thenReturn(item);

        BookingWrongTimeException response = assertThrows(BookingWrongTimeException.class,
                () -> bookingService.create(bookingRequest, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingRequest.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenStartEqualsEnd_thenBookingWrongTimeExceptionThrown() {
        LocalDateTime localDateTime = LocalDateTime.now();
        BookingRequest bookingRequest = new BookingRequest(
                1L,
                localDateTime.plusSeconds(1),
                localDateTime.plusSeconds(1)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user2, null);
        String message = "Время начала и окончания бронирования не может быть равно";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingRequest.getItemId())).thenReturn(item);

        BookingWrongTimeException response = assertThrows(BookingWrongTimeException.class,
                () -> bookingService.create(bookingRequest, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingRequest.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenStartBeforeCurrentTime_thenBookingWrongTimeExceptionThrown() {
        BookingRequest bookingRequest = new BookingRequest(
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
        when(itemService.getItemById(bookingRequest.getItemId())).thenReturn(item);

        BookingWrongTimeException response = assertThrows(BookingWrongTimeException.class,
                () -> bookingService.create(bookingRequest, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingRequest.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenEndBeforeCurrentTime_thenBookingWrongTimeExceptionThrown() {
        BookingRequest bookingRequest = new BookingRequest(
                1L,
                LocalDateTime.now().minusSeconds(1),
                LocalDateTime.now().plusSeconds(1)
        );
        long bookerId = 1L;
        User user = new User(1L, "user", "user@yandex.ru");
        User user2 = new User(2L, "user2", "user2@yandex.ru");
        Item item = new Item(1L, "Садовая тачка",
                "Возит сама", true, user2, null);
        String message = "Время начала или окончания бронирования не может быть раньше текущего времени";
        when(userService.getUserById(bookerId)).thenReturn(user);
        when(itemService.getItemById(bookingRequest.getItemId())).thenReturn(item);

        BookingWrongTimeException response = assertThrows(BookingWrongTimeException.class,
                () -> bookingService.create(bookingRequest, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingRequest.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemUnavailable_thenItemIsUnavailableExceptionThrown() {
        BookingRequest bookingRequest = new BookingRequest(
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
        when(itemService.getItemById(bookingRequest.getItemId())).thenReturn(item);

        ItemIsUnavailableException response = assertThrows(ItemIsUnavailableException.class,
                () -> bookingService.create(bookingRequest, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingRequest.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenRequesterIsOwner_thenItemNotFoundExceptionThrown() {
        BookingRequest bookingRequest = new BookingRequest(
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
        when(itemService.getItemById(bookingRequest.getItemId())).thenReturn(item);

        ItemNotFoundException response = assertThrows(ItemNotFoundException.class,
                () -> bookingService.create(bookingRequest, bookerId));

        String responseMessage = response.getMessage();
        assertEquals(message, responseMessage);
        verify(userService).getUserById(bookerId);
        verify(itemService).getItemById(bookingRequest.getItemId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void create_whenItemIsAlreadyBooked_thenItemIsUnavailableExceptionThrown() {
        BookingRequest brd = new BookingRequest(
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
                APPROVED
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemService.checkUserItem(ownerId, booking.getItem().getId())).thenReturn(null);
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingOut);

        BookingResponse response = bookingService.updateBookingStatus(bookingId, approved, ownerId);

        assertEquals(BookingMapper.toBookingResponse(bookingOut), response);
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

        BookingResponse response = bookingService.updateBookingStatus(bookingId, approved, ownerId);

        assertEquals(BookingMapper.toBookingResponse(bookingOut), response);
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
                APPROVED
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
                APPROVED
        );
        when(userService.getUserById(userId)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingOut));

        BookingResponse response = bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId);

        assertEquals(BookingMapper.toBookingResponse(bookingOut), response);
        verify(bookingRepository).findById(bookingId);
        verify(userService).getUserById(userId);
    }

    @Test
    void getBookingByIdForOwnerOrAuthor_whenRequesterCorrectAndUserIdEquals1_thenResponseContainsBooking() {
        long bookingId = 1L;
        long userId = 1L;
        User owner = new User(2L, "owner", "owner@yandex.ru");
        Booking bookingOut = new Booking(
                1L,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2),
                new Item(1L, "Садовая тачка",
                        "Возит сама", true, owner, null),
                new User(1L, "booker", "booker@yandex.ru"),
                APPROVED
        );
        when(userService.getUserById(userId)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingOut));

        BookingResponse response = bookingService.getBookingByIdForOwnerOrAuthor(bookingId, userId);

        assertEquals(BookingMapper.toBookingResponse(bookingOut), response);
        verify(bookingRepository).findById(bookingId);
        verify(userService).getUserById(userId);
    }

    @Test
    void getBookingByIdForOwnerOrAuthor_whenBookingNotFound_thenBookingNotFoundExceptionThrown() {
        long bookingId = 1L;
        long userId = 2L;
        when(userService.getUserById(userId)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService
                .getBookingByIdForOwnerOrAuthor(bookingId, userId));

        verify(userService).getUserById(userId);
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
                APPROVED
        );
        when(userService.getUserById(userId)).thenReturn(null);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingOut));

        assertThrows(UserHasNoLinkBookingOrItemException.class, () -> bookingService
                .getBookingByIdForOwnerOrAuthor(bookingId, userId));

        verify(userService).getUserById(userId);
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
                APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4),
                new Item(1L, "Самокат",
                        "Сам едет", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                APPROVED
        );
        List<Booking> bookingList = List.of(booking1, booking2);
        PageRequest page = PageRequest.of(brp.getFrom(), brp.getSize());
        when(userService.getUserById(brp.getUserId())).thenReturn(null);
        when(bookingRepository.findByBookerIdOrderByStartDesc(brp.getUserId(), page)).thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllUserBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByBookerIdOrderByStartDesc(brp.getUserId(), page);
    }

    @Test
    void getAllUserBookings_whenStateCurrent_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.CURRENT, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(brp.getUserId())).thenReturn(null);
        when(bookingRepository.findByBookerIdCurrent(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllUserBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByBookerIdCurrent(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllUserBookings_whenStatePast_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.PAST, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(brp.getUserId())).thenReturn(null);
        when(bookingRepository.findByBookerIdPast(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllUserBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByBookerIdPast(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllUserBookings_whenStateFuture_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.FUTURE, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(brp.getUserId())).thenReturn(null);
        when(bookingRepository.findByBookerIdFuture(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllUserBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByBookerIdFuture(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllUserBookings_whenStateWaiting_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.WAITING, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(brp.getUserId())).thenReturn(null);
        when(bookingRepository.findByBookerIdWaiting(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllUserBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByBookerIdWaiting(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllUserBookings_whenStateRejected_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.REJECTED, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(brp.getUserId())).thenReturn(null);
        when(bookingRepository.findByBookerIdRejected(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllUserBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByBookerIdRejected(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllUserBookings_whenStateUnknown_thenFailStateExceptionThrown() {
        BookingRequestParams brp = new BookingRequestParams(States.UNSUPPORTED_STATUS, 1L, 0, 5);
        when(userService.getUserById(brp.getUserId())).thenReturn(null);

        assertThrows(FailStateException.class, () -> bookingService.getAllUserBookings(brp));

        verify(userService).getUserById(anyLong());
    }

    @Test
    void getAllUserBookings_whenStateUnknownAndFromEquals1_thenFailStateExceptionThrown() {
        BookingRequestParams brp = new BookingRequestParams(States.UNSUPPORTED_STATUS, 1L, 1, 5);
        when(userService.getUserById(brp.getUserId())).thenReturn(null);

        assertThrows(FailStateException.class, () -> bookingService.getAllUserBookings(brp));

        verify(userService).getUserById(anyLong());
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
                APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4),
                new Item(1L, "Самокат",
                        "Сам едет", true, new User(), null),
                new User(1L, "booker", "booker@yandex.ru"),
                APPROVED
        );
        List<Booking> bookingList = List.of(booking1, booking2);
        PageRequest page = PageRequest.of(brp.getFrom(), brp.getSize());
        when(userService.getUserById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdAll(brp.getUserId(), page)).thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByOwnerIdAll(brp.getUserId(), page);
    }

    @Test
    void getAllOwnerBookings_whenStateCurrent_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.CURRENT, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdCurrent(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByOwnerIdCurrent(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllOwnerBookings_whenStatePast_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.PAST, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdPast(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByOwnerIdPast(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllOwnerBookings_whenStateFuture_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.FUTURE, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdFuture(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByOwnerIdFuture(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllOwnerBookings_whenStateWaiting_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.WAITING, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdWaiting(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByOwnerIdWaiting(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllOwnerBookings_whenStateRejected_thenResponseContainsListOfBookings() {
        LocalDateTime ldt = LocalDateTime.now();
        User booker = new User(1L, "booker", "booker@yandex.ru");
        Item item = new Item(1L, "Вещь", "Описание", true, new User(), null);
        BookingRequestParams brp = new BookingRequestParams(States.REJECTED, 1L, 0, 5);
        Booking booking1 = new Booking(
                1L,
                ldt.plusSeconds(1),
                ldt.plusSeconds(2),
                item,
                booker,
                APPROVED);
        Booking booking2 = new Booking(
                1L,
                ldt.plusSeconds(3),
                ldt.plusSeconds(4),
                item,
                booker,
                APPROVED);
        List<Booking> bookingList = List.of(booking1, booking2);
        when(userService.getUserById(anyLong())).thenReturn(null);
        when(bookingRepository.findByOwnerIdRejected(eq(brp.getUserId()), any(PageRequest.class)))
                .thenReturn(bookingList);

        List<BookingResponse> responseList = bookingService.getAllOwnerBookings(brp);

        assertEquals((BookingMapper.toBookingResponseList(bookingList)).size(), responseList.size());
        verify(userService).getUserById(anyLong());
        verify(bookingRepository).findByOwnerIdRejected(eq(brp.getUserId()), any(PageRequest.class));
    }

    @Test
    void getAllOwnerBookings_whenStateUnknown_thenFailStateExceptionThrown() {
        BookingRequestParams brp = new BookingRequestParams(States.UNSUPPORTED_STATUS, 1L, 0, 5);
        when(userService.getUserById(anyLong())).thenReturn(null);

        assertThrows(FailStateException.class, () -> bookingService.getAllOwnerBookings(brp));

        verify(userService).getUserById(anyLong());
    }

    @Test
    void getAllOwnerBookings_whenStateUnknownAndFromEquals1_thenFailStateExceptionThrown() {
        BookingRequestParams brp = new BookingRequestParams(States.UNSUPPORTED_STATUS, 1L, 1, 5);
        when(userService.getUserById(anyLong())).thenReturn(null);

        assertThrows(FailStateException.class, () -> bookingService.getAllOwnerBookings(brp));

        verify(userService).getUserById(anyLong());
    }
}