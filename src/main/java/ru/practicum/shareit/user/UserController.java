package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

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
        List<User> allUsers = userService.getAllUsers();
        List<UserDto> allUsersDto = UserMapper.toDtoList(allUsers);
        log.info("Исходящий ответ: {}", allUsersDto);
        return allUsersDto;
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        log.info("Входящий запрос GET /users/{}.", id);
        User userById = userService.getUserById(id);
        UserDto userByIdDto = UserMapper.toDto(userById);
        log.info("Исходящий ответ: {}", userByIdDto);
        return userByIdDto;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Входящий запрос POST /users: {}", userDto);
        User createdUser = userService.createUser(UserMapper.toEntity(userDto));
        UserDto createdUserDto = UserMapper.toDto(createdUser);
        log.info("Исходящий ответ: {}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Входящий запрос PUT /users: {}", userDto);
        User updatedUser = userService.updateUser(id, UserMapper.toEntity(userDto));
        UserDto updatedUserDto = UserMapper.toDto(updatedUser);
        log.info("Исходящий ответ: {}", updatedUserDto);
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Входящий запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

}
