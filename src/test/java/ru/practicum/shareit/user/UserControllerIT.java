package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnTwoUsers() throws Exception {
        UserDto userDto = new UserDto("user1", "user1@user.com");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        UserDto userDto2 = new UserDto("user2", "user2@user.com");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto2))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("user1@user.com")))
                .andExpect(content().string(containsString("user2@user.com")));
    }

    @Test
    void shouldReturnUser() throws Exception {
        UserDto user3Dto = new UserDto("user3", "use3@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user3Dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = Integer.parseInt(response.substring(6, 7));


        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value("" + id));
    }

    @SneakyThrows
    @Test
    void shouldReturnStatusNotFound() {
        mockMvc.perform(get("/users/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

    }

    @SneakyThrows
    @Test
    void shouldCreateUser() {
        UserDto user4Dto = new UserDto("user4", "user4@user.com");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user4Dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("user4@user.com")));
    }

    @Test
    void shouldNotCreateUserWithDuplicateEmail() throws Exception {
        UserDto user5Dto = new UserDto("user5", "user5@user.com");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user5Dto))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        UserDto userDto2 = new UserDto("user", "user5@user.com");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto2))
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserDto user6Dto = new UserDto("user6", "user6@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user6Dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = Integer.parseInt(response.substring(6, 7));

        UserDto userDto = new UserDto("update6", "update6@user.com");

        mockMvc.perform(patch("/users/{id}", id)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value("" + id))
                .andExpect(jsonPath("$.name").value("update6"))
                .andExpect(jsonPath("$.email").value("update6@user.com"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserDto user7Dto = new UserDto("user7", "user7@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user7Dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        int id = Integer.parseInt(response.substring(6, 7));

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }
}