package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.GatewayBookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.BookingWrongTimeException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingById(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllUserBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllOwnerBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createBookingItem(long userId, GatewayBookingRequest bookingRequest) {
        LocalDateTime start = bookingRequest.getStart();
        LocalDateTime end = bookingRequest.getEnd();

        if (start.isAfter(end)) {
            log.error("Время окончания бронирования не может быть раньше времени начала бронирования");
            throw new BookingWrongTimeException("Время окончания бронирования " +
                    "не может быть раньше времени начала бронирования");
        }
        if (start.equals(end)) {
            log.error("Время начала и окончания бронирования не может быть равно");
            throw new BookingWrongTimeException("Время начала и окончания бронирования не может быть равно");
        }
        return post("", userId, bookingRequest);
    }

    public ResponseEntity<Object> updateBookingStatus(long bookingId, boolean approved, long ownerId) {
//        Map<String, Object> parameters = Map.of(
//                "approved", approved);
        return patch("/" + bookingId + "?approved=" + approved, ownerId);
    }
}
