package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        log.info("Входящий запрос GET /users.");
        final List<UserResponse> userResponseList = userService.getAllUsers();
        log.info("Исходящий ответ: {}", userResponseList);
        return userResponseList;
    }

    @GetMapping("/{id}")
    public UserResponse findUserById(@PathVariable Long id) {
        log.info("Входящий запрос GET /users/{}.", id);
        final UserResponse userResponse = userService.getUserResponseById(id);
        log.info("Исходящий ответ: {}", userResponse);
        return userResponse;
    }

    @PostMapping
    public UserResponse create(@RequestBody UserRequest userRequest) {
        log.info("Входящий запрос POST /users: {}", userRequest);
        final UserResponse createdUserResponse = userService.createUser(userRequest);
        log.info("Исходящий ответ: {}", createdUserResponse);
        return createdUserResponse;
    }

    @PatchMapping("/{id}")
    public UserResponse update(@RequestBody UserRequest userRequest, @PathVariable Long id) {
        log.info("Входящий запрос PUT /users: {}", userRequest);
        final UserResponse updatedUserResponse = userService.updateUser(id, userRequest);
        log.info("Исходящий ответ: {}", updatedUserResponse);
        return updatedUserResponse;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Входящий запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }
}
