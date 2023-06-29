package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.GatewayUserRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerGateway {

    @Autowired
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() { //ToDo Проверить отправку листа
        log.info("Входящий запрос GET /users.");
        final ResponseEntity<Object> userResponseList = userClient.getAllUsers();
        log.info("Исходящий ответ: {}", userResponseList);
        return userResponseList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable Long id) {
        log.info("Входящий запрос GET /users/{}.", id);
        final ResponseEntity<Object> userResponse = userClient.getUserResponseById(id);
        log.info("Исходящий ответ: {}", userResponse);
        return userResponse;
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody GatewayUserRequest gatewayUserRequest) {
        log.info("Входящий запрос POST /users: {}", gatewayUserRequest);
        final ResponseEntity<Object> createdUserResponse = userClient.createUser(gatewayUserRequest);
        log.info("Исходящий ответ: {}", createdUserResponse);
        return createdUserResponse;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody GatewayUserRequest gatewayUserRequest, @PathVariable Long id) {
        log.info("Входящий запрос PUT /users: {}", gatewayUserRequest);
        final ResponseEntity<Object> updatedUserResponse = userClient.updateUser(id, gatewayUserRequest);
        log.info("Исходящий ответ: {}", updatedUserResponse);
        return updatedUserResponse;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Входящий запрос DELETE /users/{}", userId);
        userClient.deleteUser(userId);
    }
}
