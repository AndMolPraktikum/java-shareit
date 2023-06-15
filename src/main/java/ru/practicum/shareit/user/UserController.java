package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Входящий запрос GET /users.");
        final List<UserDto> allUsersDto = userService.getAllUsersDto();
        log.info("Исходящий ответ: {}", allUsersDto);
        return allUsersDto;
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        log.info("Входящий запрос GET /users/{}.", id);
        final UserDto userByIdDto = userService.getUserDtoById(id);
        log.info("Исходящий ответ: {}", userByIdDto);
        return userByIdDto;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Входящий запрос POST /users: {}", userDto);
        final UserDto createdUserDto = userService.createUser(userDto);
        log.info("Исходящий ответ: {}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Входящий запрос PUT /users: {}", userDto);
        final UserDto updatedUserDto = userService.updateUser(id, userDto);
        log.info("Исходящий ответ: {}", updatedUserDto);
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Входящий запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }
}
