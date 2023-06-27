package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserRequest;

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
        UserRequest userRequest1 = new UserRequest("user1", "user1@user.com");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest1))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        UserRequest userRequest2 = new UserRequest("user2", "user2@user.com");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest2))
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
        UserRequest userRequest3 = new UserRequest("user3", "use3@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest3))
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
        UserRequest userRequest4 = new UserRequest("user4", "user4@user.com");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest4))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().string(containsString("user4@user.com")));
    }

    @Test
    void shouldNotCreateUserWithDuplicateEmail() throws Exception {
        UserRequest userRequest5 = new UserRequest("user5", "user5@user.com");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest5))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        UserRequest userRequest51 = new UserRequest("user", "user5@user.com");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest51))
                        .contentType("application/json"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserRequest userRequest6 = new UserRequest("user6", "user6@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest6))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int id = Integer.parseInt(response.substring(6, 7));

        UserRequest userRequestUpdate = new UserRequest("update6", "update6@user.com");

        mockMvc.perform(patch("/users/{id}", id)
                        .content(objectMapper.writeValueAsString(userRequestUpdate))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value("" + id))
                .andExpect(jsonPath("$.name").value("update6"))
                .andExpect(jsonPath("$.email").value("update6@user.com"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserRequest userRequest7 = new UserRequest("user7", "user7@user.com");
        String response = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userRequest7))
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