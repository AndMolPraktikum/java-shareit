package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        User user = new User("user1", "user1@user.com");
        User user2 = new User("user2", "user2@user.com");
        User user3 = new User("user3", "user3@user.com");
        userService.createUser(user);
        userService.createUser(user2);
        userService.createUser(user3);
    }

    @Test
    void findItemById() throws Exception {
        ItemDto itemDto = new ItemDto("Дрель", "Сверлит сама", true);
        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int id = Integer.parseInt(response.substring(6, 7));

        mockMvc.perform(get("/items/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value("" + id))
                .andExpect(content().string(containsString("Сверлит сама")));
    }

    @Test
    void findAllUserItems() throws Exception {
        ItemDto itemDto = new ItemDto("Швабра", "Моет сама", true);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("Моет сама")))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchItemByText() throws Exception {
        ItemDto itemDto = new ItemDto("Дрель", "Сверлит сама", true);
        mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1)
                .content(objectMapper.writeValueAsString(itemDto))
                .contentType("application/json"));

        mockMvc.perform(get("/items/search")
                        .param("text", "дРелЬ"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("Дрель")));
    }

    @Test
    void shouldCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto("Лопата", "Копает сама", true);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("Лопата")));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemDto = new ItemDto("Садовая тачка", "Возит сама", true);
        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Садовая тачка"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int id = Integer.parseInt(response.substring(6, 7));
        itemDto = new ItemDto("Садовая тачка с апгрейдом", "Всё сама делает", true);

        mockMvc.perform(patch("/items/{id}", id)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Садовая тачка с апгрейдом"));
    }

    @Test
    void deleteItem() throws Exception {
        ItemDto itemDto = new ItemDto("Видеокамера", "Снимает сама", true);
        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 3)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType("application/json"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int id = Integer.parseInt(response.substring(6, 7));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().string(containsString("Видеокамера")));

        mockMvc.perform(delete("/items/{id}", id)
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", 3))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}