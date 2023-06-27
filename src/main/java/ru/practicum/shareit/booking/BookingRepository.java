package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 " +
            "AND b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByBookerIdCurrent(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByBookerIdPast(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByBookerIdFuture(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findByBookerIdWaiting(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findByBookerIdRejected(long bookerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 " +
            "AND b.start < CURRENT_TIMESTAMP AND b.end > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdCurrent(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdPast(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findByOwnerIdFuture(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findByOwnerIdWaiting(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> findByOwnerIdRejected(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAll(long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.start < CURRENT_TIMESTAMP ORDER BY b.end DESC")
    List<Booking> findLastBookingForItem(long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.start > CURRENT_TIMESTAMP ORDER BY b.end ASC")
    List<Booking> findNextBookingForItem(long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?2 AND b.booker.id = ?1 AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findAllByBookerIdAndItemIdAndAfterEnd(long bookerId, long itemId);

    @Query("SELECT (count(b) > 0) " +
            "FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "  AND b.status = 'APPROVED' " +
            "  AND b.start <= ?3 " +
            "  AND b.end >= ?2")
    boolean isAvailableForBooking(Long itemId, LocalDateTime start, LocalDateTime end);
}
