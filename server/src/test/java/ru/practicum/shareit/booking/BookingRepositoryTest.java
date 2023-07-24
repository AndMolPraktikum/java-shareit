package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item1;
    private Item item2;
    User user1;
    User user2;
    LocalDateTime ldt;

    private final PageRequest page = PageRequest.of(0, 5);


    @BeforeEach
    public void init() {
        ldt = LocalDateTime.now();
        user1 = userRepository.save(new User("user1", "user1@yandex.ru"));
        user2 = userRepository.save(new User("user2", "user2@yandex.ru"));

        item1 = itemRepository.save(Item.builder()
                .name("Дрель")
                .description("Чтобы сверлить")
                .available(true)
                .owner(user2)
                .build());

        item2 = itemRepository.save(Item.builder()
                .name("Бинокль")
                .description("Чтобы смотреть")
                .available(true)
                .owner(user2)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.minusSeconds(10))
                .end(ldt.minusSeconds(9))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.REJECTED)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.minusSeconds(8))
                .end(ldt.minusSeconds(7))
                .item(item1)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.minusSeconds(4))
                .end(ldt.minusSeconds(3))
                .item(item2)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.minusSeconds(2))
                .end(ldt.minusSeconds(1))
                .item(item2)
                .booker(user1)
                .status(BookingStatus.REJECTED)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.plusSeconds(1))
                .end(ldt.plusSeconds(2))
                .item(item2)
                .booker(user1)
                .status(BookingStatus.APPROVED)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.minusSeconds(1))
                .end(ldt.plusSeconds(2))
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.plusSeconds(3))
                .end(ldt.plusSeconds(4))
                .item(item1)
                .booker(user1)
                .status(BookingStatus.WAITING)
                .build());

        bookingRepository.save(Booking.builder()
                .start(ldt.plusSeconds(4))
                .end(ldt.plusSeconds(5))
                .item(item2)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build());
    }

    @AfterEach
    public void clearBase() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        List<Booking> responseList = bookingRepository.findByBookerIdOrderByStartDesc(user1.getId(), page);

        assertEquals(4, responseList.size());
    }

    @Test
    void findByBookerIdCurrent() {
        List<Booking> responseList = bookingRepository.findByBookerIdCurrent(user2.getId(), page);

        assertEquals(1, responseList.size());
    }

    @Test
    void findByBookerIdPast() {
        List<Booking> responseList = bookingRepository.findByBookerIdPast(user1.getId(), page);

        assertEquals(2, responseList.size());
    }

    @Test
    void findByBookerIdFuture() {
        List<Booking> responseList = bookingRepository.findByBookerIdFuture(user2.getId(), page);

        assertEquals(1, responseList.size());
    }

    @Test
    void findByBookerIdWaiting() {
        List<Booking> responseList = bookingRepository.findByBookerIdWaiting(user1.getId(), page);

        assertEquals(1, responseList.size());
    }

    @Test
    void findByBookerIdRejected() {
        List<Booking> responseList = bookingRepository.findByBookerIdRejected(user1.getId(), page);

        assertEquals(2, responseList.size());
    }

    @Test
    void findByOwnerIdCurrent() {
        List<Booking> responseList = bookingRepository.findByOwnerIdCurrent(user2.getId(), page);

        assertEquals(1, responseList.size());
    }

    @Test
    void findByOwnerIdPast() {
        List<Booking> responseList = bookingRepository.findByOwnerIdPast(user2.getId(), page);

        assertEquals(4, responseList.size());
    }

    @Test
    void findByOwnerIdFuture() {
        List<Booking> responseList = bookingRepository.findByOwnerIdFuture(user2.getId(), page);

        assertEquals(3, responseList.size());
    }

    @Test
    void findByOwnerIdWaiting() {
        List<Booking> responseList = bookingRepository.findByOwnerIdWaiting(user2.getId(), page);

        assertEquals(3, responseList.size());
    }

    @Test
    void findByOwnerIdRejected() {
        List<Booking> responseList = bookingRepository.findByOwnerIdRejected(user2.getId(), page);

        assertEquals(2, responseList.size());
    }

    @Test
    void findByOwnerIdAll() {
        List<Booking> responseList = bookingRepository.findByOwnerIdAll(user2.getId(), page);

        assertEquals(5, responseList.size());
    }

    @Test
    void findLastBookingForItem() {
        List<Booking> responseList = bookingRepository.findLastBookingForItem(item2.getId());

        assertEquals(2, responseList.size());
    }

    @Test
    void findNextBookingForItem() {
        List<Booking> responseList = bookingRepository.findNextBookingForItem(item1.getId());

        assertEquals(1, responseList.size());
    }

    @Test
    void findAllByBookerIdAndItemIdAndAfterEnd() {
        List<Booking> responseList = bookingRepository
                .findAllByBookerIdAndItemIdAndAfterEnd(user2.getId(), item1.getId());

        assertEquals(1, responseList.size());
    }

    @Test
    void isAvailableForBooking_whenTimeUnavailable() {
        boolean isAvailable =
                bookingRepository.isAvailableForBooking(item2.getId(), ldt.plusSeconds(1), ldt.plusSeconds(3));

        assertTrue(isAvailable);
    }

    @Test
    void isAvailableForBooking_whenTimeAvailable() {
        boolean isAvailable =
                bookingRepository.isAvailableForBooking(item2.getId(), ldt.plusSeconds(6), ldt.plusSeconds(6));

        assertFalse(isAvailable);
    }
}