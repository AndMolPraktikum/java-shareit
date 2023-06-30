package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.GatewayBookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.FailStateException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class GatewayBookingController {
	private final BookingClient bookingClient;

	@GetMapping("/{bookingId}")  //getBookingById
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info("Входящий запрос GET /bookings/{}, userId={}", bookingId, userId);
		final ResponseEntity<Object> bookingResponse = bookingClient.getBookingById(userId, bookingId);
		log.info("Исходящий ответ: {}", bookingResponse);
		return bookingResponse;
	}

	@GetMapping
	public ResponseEntity<Object> getAllUserBookings(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new FailStateException("Unknown state: " + stateParam));
		log.info("Входящий запрос GET /bookings?state={}from={}&size={}. ID пользователя: {}",
				stateParam, from, size, userId);
		final ResponseEntity<Object> allUserBookingsList = bookingClient.getAllUserBookings(userId, state, from, size);
		log.info("Исходящий ответ: {}", allUserBookingsList);
		return allUserBookingsList;
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllOwnerBookings(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
			@RequestParam(name = "size", defaultValue = "5") @Min(1) int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new FailStateException("Unknown state: " + stateParam));
		log.info("Входящий запрос GET /bookings/owner?state={}from={}&size={}. ID владельца: {}",
				stateParam, from, size, userId);
		final ResponseEntity<Object> allOwnerBookingsList = bookingClient.getAllOwnerBookings(userId, state, from, size);
		log.info("Исходящий ответ: {}", allOwnerBookingsList);
		return allOwnerBookingsList;
	}

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
												@Valid @RequestBody GatewayBookingRequest bookingRequest) {
		log.info("Входящий запрос POST /bookings. ID пользователя: {}.  BookingRequest: {}", userId, bookingRequest);
		final ResponseEntity<Object> createdBooking = bookingClient.createBookingItem(userId, bookingRequest);
		log.info("Исходящий ответ: {}", createdBooking);
		return createdBooking;
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBookingStatus(@RequestParam boolean approved,
													  @PathVariable long bookingId,
													  @RequestHeader("X-Sharer-User-Id") long ownerId) {
		log.info("Входящий запрос GET /bookings/{}?approved={}. ID владельца: {}", bookingId, approved, ownerId);
		final ResponseEntity<Object> bookingResponse = bookingClient.updateBookingStatus(bookingId, approved, ownerId);
		log.info("Исходящий ответ: {}", bookingResponse);
		return bookingResponse;
	}
}
