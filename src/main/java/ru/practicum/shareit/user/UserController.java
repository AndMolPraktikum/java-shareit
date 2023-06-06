package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.config.MapperUtil;
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

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Входящий запрос GET /users.");
        final List<User> allUsers = userService.getAllUsers();
        final List<UserDto> allUsersDto = MapperUtil.convertList(allUsers, this::convertToUserDto);
        log.info("Исходящий ответ: {}", allUsersDto);
        return allUsersDto;
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        log.info("Входящий запрос GET /users/{}.", id);
        final User userById = userService.getUserById(id);
        final UserDto userByIdDto = modelMapper.map(userById, UserDto.class);
        log.info("Исходящий ответ: {}", userByIdDto);
        return userByIdDto;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Входящий запрос POST /users: {}", userDto);
        final User createdUser = userService.createUser(modelMapper.map(userDto, User.class));
        final UserDto createdUserDto = modelMapper.map(createdUser, UserDto.class);
        log.info("Исходящий ответ: {}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Входящий запрос PUT /users: {}", userDto);
        final User updatedUser = userService.updateUser(id, modelMapper.map(userDto, User.class));
        final UserDto updatedUserDto = modelMapper.map(updatedUser, UserDto.class);
        log.info("Исходящий ответ: {}", updatedUserDto);
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Входящий запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
