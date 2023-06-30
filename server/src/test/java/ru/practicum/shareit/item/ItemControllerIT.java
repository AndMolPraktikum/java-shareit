package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @SneakyThrows
    @Test
    void findItemById_whenItemExist_thenReturnItemAndStatusOk() {
        User user = new User("user1", "user1@user.com");
        userRepository.save(user);

        ItemDtoIn itemDto = new ItemDtoIn("Дрель", "Сверлит сама", true);
        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long itemId = objectMapper.readValue(response, ItemDtoOut.class).getId();

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value("" + itemId))
                .andExpect(content().string(containsString("Сверлит сама")));
    }

    @SneakyThrows
    @Test
    void findAllUserItems_whenInvoked_thenResponseContainsListOfItems() {
        UserRequest userRequest2 = new UserRequest("user2", "user2@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest2))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long userId = objectMapper.readValue(response, UserResponse.class).getId();

        ItemDtoIn itemDto = new ItemDtoIn("Швабра", "Моет сама", true);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("Моет сама")))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void searchItemByText_whenItemsExist_thenResponseContainsListOfItems() {
        UserRequest userRequest3 = new UserRequest("user3", "user3@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest3))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long userId = objectMapper.readValue(response, UserResponse.class).getId();

        ItemDtoIn itemDto = new ItemDtoIn("Дрель", "Сверлит сама", true);

        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", userId)
                .content(objectMapper.writeValueAsString(itemDto))
                .contentType("application/json"));
        mockMvc.perform(get("/items/search")
                        .param("text", "дРелЬ"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("Дрель")));
    }

    @SneakyThrows
    @Test
    void createItem_whenInvoked_thenCreateItem() {
        UserRequest userRequest4 = new UserRequest("user4", "user4@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest4))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long userId = objectMapper.readValue(response, UserResponse.class).getId();

        ItemDtoIn itemDto = new ItemDtoIn("Лопата", "Копает сама", true);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("Лопата")));
    }

    @SneakyThrows
    @Test
    void updateItem_whenInvoked_thenItemUpdated() {
        UserRequest userRequest5 = new UserRequest("user5", "user5@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest5))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long userId = objectMapper.readValue(response, UserResponse.class).getId();

        ItemDtoIn itemDto = new ItemDtoIn("Садовая тачка", "Возит сама", true);
        String responseItem = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Садовая тачка"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int id = Integer.parseInt(responseItem.substring(6, 7));

        itemDto = new ItemDtoIn("Садовая тачка с апгрейдом", "Всё сама делает", true);
        mockMvc.perform(patch("/items/{id}", id)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Садовая тачка с апгрейдом"));
    }

    @SneakyThrows
    @Test
    void deleteItem() {
        UserRequest userRequest6 = new UserRequest("user6", "user6@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest6))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long userId = objectMapper.readValue(response, UserResponse.class).getId();

        ItemDtoIn itemDto = new ItemDtoIn("Видеокамера", "Снимает сама", true);
        String responseItem = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int id = Integer.parseInt(responseItem.substring(6, 7));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().string(containsString("Видеокамера")));

        mockMvc.perform(delete("/items/{id}", id)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    public void createComment_whenInvoked_thenCommentCreated() {
        UserRequest owner = new UserRequest("owner", "owner@user.com");
        String responseOwner = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(owner))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long ownerId = objectMapper.readValue(responseOwner, UserResponse.class).getId();

        ItemDtoIn itemDto = new ItemDtoIn("Швабра", "Моет сама", true);
        String responseItem = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long itemId = objectMapper.readValue(responseItem, ItemDtoOut.class).getId();

        UserRequest booker = new UserRequest("booker", "booker@user.com");
        String responseBooker = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(booker))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long bookerId = objectMapper.readValue(responseBooker, UserResponse.class).getId();

        BookingRequest brDto = new BookingRequest(itemId,
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(brDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Thread.sleep(3000);

        CommentRequest commentRequest = new CommentRequest("Комментарий от юзера" + bookerId);
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", bookerId)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(content().string(containsString("Комментарий от юзера")))
                .andExpect(jsonPath("$.authorName").value("booker"))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}
