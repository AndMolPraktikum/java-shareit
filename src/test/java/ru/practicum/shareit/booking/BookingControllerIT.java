package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    public long ownerId;
    public long itemId;
    long bookerId;

    @SneakyThrows
    @BeforeEach
    public void init() {
        UserDto owner = new UserDto("owner", "owner@user.com");
        String responseOwner = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(owner))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ownerId = objectMapper.readValue(responseOwner, UserDto.class).getId();

        ItemDtoIn itemDto = new ItemDtoIn("Швабра", "Моет сама", true);
        String responseItem = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        itemId = objectMapper.readValue(responseItem, ItemDtoOut.class).getId();

        UserDto booker = new UserDto("booker", "booker@user.com");
        String responseBooker = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(booker))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        bookerId = objectMapper.readValue(responseBooker, UserDto.class).getId();
    }

    @AfterEach
    public void clear() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void createBooking_whenBookerIdAndItemIdIsCorrect_thenUserCreated() {
        BookingDtoIn brDto = new BookingDtoIn(itemId,
                LocalDateTime.now().plusMinutes(2),
                LocalDateTime.now().plusMinutes(5));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(brDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void updateBookingStatus_whenBookingFoundStatusWaitingApprovedTrue_thenBookingStatusUpdateToApproved() {
        BookingDtoIn brDto = new BookingDtoIn(itemId,
                LocalDateTime.now().plusMinutes(2),
                LocalDateTime.now().plusMinutes(5));

        String responseBooking = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(brDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long bookingId = objectMapper.readValue(responseBooking, BookingDtoOut.class).getId();

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", "true")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("" + bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @SneakyThrows
    void getAllOwnerBookings_whenStateDefault_thenResponseContainsListOfBookings() {
        BookingDtoIn brDto = new BookingDtoIn(itemId,
                LocalDateTime.now().plusMinutes(2),
                LocalDateTime.now().plusMinutes(5));

        String responseBooking = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(brDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long bookingId = objectMapper.readValue(responseBooking, BookingDtoOut.class).getId();

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("" + bookingId))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    @SneakyThrows
    void getAllUserBookings_whenTimeIsCorrect_thenResponseContainsListOfBookings() {
        BookingDtoIn brDto = new BookingDtoIn(itemId,
                LocalDateTime.now().plusMinutes(2),
                LocalDateTime.now().plusMinutes(5));

        String responseBooking = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(brDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long bookingId = objectMapper.readValue(responseBooking, BookingDtoOut.class).getId();

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("" + bookingId))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    @SneakyThrows
    void getBookingById_whenUserIdFound_thenResponseContainsBooking() {
        BookingDtoIn brDto = new BookingDtoIn(itemId,
                LocalDateTime.now().plusMinutes(2),
                LocalDateTime.now().plusMinutes(5));

        String responseBooking = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(brDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long bookingId = objectMapper.readValue(responseBooking, BookingDtoOut.class).getId();

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("" + bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }
}